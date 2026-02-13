package com.backendsyndicate.smashclub.auth.service;

import com.backendsyndicate.smashclub.auth.dto.request.ForgotPasswordRequest;
import com.backendsyndicate.smashclub.auth.dto.request.ResetPasswordRequest;
import com.backendsyndicate.smashclub.auth.model.PasswordResetTokens;
import com.backendsyndicate.smashclub.auth.model.User;
import com.backendsyndicate.smashclub.auth.repository.PasswordResetTokenRepository;
import com.backendsyndicate.smashclub.auth.repository.UserRepository;
import com.backendsyndicate.smashclub.common.security.PasswordHasher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailServiceImpl emailServiceImpl;
    private final PasswordHasher passwordHasher;
    private final SessionService sessionService;

    @Transactional
    public ResponseEntity<Object> forgotPassword(ForgotPasswordRequest request, HttpServletRequest httpRequest,
                                                 com.backendsyndicate.smashclub.common.handler.ResponseHandler responseHandler) {
        String ipAddress = httpRequest.getRemoteAddr();
        log.info("=== FORGOT PASSWORD START ===");
        log.info("Email: {}, IP: {}", request.getEmail(), ipAddress);

        try {
            // 1. Cari user
            Optional<User> userOpt = userRepository.findByEmail(request.getEmail().toLowerCase().trim());

            if (userOpt.isEmpty()) {
                log.info("User not found (security response)");
                return responseHandler.handleResponse(
                        "Jika email terdaftar, link reset akan dikirim",
                        HttpStatus.OK,
                        null,
                        null,
                        httpRequest
                );
            }

            User user = userOpt.get();
            log.info("User found - ID: {}, Email: {}, Status: {}",
                    user.getId(), user.getEmail(), user.getStatus());

            // 2. Cek status user
            if (user.getStatus() == com.backendsyndicate.smashclub.common.constant.AuthenticationConstant.PENDING) {
                log.warn("Account pending, cannot reset password");
                return responseHandler.handleResponse(
                        "Akun belum aktif. Silakan verifikasi email terlebih dahulu",
                        HttpStatus.BAD_REQUEST,
                        "AUTH_004",
                        null,
                        httpRequest
                );
            }

            if (user.getStatus() == com.backendsyndicate.smashclub.common.constant.AuthenticationConstant.LOCKED) {
                log.warn("Account locked, cannot reset password");
                return responseHandler.handleResponse(
                        "Akun terkunci. Tidak dapat reset password",
                        HttpStatus.BAD_REQUEST,
                        "AUTH_005",
                        null,
                        httpRequest
                );
            }

            // 3. DEBUG: Cek token yang ada
            List<PasswordResetTokens> existingTokens = passwordResetTokenRepository.findByUser(user);
            log.info("Existing tokens in DB: {}", existingTokens.size());

            // 4. Invalidate existing tokens (HANYA jika ada)
            Long activeTokens = passwordResetTokenRepository.countByUserAndUsedAtIsNull(user);
            log.info("Active tokens to invalidate: {}", activeTokens);

            if (activeTokens > 0) {
                try {
                    int invalidated = passwordResetTokenRepository.invalidateByUser(user, LocalDateTime.now());
                    log.info("Successfully invalidated {} tokens", invalidated);
                } catch (Exception e) {
                    log.error("Failed to invalidate tokens: {}", e.getMessage());
                    // Fallback to native query
                    try {
                        int invalidatedNative = passwordResetTokenRepository.invalidateUserTokens(user.getId(), LocalDateTime.now());
                        log.info("Fallback native invalidate: {} tokens", invalidatedNative);
                    } catch (Exception e2) {
                        log.warn("Native invalidate also failed: {}", e2.getMessage());
                        // Continue anyway
                    }
                }
            } else {
                log.info("No active tokens to invalidate (first time request)");
            }

            // 5. Generate new token
            String token = UUID.randomUUID().toString();
            log.info("Generated new token: {}", token);

            PasswordResetTokens resetToken = createPasswordResetToken(user, token);

            // 6. Save token
            try {
                PasswordResetTokens savedToken = passwordResetTokenRepository.save(resetToken);
                log.info("✅ Token saved successfully! ID: {}, Token: {}",
                        savedToken.getId(), savedToken.getToken());

                // Verify save
                Optional<PasswordResetTokens> verify = passwordResetTokenRepository.findByToken(token);
                log.info("Token verification after save: {}", verify.isPresent());

            } catch (Exception e) {
                log.error(" Failed to save token: {}", e.getMessage());
                log.error("Stack trace:", e);

                return responseHandler.handleResponse(
                        "Gagal membuat token reset",
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "AUTH_028",
                        null,
                        httpRequest
                );
            }

            // 7. Send email
            log.info("Sending reset password email...");
            try {
                emailServiceImpl.sendResetPasswordEmail(user.getEmail(), token);
                log.info("✅ Email sent to {}", user.getEmail());
            } catch (Exception e) {
                log.error("Failed to send email: {}", e.getMessage());
                // Continue, email is secondary
            }

            log.info("=== FORGOT PASSWORD END - SUCCESS ===");

            return responseHandler.handleResponse(
                    "Link reset password dikirim ke email",
                    HttpStatus.OK,
                    null,
                    null,
                    httpRequest
            );

        } catch (Exception e) {
            log.error("=== FORGOT PASSWORD ERROR ===");
            log.error("Unexpected error: {}", e.getMessage());
            log.error("Stack trace:", e);

            return responseHandler.handleResponse(
                    "Terjadi kesalahan internal",
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "AUTH_999",
                    null,
                    httpRequest
            );
        }
    }

    @Transactional
    public ResponseEntity<Object> resetPassword(ResetPasswordRequest request, HttpServletRequest httpRequest,
                                                com.backendsyndicate.smashclub.common.handler.ResponseHandler responseHandler) {
        log.info("Reset password attempt - token: {}", request.getToken());

        Optional<PasswordResetTokens> tokenOpt = passwordResetTokenRepository
                .findByTokenAndUsedAtIsNull(request.getToken());

        if (tokenOpt.isEmpty()) {
            log.warn("Invalid reset token - token: {}", request.getToken());
            return responseHandler.handleResponse(
                    "Token reset tidak valid",
                    HttpStatus.BAD_REQUEST,
                    "AUTH_011",
                    null,
                    httpRequest
            );
        }

        PasswordResetTokens resetToken = tokenOpt.get();

        // Cek expired
        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.warn("Expired reset token - token: {}", request.getToken());
            return responseHandler.handleResponse(
                    "Token reset telah kadaluarsa",
                    HttpStatus.BAD_REQUEST,
                    "AUTH_012",
                    null,
                    httpRequest
            );
        }

        // Validasi password baru
        if (request.getNewPassword() == null || request.getNewPassword().length() < 8) {
            return responseHandler.handleResponse(
                    "Password baru minimal 8 karakter",
                    HttpStatus.BAD_REQUEST,
                    "AUTH_001",
                    null,
                    httpRequest
            );
        }

        // Update token as used
        resetToken.setUsedAt(LocalDateTime.now());
        passwordResetTokenRepository.save(resetToken);

        // Update user password
        User user = resetToken.getUser();
        user.setPasswordHash(passwordHasher.hash(request.getNewPassword()));
        user.setUpdatedDate(LocalDateTime.now());
        user.setFailedLoginAttempt(0); // Reset failed attempts
        user.setLockedUntil(null); // Clear any lock
        userRepository.save(user);

        // Invalidate all JWT tokens (force logout from all devices)
        sessionService.invalidateAllUserTokens(user.getId());

        log.info("Password reset successfully - userId: {}, email: {}", user.getId(), user.getEmail());

        return responseHandler.handleResponse(
                "Password berhasil direset",
                HttpStatus.OK,
                null,
                null,
                httpRequest
        );
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Object> validateResetToken(String token, HttpServletRequest httpRequest,
                                                     com.backendsyndicate.smashclub.common.handler.ResponseHandler responseHandler) {
        log.info("Validate reset token - token: {}", token);

        Optional<PasswordResetTokens> tokenOpt = passwordResetTokenRepository
                .findByTokenAndUsedAtIsNull(token);

        if (tokenOpt.isEmpty()) {
            return responseHandler.handleResponse(
                    "Token reset tidak valid",
                    HttpStatus.BAD_REQUEST,
                    "AUTH_011",
                    null,
                    httpRequest
            );
        }

        PasswordResetTokens resetToken = tokenOpt.get();

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            return responseHandler.handleResponse(
                    "Token reset telah kadaluarsa",
                    HttpStatus.BAD_REQUEST,
                    "AUTH_012",
                    null,
                    httpRequest
            );
        }

        User user = resetToken.getUser();

        Map<String, Object> data = new HashMap<>();
        data.put("email", user.getEmail());
        data.put("userId", user.getId());
        data.put("expiresAt", resetToken.getExpiresAt());

        return responseHandler.handleResponse(
                "Token valid",
                HttpStatus.OK,
                null,
                data,
                httpRequest
        );
    }

    private PasswordResetTokens createPasswordResetToken(User user, String token) {
        PasswordResetTokens resetToken = new PasswordResetTokens();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiresAt(LocalDateTime.now()
                .plusHours(com.backendsyndicate.smashclub.common.constant.AuthenticationConstant.RESET_TOKEN_EXPIRY_HOURS));
        resetToken.setCreatedAt(LocalDateTime.now());
        return resetToken;
    }
}