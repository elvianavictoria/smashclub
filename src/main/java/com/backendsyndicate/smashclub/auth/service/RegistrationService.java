package com.backendsyndicate.smashclub.auth.service;

import com.backendsyndicate.smashclub.auth.dto.request.RegisterRequest;
import com.backendsyndicate.smashclub.auth.model.EmailVerificationTokens;
import com.backendsyndicate.smashclub.auth.model.User;
import com.backendsyndicate.smashclub.auth.repository.EmailVerificationTokenRepository;
import com.backendsyndicate.smashclub.auth.repository.UserRepository;
import com.backendsyndicate.smashclub.common.handler.ResponseHandler;
import com.backendsyndicate.smashclub.common.security.PasswordHasher;
import com.backendsyndicate.smashclub.common.constant.AuthenticationConstant;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    @Transactional
    public ResponseEntity<Object> register(RegisterRequest request, HttpServletRequest httpRequest,
                                           ResponseHandler responseHandler) {
        log.info("Registration attempt - email: {}", request.getEmail());

        // 1. Validasi format data
        if (!validationService.isValidRegistrationData(request)) {
            log.warn("Invalid registration data - email: {}", request.getEmail());
            return responseHandler.handleResponse(
                    "Data registrasi tidak valid",
                    HttpStatus.BAD_REQUEST,
                    "AUTH_001",
                    null,
                    httpRequest
            );
        }

        // 2. Cek email unique
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Email already exists - email: {}", request.getEmail());
            return responseHandler.handleResponse(
                    "Email sudah terdaftar",
                    HttpStatus.BAD_REQUEST,
                    "AUTH_002",
                    null,
                    httpRequest
            );
        }

        // 3. Create user dengan status PENDING
        User user = createUser(request);
        user = userRepository.saveAndFlush(user);
        log.info("User created - userId: {}, email: {}", user.getId(), user.getEmail());

        // 4. Generate dan simpan verification token
        EmailVerificationTokens verificationToken = createVerificationToken(user);
        emailVerificationTokenRepository.saveAndFlush(verificationToken);

        // 5. Kirim email verifikasi
        sendVerificationEmail(user, verificationToken.getToken());

        Map<String, Object> data = Map.of("userId", user.getId());

        return responseHandler.handleResponse(
                "Registrasi berhasil, cek email untuk verifikasi",
                HttpStatus.CREATED,
                null,
                data,
                httpRequest
        );
    }

    @Transactional
    public ResponseEntity<Object> verifyEmail(String token, HttpServletRequest httpRequest,
                                              ResponseHandler responseHandler) {
        log.info("Email verification attempt - token: {}", token);

        Optional<EmailVerificationTokens> tokenOpt = emailVerificationTokenRepository
                .findByTokenAndUsedAtIsNull(token);

        if (tokenOpt.isEmpty()) {
            log.warn("Invalid verification token - token: {}", token);
            return responseHandler.handleResponse(
                    "Token verifikasi tidak valid",
                    HttpStatus.BAD_REQUEST,
                    "AUTH_009",
                    null,
                    httpRequest
            );
        }

        EmailVerificationTokens verificationToken = tokenOpt.get();

        // Cek expired
        if (verificationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.warn("Expired verification token - token: {}", token);
            return responseHandler.handleResponse(
                    "Token verifikasi telah kadaluarsa",
                    HttpStatus.BAD_REQUEST,
                    "AUTH_010",
                    null,
                    httpRequest
            );
        }

        // Update token as used
        verificationToken.setUsedAt(LocalDateTime.now());
        emailVerificationTokenRepository.save(verificationToken);

        // Update user status to ACTIVE
        User user = verificationToken.getUser();
        user.setStatus(AuthenticationConstant.ACTIVE);
        user.setUpdatedDate(LocalDateTime.now());
        userRepository.save(user);

        log.info("Email verified successfully - userId: {}, email: {}", user.getId(), user.getEmail());

        return responseHandler.handleResponse(
                "Email berhasil diverifikasi",
                HttpStatus.OK,
                null,
                null,
                httpRequest
        );
    }

    @Transactional
    public ResponseEntity<Object> resendVerificationEmail(String email, HttpServletRequest httpRequest,
                                                          ResponseHandler responseHandler) {
        String ipAddress = httpRequest.getRemoteAddr();
        log.info("Resend verification email request - email: {}, IP: {}", email, ipAddress);

        Optional<User> userOpt = userRepository.findByEmail(email.toLowerCase().trim());

        if (userOpt.isEmpty()) {
            log.warn("Email not found for resend - email: {}", email);
            return responseHandler.handleResponse(
                    "Email tidak ditemukan",
                    HttpStatus.NOT_FOUND,
                    "AUTH_013",
                    null,
                    httpRequest
            );
        }

        User user = userOpt.get();

        if (user.getStatus() != AuthenticationConstant.PENDING) {
            log.warn("Resend blocked - account not pending - email: {}, status: {}", email, user.getStatus());
            return responseHandler.handleResponse(
                    "Akun sudah aktif atau tidak memerlukan verifikasi",
                    HttpStatus.BAD_REQUEST,
                    "AUTH_014",
                    null,
                    httpRequest
            );
        }

        // Cek kapan terakhir dikirim (prevent spam)
        Optional<EmailVerificationTokens> lastToken = emailVerificationTokenRepository
                .findTopByUserOrderByCreatedAtDesc(user);

        if (lastToken.isPresent()) {
            LocalDateTime lastSent = lastToken.get().getCreatedAt();
            java.time.Duration timeSinceLast = java.time.Duration.between(lastSent, LocalDateTime.now());

            if (timeSinceLast.toMinutes() < 1) { // Minimal 1 menit antar request
                log.warn("Resend too soon - email: {}, lastSent: {}", email, lastSent);
                return responseHandler.handleResponse(
                        "Silakan tunggu 1 menit sebelum meminta email verifikasi lagi",
                        HttpStatus.BAD_REQUEST,
                        "AUTH_020",
                        null,
                        httpRequest
                );
            }
        }

        // Invalidate existing tokens
        emailVerificationTokenRepository.invalidateUserTokens(user.getId(), LocalDateTime.now());

        // Generate new token
        String token = UUID.randomUUID().toString();
        EmailVerificationTokens verificationToken = createVerificationToken(user, token);
        emailVerificationTokenRepository.save(verificationToken);

        // Kirim email
        try {
            emailServiceImpl.sendActivationEmail(user.getEmail(), token);
            log.info("Verification email resent - email: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to resend verification email - email: {}, error: {}", user.getEmail(), e.getMessage());
            return responseHandler.handleResponse(
                    "Gagal mengirim email verifikasi. Silakan coba lagi nanti.",
                    HttpStatus.BAD_REQUEST,
                    "AUTH_021",
                    null,
                    httpRequest
            );
        }

        return responseHandler.handleResponse(
                "Email verifikasi telah dikirim ulang",
                HttpStatus.OK,
                null,
                null,
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

    // RegistrationService.java
    private EmailVerificationTokens createVerificationToken(User user, String token) {
        return EmailVerificationTokens.builder()
                .user(user)
                .token(token)
                .expiresAt(LocalDateTime.now()
                        .plusHours(AuthenticationConstant.VERIFICATION_TOKEN_EXPIRY_HOURS))
                .createdAt(LocalDateTime.now())
                .build();  // ← usedAt otomatis null (default)
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