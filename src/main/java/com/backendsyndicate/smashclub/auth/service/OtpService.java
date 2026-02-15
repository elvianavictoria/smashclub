package com.backendsyndicate.smashclub.auth.service;

import com.backendsyndicate.smashclub.auth.dto.request.OtpVerificationRequest;
import com.backendsyndicate.smashclub.auth.dto.request.ResendOtpRequest;
import com.backendsyndicate.smashclub.common.handler.ResponseHandler;
import com.backendsyndicate.smashclub.common.constant.AuthenticationConstant;
import com.backendsyndicate.smashclub.auth.model.LoginOtpTokens;
import com.backendsyndicate.smashclub.auth.model.User;
import com.backendsyndicate.smashclub.auth.repository.LoginOtpTokenRepository;
import com.backendsyndicate.smashclub.auth.repository.UserRepository;
import com.backendsyndicate.smashclub.common.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {

    private final LoginOtpTokenRepository loginOtpTokenRepository;
    private final UserRepository userRepository;
    private final EmailServiceImpl emailServiceImpl;
    private final TokenService tokenService;
    private final JwtService jwtService;

    public ResponseEntity<Object> generateAndSendOtp(User user, String ipAddress,
                                                     ResponseHandler responseHandler,
                                                     HttpServletRequest httpRequest) {
        // Generate OTP untuk 2FA
        String otp = generateSecureOtp();
        LoginOtpTokens otpToken = createOtpToken(user, otp);

        // Invalidate existing OTP tokens for this user
        loginOtpTokenRepository.invalidateUserTokens(user.getId(), LocalDateTime.now());
        loginOtpTokenRepository.save(otpToken);

        // Kirim OTP email
        try {
            emailServiceImpl.sendOtpEmail(user.getEmail(), otp);
            log.info("OTP sent - userId: {}, email: {}, IP: {}", user.getId(), user.getEmail(), ipAddress);
        } catch (Exception e) {
            log.error("Failed to send OTP email - email: {}, error: {}", user.getEmail(), e.getMessage());
            // Lanjutkan proses, user bisa request OTP ulang nanti
        }

        // Return response
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("userId", user.getId());
        responseData.put("email", user.getEmail());
        responseData.put("requiresOtp", true);
        responseData.put("otpExpiresIn", AuthenticationConstant.OTP_EXPIRY_MINUTES);

        log.info("Login successful (awaiting OTP) - userId: {}, email: {}, IP: {}",
                user.getId(), user.getEmail(), ipAddress);

        return responseHandler.handleResponse(
                "OTP telah dikirim ke email Anda",
                HttpStatus.OK,
                null,
                responseData,
                httpRequest
        );
    }

    @Transactional
    public ResponseEntity<Object> verifyOtp(OtpVerificationRequest request, HttpServletRequest httpRequest,
                                            ResponseHandler responseHandler) {
        String ipAddress = httpRequest.getRemoteAddr();
        log.info("OTP verification attempt - userId: {}, IP: {}", request.getUserId(), ipAddress);

        // 1. Cari OTP yang belum digunakan
        Optional<LoginOtpTokens> otpOpt = loginOtpTokenRepository
                .findByUserIdAndOtpCodeAndUsedAtIsNull(request.getUserId(), request.getOtp());

        if (otpOpt.isEmpty()) {
            // Cek apakah OTP sudah digunakan
            Optional<LoginOtpTokens> usedOtp = loginOtpTokenRepository
                    .findByUserIdAndOtpCodeAndUsedAtIsNotNull(request.getUserId(), request.getOtp());

            if (usedOtp.isPresent()) {
                log.warn("OTP already used - userId: {}, IP: {}", request.getUserId(), ipAddress);
                return responseHandler.handleResponse(
                        "OTP sudah digunakan",
                        HttpStatus.BAD_REQUEST,
                        "AUTH_017",
                        null,
                        httpRequest
                );
            }

            log.warn("Invalid OTP - userId: {}, IP: {}", request.getUserId(), ipAddress);
            return responseHandler.handleResponse(
                    "OTP tidak valid",
                    HttpStatus.BAD_REQUEST,
                    "AUTH_007",
                    null,
                    httpRequest
            );
        }

        LoginOtpTokens otpToken = otpOpt.get();

        // 2. Cek expired
        if (otpToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.warn("Expired OTP - userId: {}, IP: {}", request.getUserId(), ipAddress);
            return responseHandler.handleResponse(
                    "OTP telah kadaluarsa",
                    HttpStatus.BAD_REQUEST,
                    "AUTH_008",
                    null,
                    httpRequest
            );
        }

        // 3. Cek apakah user masih ACTIVE
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> {
                    log.error("User not found during OTP verification - userId: {}", request.getUserId());
                    return new RuntimeException("User tidak ditemukan");
                });

        if (user.getStatus() != AuthenticationConstant.ACTIVE) {
            log.warn("User not active during OTP verification - userId: {}, status: {}",
                    request.getUserId(), user.getStatus());
            return responseHandler.handleResponse(
                    "Akun tidak aktif",
                    HttpStatus.BAD_REQUEST,
                    "AUTH_018",
                    null,
                    httpRequest
            );
        }

        // 4. Mark OTP as used
        otpToken.setUsedAt(LocalDateTime.now());
        loginOtpTokenRepository.save(otpToken);

        // 5. Delegate to TokenService untuk generate JWT
        return tokenService.generateJwtTokensAfterOtpVerification(user, responseHandler, httpRequest);
    }

    @Transactional
    public ResponseEntity<Object> resendOtp(ResendOtpRequest request, HttpServletRequest httpRequest,
                                            ResponseHandler responseHandler) {
        String ipAddress = httpRequest.getRemoteAddr();
        log.info("Resend OTP request - userId: {}, IP: {}", request.getUserId(), ipAddress);

        // Cek user
        Optional<User> userOpt = userRepository.findById(request.getUserId());
        if (userOpt.isEmpty()) {
            return responseHandler.handleResponse(
                    "User tidak ditemukan",
                    HttpStatus.NOT_FOUND,
                    "AUTH_013",
                    null,
                    httpRequest
            );
        }

        User user = userOpt.get();

        // Cek user status
        if (user.getStatus() != AuthenticationConstant.ACTIVE) {
            return responseHandler.handleResponse(
                    "Akun tidak aktif",
                    HttpStatus.BAD_REQUEST,
                    "AUTH_018",
                    null,
                    httpRequest
            );
        }

        // Cek rate limiting untuk resend OTP
        Optional<LoginOtpTokens> lastOtpOpt = loginOtpTokenRepository
                .findTopByUserOrderByCreatedAtDesc(user);

        if (lastOtpOpt.isPresent()) {
            LoginOtpTokens lastOtp = lastOtpOpt.get();
            Duration timeSinceLast = Duration.between(lastOtp.getCreatedAt(), LocalDateTime.now());

            if (timeSinceLast.toSeconds() < 60) { // Minimal 60 detik antar request
                long waitSeconds = 60 - timeSinceLast.toSeconds();

                Map<String, Object> data = new HashMap<>();
                data.put("waitSeconds", waitSeconds);
                data.put("userId", user.getId());

                return responseHandler.handleResponse(
                        String.format("Silakan tunggu %d detik sebelum meminta OTP lagi", waitSeconds),
                        HttpStatus.BAD_REQUEST,
                        "AUTH_022",
                        data,
                        httpRequest
                );
            }
        }

        // Invalidate existing OTP tokens
        loginOtpTokenRepository.invalidateUserTokens(user.getId(), LocalDateTime.now());

        // Generate new OTP
        String otp = generateSecureOtp();
        LoginOtpTokens otpToken = createOtpToken(user, otp);
        loginOtpTokenRepository.save(otpToken);

        // Kirim email
        try {
            emailServiceImpl.sendOtpEmail(user.getEmail(), otp);
            log.info("OTP resent - userId: {}, email: {}, IP: {}", user.getId(), user.getEmail(), ipAddress);
        } catch (Exception e) {
            log.error("Failed to resend OTP email - email: {}, error: {}", user.getEmail(), e.getMessage());
            return responseHandler.handleResponse(
                    "Gagal mengirim OTP ke email. Silakan coba lagi nanti.",
                    HttpStatus.BAD_REQUEST,
                    "AUTH_019",
                    null,
                    httpRequest
            );
        }

        // Response
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("userId", user.getId());
        responseData.put("email", user.getEmail());
        responseData.put("otpExpiresIn", AuthenticationConstant.OTP_EXPIRY_MINUTES);
        responseData.put("nextResendAvailableIn", 60); // Bisa resend setelah 60 detik

        return responseHandler.handleResponse(
                "OTP baru telah dikirim ke email Anda",
                HttpStatus.OK,
                null,
                responseData,
                httpRequest
        );
    }

    private LoginOtpTokens createOtpToken(User user, String otp) {
        LoginOtpTokens otpToken = new LoginOtpTokens();
        otpToken.setUser(user);
        otpToken.setOtpCode(otp);
        otpToken.setExpiresAt(LocalDateTime.now()
                .plusMinutes(AuthenticationConstant.OTP_EXPIRY_MINUTES));
        otpToken.setCreatedAt(LocalDateTime.now());
        return otpToken;
    }

    private String generateSecureOtp() {
        java.security.SecureRandom random = new java.security.SecureRandom();
        // 100000 - 999999 (6 digit, tidak ada leading zero)
        int num = 100000 + random.nextInt(900000);
        return String.valueOf(num);  // Tidak perlu format, sudah 6 digit
    }
}