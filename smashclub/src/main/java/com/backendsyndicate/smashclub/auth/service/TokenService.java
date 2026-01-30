package com.backendsyndicate.smashclub.auth.service;

import com.backendsyndicate.smashclub.auth.model.*;
import com.backendsyndicate.smashclub.auth.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {

    private final EmailVerificationTokenRepository emailVerificationRepo;
    private final PasswordResetTokenRepository passwordResetRepo;
    private final LoginOtpTokenRepository loginOtpRepo;

    // In-memory rate limiting (simple implementation)
    private final ConcurrentHashMap<String, LocalDateTime> otpRequestCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, LocalDateTime> resetRequestCache = new ConcurrentHashMap<>();

    private static final int OTP_RATE_LIMIT_MINUTES = 1;
    private static final int RESET_PASSWORD_RATE_LIMIT_MINUTES = 5;
    private static final int MAX_OTP_REQUESTS_PER_MINUTE = 3;

    // ---------------- Email Verification ----------------
    @Transactional
    public EmailVerificationToken generateEmailVerificationToken(String userId) {
        // Delete only unused tokens
        emailVerificationRepo.deleteUnusedByUserId(userId, LocalDateTime.now());

        EmailVerificationTokens token = new EmailVerificationTokens();
        token.setId(UUID.randomUUID().toString());
        token.setUserId(userId);
        token.setToken(UUID.randomUUID().toString());
        token.setCreatedDate(LocalDateTime.now());
        token.setExpiredDate(LocalDateTime.now().plusHours(24));

        return emailVerificationRepo.save(token);
    }

    public Optional<EmailVerificationToken> getEmailVerificationToken(String token) {
        return emailVerificationRepo.findByToken(token);
    }

    @Transactional
    public void markEmailVerificationTokenAsUsed(String token) {
        emailVerificationRepo.findByToken(token).ifPresent(t -> {
            t.setUsedDate(LocalDateTime.now());
            emailVerificationRepo.save(t);
        });
    }

    public boolean validateEmailVerificationToken(String token) {
        Optional<EmailVerificationToken> tokenOpt = getEmailVerificationToken(token);
        if (tokenOpt.isEmpty()) return false;

        EmailVerificationToken evToken = tokenOpt.get();
        return evToken.getUsedDate() == null &&
                evToken.getExpiredDate().isAfter(LocalDateTime.now());
    }

    // ---------------- Password Reset ----------------
    @Transactional
    public PasswordResetToken generatePasswordResetToken(String userId) {
        // Delete only unused tokens
        passwordResetRepo.deleteUnusedByUserId(userId, LocalDateTime.now());

        PasswordResetToken token = new PasswordResetToken();
        token.setId(UUID.randomUUID().toString());
        token.setUserId(userId);
        token.setToken(UUID.randomUUID().toString());
        token.setCreatedDate(LocalDateTime.now());
        token.setExpiredDate(LocalDateTime.now().plusHours(1));

        return passwordResetRepo.save(token);
    }

    public Optional<PasswordResetToken> getPasswordResetToken(String token) {
        return passwordResetRepo.findByToken(token);
    }

    @Transactional
    public void markPasswordResetTokenAsUsed(String token) {
        passwordResetRepo.findByToken(token).ifPresent(t -> {
            t.setUsedDate(LocalDateTime.now());
            passwordResetRepo.save(t);
        });
    }

    public boolean validatePasswordResetToken(String token) {
        Optional<PasswordResetToken> tokenOpt = getPasswordResetToken(token);
        if (tokenOpt.isEmpty()) return false;

        PasswordResetToken resetToken = tokenOpt.get();
        return resetToken.getUsedDate() == null &&
                resetToken.getExpiredDate().isAfter(LocalDateTime.now());
    }

    // Simple in-memory rate limiting for password reset
    public boolean isResetPasswordRateLimited(String userId) {
        String key = "reset_" + userId;
        LocalDateTime lastRequest = resetRequestCache.get(key);

        if (lastRequest == null) {
            resetRequestCache.put(key, LocalDateTime.now());
            return false;
        }

        if (lastRequest.plusMinutes(RESET_PASSWORD_RATE_LIMIT_MINUTES).isAfter(LocalDateTime.now())) {
            return true;
        }

        resetRequestCache.put(key, LocalDateTime.now());
        return false;
    }

    // ---------------- OTP ----------------
    @Transactional
    public LoginOtpToken generateOtp(String userId, int expiryMinutes) {
        // Delete only unused OTPs
        loginOtpRepo.deleteUnusedByUserId(userId, LocalDateTime.now());

        LoginOtpToken otp = new LoginOtpToken();
        otp.setId(UUID.randomUUID().toString());
        otp.setUserId(userId);
        otp.setOtpCode(generateRandomOtp());
        otp.setCreatedDate(LocalDateTime.now());
        otp.setExpiredDate(LocalDateTime.now().plusMinutes(expiryMinutes));

        return loginOtpRepo.save(otp);
    }

    @Transactional(readOnly = true)
    public Optional<LoginOtpToken> getOtpToken(String userId, String otpCode) {
        return loginOtpRepo.findByUserIdAndOtpCode(userId, otpCode);
    }

    @Transactional
    public boolean validateOtp(String userId, String otpCode) {
        Optional<LoginOtpToken> otpOpt = loginOtpRepo.findByUserIdAndOtpCode(userId, otpCode);
        if (otpOpt.isEmpty()) {
            return false;
        }

        LoginOtpToken otp = otpOpt.get();

        // Check if already used
        if (otp.getUsedDate() != null) {
            return false;
        }

        // Check if expired
        if (otp.getExpiredDate().isBefore(LocalDateTime.now())) {
            return false;
        }

        // Mark as used
        otp.setUsedDate(LocalDateTime.now());
        loginOtpRepo.save(otp);
        return true;
    }

    @Transactional
    public boolean verifyAndConsumeOtp(String userId, String otpCode) {
        return validateOtp(userId, otpCode);
    }

    // Simple in-memory rate limiting for OTP
    public boolean isOtpRateLimited(String userId) {
        String key = "otp_" + userId;
        LocalDateTime lastRequest = otpRequestCache.get(key);

        if (lastRequest == null) {
            otpRequestCache.put(key, LocalDateTime.now());
            return false;
        }

        if (lastRequest.plusMinutes(OTP_RATE_LIMIT_MINUTES).isAfter(LocalDateTime.now())) {
            return true;
        }

        otpRequestCache.put(key, LocalDateTime.now());
        return false;
    }

    // Cleanup in-memory cache setiap jam
    @Scheduled(cron = "0 0 * * * *")
    public void cleanupRateLimitCache() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(Math.max(
                OTP_RATE_LIMIT_MINUTES, RESET_PASSWORD_RATE_LIMIT_MINUTES
        ));

        otpRequestCache.entrySet().removeIf(entry ->
                entry.getValue().isBefore(cutoff));
        resetRequestCache.entrySet().removeIf(entry ->
                entry.getValue().isBefore(cutoff));

        log.info("Rate limit cache cleaned up. OTP cache: {}, Reset cache: {}",
                otpRequestCache.size(), resetRequestCache.size());
    }

    // ---------------- Utility ----------------
    private String generateRandomOtp() {
        // Secure random OTP generation
        return String.format("%06d", (int)(Math.random() * 1_000_000));
    }

    // Cleanup expired tokens setiap jam
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void cleanupExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();

        int emailTokens = emailVerificationRepo.deleteExpiredTokens(now);
        int resetTokens = passwordResetRepo.deleteExpiredTokens(now);
        int otpTokens = loginOtpRepo.deleteExpiredTokens(now);

        if (emailTokens > 0 || resetTokens > 0 || otpTokens > 0) {
            log.info("Cleaned up expired tokens: {} email, {} reset, {} OTP",
                    emailTokens, resetTokens, otpTokens);
        }
    }

    // Additional cleanup untuk OTP lebih sering (setiap 30 menit)
    @Scheduled(cron = "0 */30 * * * *")
    @Transactional
    public void cleanupExpiredOtps() {
        LocalDateTime now = LocalDateTime.now();
        int deleted = loginOtpRepo.deleteExpiredTokens(now);
        if (deleted > 0) {
            log.debug("Cleaned up {} expired OTPs", deleted);
        }
    }
}