package com.backendsyndicate.smashclub.auth.service;

import com.backendsyndicate.smashclub.auth.dto.request.RegisterRequest;
import com.backendsyndicate.smashclub.auth.model.EmailVerificationTokens;
import com.backendsyndicate.smashclub.auth.model.User;
import com.backendsyndicate.smashclub.auth.repository.EmailVerificationTokenRepository;
import com.backendsyndicate.smashclub.auth.repository.UserRepository;
import com.backendsyndicate.smashclub.common.util.ValidationError;
import com.backendsyndicate.smashclub.common.handler.ResponseHandler;
import com.backendsyndicate.smashclub.common.security.PasswordHasher;
import com.backendsyndicate.smashclub.common.constant.AuthenticationConstant;
import com.backendsyndicate.smashclub.ecommerce.service.CartService;
import com.backendsyndicate.smashclub.payment.service.WalletService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationService {

    private final UserRepository userRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final PasswordHasher passwordHasher;
    private final EmailServiceImpl emailServiceImpl;
    private final ValidationService validationService;
    private final ResponseHandler responseHandler;

    @Autowired
    private WalletService walletService;
    @Autowired
    private CartService cartService;

    @Transactional
    public ResponseEntity<Object> register(RegisterRequest request, HttpServletRequest httpRequest) {
        log.info("Registration attempt - email: {}", request.getEmail());

        // Validasi data registrasi (format, panjang, kompleksitas)
        List<ValidationError> validationErrors = validationService.validateRegistrationData(request);

        if (!validationErrors.isEmpty()) {
            log.warn("Invalid registration data - email: {}, errors: {}",
                    request.getEmail(), validationErrors.size());

            Map<String, Object> errorData = new HashMap<>();
            errorData.put("errors", validationErrors);

            return responseHandler.handleResponse(
                    "Data registrasi tidak valid",
                    HttpStatus.BAD_REQUEST,
                    "AUTH_001",
                    errorData,
                    httpRequest
            );
        }

        // Cek keunikan email di database
        if (userRepository.existsByEmail(request.getEmail().toLowerCase().trim())) {
            log.warn("Email already exists - email: {}", request.getEmail());

            Map<String, Object> errorData = new HashMap<>();
            errorData.put("field", "email");
            errorData.put("reason", "already_exists");

            return responseHandler.handleResponse(
                    "Email sudah terdaftar",
                    HttpStatus.BAD_REQUEST,
                    "AUTH_002",
                    errorData,
                    httpRequest
            );
        }

        // Buat user baru dengan status PENDING (menunggu verifikasi email)
        User user = createUser(request);
        user = userRepository.saveAndFlush(user);
        log.info("User created - userId: {}, email: {}", user.getId(), user.getEmail());

        // Generate token verifikasi email (berlaku 24 jam)
        EmailVerificationTokens verificationToken = createVerificationToken(user);
        emailVerificationTokenRepository.saveAndFlush(verificationToken);

        // Kirim email verifikasi ke user
        sendVerificationEmail(user, verificationToken.getToken());

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("userId", user.getId());
        responseData.put("email", user.getEmail());

        return responseHandler.handleResponse(
                "Registrasi berhasil, cek email untuk verifikasi",
                HttpStatus.CREATED,
                null,
                responseData,
                httpRequest
        );
    }

    @Transactional
    public ResponseEntity<Object> verifyEmail(String token, HttpServletRequest httpRequest) {
        log.info("Email verification attempt - token: {}", token);

        Optional<EmailVerificationTokens> tokenOpt = emailVerificationTokenRepository
                .findByTokenAndUsedAtIsNull(token);

        if (tokenOpt.isEmpty()) {
            log.warn("Invalid verification token - token: {}", token);

            Map<String, Object> errorData = new HashMap<>();
            errorData.put("token", token);
            errorData.put("reason", "invalid_or_used");

            return responseHandler.handleResponse(
                    "Token verifikasi tidak valid",
                    HttpStatus.BAD_REQUEST,
                    "AUTH_009",
                    errorData,
                    httpRequest
            );
        }

        EmailVerificationTokens verificationToken = tokenOpt.get();

        // Cek masa berlaku token (24 jam sejak dibuat)
        if (verificationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.warn("Expired verification token - token: {}", token);

            Map<String, Object> errorData = new HashMap<>();
            errorData.put("token", token);
            errorData.put("expiredAt", verificationToken.getExpiresAt().toString());

            return responseHandler.handleResponse(
                    "Token verifikasi telah kadaluarsa",
                    HttpStatus.BAD_REQUEST,
                    "AUTH_010",
                    errorData,
                    httpRequest
            );
        }

        // Tandai token sudah digunakan
        verificationToken.setUsedAt(LocalDateTime.now());
        emailVerificationTokenRepository.save(verificationToken);

        // Aktifkan user
        User user = verificationToken.getUser();
        user.setStatus(AuthenticationConstant.ACTIVE);
        user.setUpdatedDate(LocalDateTime.now());
        userRepository.save(user);

        cartService.getOrCreateActiveCart(user.getId());
        walletService.createWallet(user.getId());

        log.info("Email verified successfully - userId: {}, email: {}", user.getId(), user.getEmail());

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("userId", user.getId());
        responseData.put("email", user.getEmail());
        responseData.put("verifiedAt", LocalDateTime.now().toString());

        return responseHandler.handleResponse(
                "Email berhasil diverifikasi",
                HttpStatus.OK,
                null,
                responseData,
                httpRequest
        );
    }

    @Transactional
    public ResponseEntity<Object> resendVerificationEmail(String email, HttpServletRequest httpRequest) {
        String ipAddress = httpRequest.getRemoteAddr();
        log.info("Resend verification email request - email: {}, IP: {}", email, ipAddress);

        Optional<User> userOpt = userRepository.findByEmail(email.toLowerCase().trim());

        if (userOpt.isEmpty()) {
            log.warn("Email not found for resend - email: {}", email);

            Map<String, Object> errorData = new HashMap<>();
            errorData.put("email", email);

            return responseHandler.handleResponse(
                    "Email tidak ditemukan",
                    HttpStatus.NOT_FOUND,
                    "AUTH_013",
                    errorData,
                    httpRequest
            );
        }

        User user = userOpt.get();

        // Hanya user dengan status PENDING yang boleh minta verifikasi ulang
        if (user.getStatus() != AuthenticationConstant.PENDING) {
            log.warn("Resend blocked - account not pending - email: {}, status: {}", email, user.getStatus());

            Map<String, Object> errorData = new HashMap<>();
            errorData.put("email", email);
            errorData.put("status", user.getStatus());

            return responseHandler.handleResponse(
                    "Akun sudah aktif atau tidak memerlukan verifikasi",
                    HttpStatus.BAD_REQUEST,
                    "AUTH_014",
                    errorData,
                    httpRequest
            );
        }

        // Cegah spam: minimal 1 menit antar pengiriman
        Optional<EmailVerificationTokens> lastToken = emailVerificationTokenRepository
                .findTopByUserOrderByCreatedAtDesc(user);

        if (lastToken.isPresent()) {
            LocalDateTime lastSent = lastToken.get().getCreatedAt();
            Duration timeSinceLast = Duration.between(lastSent, LocalDateTime.now());

            if (timeSinceLast.toMinutes() < 1) {
                log.warn("Resend too soon - email: {}, lastSent: {}", email, lastSent);

                Map<String, Object> errorData = new HashMap<>();
                errorData.put("email", email);
                errorData.put("lastSent", lastSent.toString());
                errorData.put("waitSeconds", 60 - timeSinceLast.toSeconds());

                return responseHandler.handleResponse(
                        "Silakan tunggu 1 menit sebelum meminta email verifikasi lagi",
                        HttpStatus.BAD_REQUEST,
                        "AUTH_020",
                        errorData,
                        httpRequest
                );
            }
        }

        // Nonaktifkan token-token lama
        emailVerificationTokenRepository.invalidateUserTokens(user.getId(), LocalDateTime.now());

        // Generate token baru
        String token = UUID.randomUUID().toString();
        EmailVerificationTokens verificationToken = createVerificationToken(user, token);
        emailVerificationTokenRepository.save(verificationToken);

        // Kirim ulang email verifikasi
        try {
            emailServiceImpl.sendActivationEmail(user.getEmail(), token);
            log.info("Verification email resent - email: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to resend verification email - email: {}, error: {}", user.getEmail(), e.getMessage());

            Map<String, Object> errorData = new HashMap<>();
            errorData.put("email", user.getEmail());

            return responseHandler.handleResponse(
                    "Gagal mengirim email verifikasi. Silakan coba lagi nanti.",
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "AUTH_021",
                    errorData,
                    httpRequest
            );
        }

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("email", user.getEmail());
        responseData.put("nextResendAvailableIn", 60);

        return responseHandler.handleResponse(
                "Email verifikasi telah dikirim ulang",
                HttpStatus.OK,
                null,
                responseData,
                httpRequest
        );
    }

    private User createUser(RegisterRequest request) {
        User user = new User();
        user.setFullName(request.getFullName().trim());
        user.setEmail(request.getEmail().toLowerCase().trim());
        user.setPasswordHash(passwordHasher.hash(request.getPassword()));
        user.setStatus(AuthenticationConstant.PENDING);
        user.setFailedLoginAttempt(0);
        user.setCreatedDate(LocalDateTime.now());
        return user;
    }

    private EmailVerificationTokens createVerificationToken(User user) {
        return createVerificationToken(user, UUID.randomUUID().toString());
    }

    private EmailVerificationTokens createVerificationToken(User user, String token) {
        return EmailVerificationTokens.builder()
                .user(user)
                .token(token)
                .expiresAt(LocalDateTime.now()
                        .plusHours(AuthenticationConstant.VERIFICATION_TOKEN_EXPIRY_HOURS))
                .createdAt(LocalDateTime.now())
                .build();
    }

    private void sendVerificationEmail(User user, String token) {
        try {
            emailServiceImpl.sendActivationEmail(user.getEmail(), token);
            log.info("Verification email sent - email: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send verification email - email: {}, error: {}", user.getEmail(), e.getMessage());
        }
    }
}