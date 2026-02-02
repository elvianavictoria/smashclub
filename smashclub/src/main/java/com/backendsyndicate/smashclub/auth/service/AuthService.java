package com.backendsyndicate.smashclub.auth.service;

import com.backendsyndicate.smashclub.auth.dto.ForgotPasswordRequest;
import com.backendsyndicate.smashclub.auth.dto.request.LoginRequest;
import com.backendsyndicate.smashclub.auth.dto.RegisterRequest;
import com.backendsyndicate.smashclub.auth.dto.request.OtpVerificationRequest;
import com.backendsyndicate.smashclub.auth.dto.request.ResendOtpRequest;
import com.backendsyndicate.smashclub.auth.dto.request.ResetPasswordRequest;
import com.backendsyndicate.smashclub.auth.dto.response.LoginResponse;
import com.backendsyndicate.smashclub.auth.dto.response.SessionResponse;
import com.backendsyndicate.smashclub.common.constant.AuthenticationConstant;
import com.backendsyndicate.smashclub.common.handler.ResponseHandler;
import com.backendsyndicate.smashclub.common.security.JwtService;
import com.backendsyndicate.smashclub.common.security.PasswordHasher;
import com.backendsyndicate.smashclub.auth.model.*;
import com.backendsyndicate.smashclub.auth.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final LoginOtpTokenRepository loginOtpTokenRepository;
    private final SessionRepository sessionRepository;
    private final com.backendsyndicate.smashclub.auth.service.impl.EmailServiceImpl emailServiceImpl;
    private final PasswordHasher passwordHasher;
    private final JwtService jwtService;
    private final ResponseHandler responseHandler;

    /*// Configuration
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCK_DURATION_MINUTES = 30;
    private static final int OTP_EXPIRY_MINUTES = 10;
    private static final int VERIFICATION_TOKEN_EXPIRY_HOURS = 24;
    private static final int RESET_TOKEN_EXPIRY_HOURS = 1;
    private static final int ACCESS_TOKEN_EXPIRY_HOURS = 24;
    private static final int REFRESH_TOKEN_EXPIRY_DAYS = 7;

    // Token Types
    private static final String TOKEN_TYPE_ACCESS = "ACCESS";
    private static final String TOKEN_TYPE_REFRESH = "REFRESH";*/

    // ============ REGISTRATION ============
    @Transactional
    public ResponseEntity<Object> register(RegisterRequest request, HttpServletRequest httpRequest) {
        log.info("Registration attempt - email: {}", request.getEmail());

        // 1. Validasi format data
        if (!isValidRegistrationData(request)) {
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
        User user = new User();
        //user.setId(UUID.randomUUID().toString());
        user.setFullName(request.getFullName().trim());
        user.setEmail(request.getEmail().toLowerCase().trim());
        user.setPasswordHash(passwordHasher.hash(request.getPassword()));
        user.setStatus(AuthenticationConstant.PENDING);
        user.setFailedLoginAttempt(0);
        user.setCreatedDate(LocalDateTime.now());

        user = userRepository.saveAndFlush(user);
        log.info("User created - userId: {}, email: {}", user.getId(), user.getEmail());

        // 4. Generate dan simpan verification token
        String token = UUID.randomUUID().toString();
        EmailVerificationTokens verificationToken = new EmailVerificationTokens();
        //verificationToken.setId(UUID.randomUUID().toString());
        verificationToken.setUser(user);
        verificationToken.setToken(token);
        verificationToken.setExpiresAt(LocalDateTime.now().plusHours(AuthenticationConstant.VERIFICATION_TOKEN_EXPIRY_HOURS));
        verificationToken.setCreatedAt(LocalDateTime.now());

        emailVerificationTokenRepository.saveAndFlush(verificationToken);

        // 5. Kirim email verifikasi
        try {
            emailServiceImpl.sendActivationEmail(user.getEmail(), token);
            log.info("Verification email sent - email: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send verification email - email: {}, error: {}", user.getEmail(), e.getMessage());
            // Tetap return success karena user sudah dibuat
        }

        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());

        return responseHandler.handleResponse(
                "Registrasi berhasil, cek email untuk verifikasi",
                HttpStatus.CREATED,
                null,
                data,
                httpRequest
        );
    }

    // ============ LOGIN ============
    @Transactional
    public ResponseEntity<Object> login(LoginRequest request, HttpServletRequest httpRequest) {
        String ipAddress = httpRequest.getRemoteAddr();
        String userAgent = httpRequest.getHeader("User-Agent");
        log.info("Login attempt - email: {}, IP: {}", request.getEmail(), ipAddress);

        // 1. Validasi input
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            return responseHandler.handleResponse(
                    "Email harus diisi",
                    HttpStatus.BAD_REQUEST,
                    "AUTH_001",
                    null,
                    httpRequest
            );
        }
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            return responseHandler.handleResponse(
                    "Password harus diisi",
                    HttpStatus.BAD_REQUEST,
                    "AUTH_001",
                    null,
                    httpRequest
            );
        }

        // 2. Cari user berdasarkan email
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail().toLowerCase().trim());

        if (userOpt.isEmpty()) {
            log.warn("User not found - email: {}, IP: {}", request.getEmail(), ipAddress);
            return responseHandler.handleResponse(
                    "Email atau password salah",
                    HttpStatus.BAD_REQUEST,
                    "AUTH_003",
                    null,
                    httpRequest
            );
        }

        User user = userOpt.get();

        // 3. LANGSUNG VERIFIKASI PASSWORD
        boolean passwordValid = passwordHasher.verify(request.getPassword(), user.getPasswordHash());

        // 4. Jika PASSWORD SALAH
        if (!passwordValid) {
            return handleFailedLogin(user, ipAddress, httpRequest);
        }

        // 5. PASSWORD BENAR → sekarang cek status lainnya
        return handleSuccessfulPasswordVerification(user, ipAddress, userAgent, httpRequest);
    }

    private ResponseEntity<Object> handleFailedLogin(User user, String ipAddress, HttpServletRequest httpRequest) {
        // Increment failed attempts
        int newFailedAttempts = user.getFailedLoginAttempt() + 1;
        user.setFailedLoginAttempt(newFailedAttempts);

        // Lock jika >= 5 kali gagal
        if (newFailedAttempts >= AuthenticationConstant.MAX_FAILED_ATTEMPTS) {
            user.setStatus(AuthenticationConstant.LOCKED);
            user.setLockedUntil(LocalDateTime.now().plusMinutes(AuthenticationConstant.LOCK_DURATION_MINUTES));
            userRepository.save(user);

            log.warn("Account locked - userId: {}, email: {}, IP: {}, failedAttempts: {}",
                    user.getId(), user.getEmail(), ipAddress, newFailedAttempts);

            return responseHandler.handleResponse(
                    String.format("Akun terkunci selama %d menit karena terlalu banyak percobaan gagal", AuthenticationConstant.LOCK_DURATION_MINUTES),
                    HttpStatus.BAD_REQUEST,
                    "AUTH_005",
                    null,
                    httpRequest
            );
        }

        userRepository.save(user);

        // Beri hint sisa percobaan
        int remainingAttempts = AuthenticationConstant.MAX_FAILED_ATTEMPTS - newFailedAttempts;
        String message = "Email atau password salah";
        if (remainingAttempts > 0) {
            message += String.format(". Sisa percobaan: %d", remainingAttempts);
        } else {
            message += ". Akun akan terkunci setelah percobaan gagal berikutnya";
        }

        log.warn("Failed login - userId: {}, email: {}, IP: {}, failedAttempts: {}, remaining: {}",
                user.getId(), user.getEmail(), ipAddress, newFailedAttempts, remainingAttempts);

        return responseHandler.handleResponse(
                message,
                HttpStatus.BAD_REQUEST,
                "AUTH_006",
                null,
                httpRequest
        );
    }

    private ResponseEntity<Object> handleSuccessfulPasswordVerification(User user, String ipAddress,
                                                                        String userAgent, HttpServletRequest httpRequest) {
        // 1. Reset failed attempts karena password benar
        user.setFailedLoginAttempt(0);
        userRepository.save(user);

        // 2. Cek PENDING status (Email belum diverifikasi)
        if (user.getStatus() == AuthenticationConstant.PENDING) {
            Map<String, Object> data = new HashMap<>();
            data.put("userId", user.getId());
            data.put("email", user.getEmail());
            data.put("canResend", true);

            log.info("Login blocked - account pending - userId: {}, email: {}", user.getId(), user.getEmail());

            return responseHandler.handleResponse(
                    "Akun belum aktif. Silakan verifikasi email terlebih dahulu",
                    HttpStatus.BAD_REQUEST,
                    "AUTH_004",
                    data,
                    httpRequest
            );
        }

        // 3. Cek LOCKED status
        if (user.getStatus() == AuthenticationConstant.LOCKED) {
            // Cek apakah masih dalam waktu lock
            if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(LocalDateTime.now())) {
                String lockedUntil = user.getLockedUntil().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

                log.info("Login blocked - account locked - userId: {}, email: {}, lockedUntil: {}",
                        user.getId(), user.getEmail(), lockedUntil);

                return responseHandler.handleResponse(
                        String.format("Akun terkunci hingga %s", lockedUntil),
                        HttpStatus.BAD_REQUEST,
                        "AUTH_005",
                        null,
                        httpRequest
                );
            } else {
                // Auto-unlock jika waktu lock sudah lewat
                user.setStatus(AuthenticationConstant.ACTIVE);
                user.setLockedUntil(null);
                userRepository.save(user);
                log.info("Auto-unlocked account - userId: {}, email: {}", user.getId(), user.getEmail());
            }
        }

        // 4. Generate OTP untuk 2FA
        String otp = generateSecureOtp();
        LoginOtpTokens otpToken = new LoginOtpTokens();
        //otpToken.setId(UUID.randomUUID().toString());
        otpToken.setUser(user);
        otpToken.setOtpCode(otp);
        otpToken.setExpiresAt(LocalDateTime.now().plusMinutes(AuthenticationConstant.OTP_EXPIRY_MINUTES));
        otpToken.setCreatedAt(LocalDateTime.now());

        // Invalidate existing OTP tokens for this user
        loginOtpTokenRepository.invalidateUserTokens(user.getId(), LocalDateTime.now());
        loginOtpTokenRepository.save(otpToken);

        // 5. Kirim OTP email
        try {
            emailServiceImpl.sendOtpEmail(user.getEmail(), otp);
            log.info("OTP sent - userId: {}, email: {}, IP: {}", user.getId(), user.getEmail(), ipAddress);
        } catch (Exception e) {
            log.error("Failed to send OTP email - email: {}, error: {}", user.getEmail(), e.getMessage());
            // Lanjutkan proses, user bisa request OTP ulang nanti
        }

        // 6. Return response
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

    // ============ VERIFY OTP & GENERATE JWT TOKENS ============
    @Transactional
    public ResponseEntity<Object> verifyOtp(OtpVerificationRequest request, HttpServletRequest httpRequest) {
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

        // 5. GENERATE JWT TOKENS
        String accessToken = jwtService.generateToken(user.getId(), user.getEmail(), user.getFullName());
        String refreshToken = jwtService.generateRefreshToken(user.getId());

        // 6. INVALIDATE EXISTING TOKENS & SAVE NEW TOKENS
        sessionRepository.invalidateUserTokens(user.getId(), LocalDateTime.now());

        // Save ACCESS token
        saveTokenToSession(user, accessToken, AuthenticationConstant.TOKEN_TYPE_ACCESS, AuthenticationConstant.ACCESS_TOKEN_EXPIRY_HOURS * 60L);

        // Save REFRESH token
        saveTokenToSession(user, refreshToken, AuthenticationConstant.TOKEN_TYPE_REFRESH, AuthenticationConstant.REFRESH_TOKEN_EXPIRY_DAYS * 24L * 60L);

        // 7. Buat response
        LoginResponse loginResponse = LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(AuthenticationConstant.ACCESS_TOKEN_EXPIRY_HOURS * 3600L)
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .build();

        log.info("Login successful with JWT - userId: {}, email: {}, IP: {}",
                user.getId(), user.getEmail(), ipAddress);

        return responseHandler.handleResponse(
                "Login berhasil",
                HttpStatus.OK,
                null,
                loginResponse,
                httpRequest
        );
    }

    private void saveTokenToSession(User user, String token, String tokenType, Long expiryMinutes) {
        Sessions session = new Sessions();
        //session.setId(UUID.randomUUID().toString());
        session.setUser(user);
        session.setSessionToken(token);
        session.setTokenType(tokenType);
        session.setExpiresAt(LocalDateTime.now().plusMinutes(expiryMinutes));
        session.setCreatedAt(LocalDateTime.now());
        session.setLastAccessedAt(LocalDateTime.now());

        sessionRepository.save(session);
    }

    // ============ VERIFY EMAIL ============
    @Transactional
    public ResponseEntity<Object> verifyEmail(String token, HttpServletRequest httpRequest) {
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

    // ============ FORGOT PASSWORD ============
    @Transactional
    public ResponseEntity<Object> forgotPassword(ForgotPasswordRequest request, HttpServletRequest httpRequest) {
        String ipAddress = httpRequest.getRemoteAddr();
        log.info("Forgot password request - email: {}, IP: {}", request.getEmail(), ipAddress);

        // 1. Cari user
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail().toLowerCase().trim());

        if (userOpt.isEmpty()) {
            // Untuk security, tetap return success meski email tidak ditemukan
            log.info("Email not found (for security) - email: {}, IP: {}", request.getEmail(), ipAddress);
            return responseHandler.handleResponse(
                    "Jika email terdaftar, link reset akan dikirim",
                    HttpStatus.OK,
                    null,
                    null,
                    httpRequest
            );
        }

        User user = userOpt.get();

        // 2. Cek status user
        if (user.getStatus() == AuthenticationConstant.PENDING) {
            log.warn("Forgot password blocked - account pending - email: {}", request.getEmail());
            return responseHandler.handleResponse(
                    "Akun belum aktif. Silakan verifikasi email terlebih dahulu",
                    HttpStatus.BAD_REQUEST,
                    "AUTH_004",
                    null,
                    httpRequest
            );
        }

        if (user.getStatus() == AuthenticationConstant.LOCKED) {
            log.warn("Forgot password blocked - account locked - email: {}", request.getEmail());
            return responseHandler.handleResponse(
                    "Akun terkunci. Tidak dapat reset password",
                    HttpStatus.BAD_REQUEST,
                    "AUTH_005",
                    null,
                    httpRequest
            );
        }

        // 3. Invalidate existing reset tokens
        passwordResetTokenRepository.invalidateUserTokens(user.getId(), LocalDateTime.now());

        // 4. Generate new reset token
        String token = UUID.randomUUID().toString();
        PasswordResetTokens resetToken = new PasswordResetTokens();
        //resetToken.setId(UUID.randomUUID().toString());
        resetToken.setUser(user);
        resetToken.setToken(token);
        resetToken.setExpiresAt(LocalDateTime.now().plusHours(AuthenticationConstant.RESET_TOKEN_EXPIRY_HOURS));
        resetToken.setCreatedAt(LocalDateTime.now());

        passwordResetTokenRepository.save(resetToken);

        // 5. Kirim email
        try {
            emailServiceImpl.sendResetPasswordEmail(user.getEmail(), token);
            log.info("Reset password email sent - email: {}, IP: {}", user.getEmail(), ipAddress);
        } catch (Exception e) {
            log.error("Failed to send reset password email - email: {}, error: {}", user.getEmail(), e.getMessage());
            return responseHandler.handleResponse(
                    "Gagal mengirim email reset. Silakan coba lagi nanti.",
                    HttpStatus.BAD_REQUEST,
                    "AUTH_019",
                    null,
                    httpRequest
            );
        }

        return responseHandler.handleResponse(
                "Link reset password dikirim ke email",
                HttpStatus.OK,
                null,
                null,
                httpRequest
        );
    }

    // ============ RESET PASSWORD ============
    @Transactional
    public ResponseEntity<Object> resetPassword(ResetPasswordRequest request, HttpServletRequest httpRequest) {
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
        sessionRepository.invalidateUserTokens(user.getId(), LocalDateTime.now());

        log.info("Password reset successfully - userId: {}, email: {}", user.getId(), user.getEmail());

        return responseHandler.handleResponse(
                "Password berhasil direset",
                HttpStatus.OK,
                null,
                null,
                httpRequest
        );
    }

    // ============ LOGOUT ============
    @Transactional
    public ResponseEntity<Object> logout(String refreshToken, HttpServletRequest httpRequest) {
        log.info("Logout request");

        try {
            // 1. Extract user ID from refresh token
            String userId = jwtService.extractUserId(refreshToken);

            // 2. Invalidate all tokens for this user
            sessionRepository.invalidateUserTokens(userId, LocalDateTime.now());

            log.info("Logout successful - userId: {}", userId);

            return responseHandler.handleResponse(
                    "Logout berhasil",
                    HttpStatus.OK,
                    null,
                    null,
                    httpRequest
            );

        } catch (Exception e) {
            log.warn("Logout with invalid token - error: {}", e.getMessage());
            // Tetap return success karena token sudah invalid
            return responseHandler.handleResponse(
                    "Logout berhasil",
                    HttpStatus.OK,
                    null,
                    null,
                    httpRequest
            );
        }
    }

    // ============ LOGOUT ALL SESSIONS ============
    @Transactional
    public ResponseEntity<Object> logoutAll(String userId, HttpServletRequest httpRequest) {
        log.info("Logout all sessions request - userId: {}", userId);

        // Cek user exists
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return responseHandler.handleResponse(
                    "User tidak ditemukan",
                    HttpStatus.NOT_FOUND,
                    "AUTH_013",
                    null,
                    httpRequest
            );
        }

        // Invalidate all tokens for this user
        sessionRepository.invalidateUserTokens(userId, LocalDateTime.now());

        log.info("All sessions logged out - userId: {}", userId);

        return responseHandler.handleResponse(
                "Semua sesi berhasil di-logout",
                HttpStatus.OK,
                null,
                null,
                httpRequest
        );
    }

    // ============ RESEND VERIFICATION EMAIL ============
    @Transactional
    public ResponseEntity<Object> resendVerificationEmail(String email, HttpServletRequest httpRequest) {
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
            Duration timeSinceLast = Duration.between(lastSent, LocalDateTime.now());

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
        EmailVerificationTokens verificationToken = new EmailVerificationTokens();
        //verificationToken.setId(UUID.randomUUID().toString());
        verificationToken.setUser(user);
        verificationToken.setToken(token);
        verificationToken.setExpiresAt(LocalDateTime.now().plusHours(AuthenticationConstant.VERIFICATION_TOKEN_EXPIRY_HOURS));
        verificationToken.setCreatedAt(LocalDateTime.now());

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

    // ============ RESEND OTP ============
    @Transactional
    public ResponseEntity<Object> resendOtp(ResendOtpRequest request, HttpServletRequest httpRequest) {
        String ipAddress = httpRequest.getRemoteAddr();
        log.info("Resend OTP request - userId: {}, IP: {}", request.getUserId(), ipAddress);

        // Validasi input
        if (request.getUserId() == null || request.getUserId().trim().isEmpty()) {
            return responseHandler.handleResponse(
                    "User ID harus diisi",
                    HttpStatus.BAD_REQUEST,
                    "AUTH_001",
                    null,
                    httpRequest
            );
        }

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
        LoginOtpTokens otpToken = new LoginOtpTokens();
        //otpToken.setId(UUID.randomUUID().toString());
        otpToken.setUser(user);
        otpToken.setOtpCode(otp);
        otpToken.setExpiresAt(LocalDateTime.now().plusMinutes(AuthenticationConstant.OTP_EXPIRY_MINUTES));
        otpToken.setCreatedAt(LocalDateTime.now());

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

    // ============ CHECK SESSION (WITH JWT VALIDATION) ============
    @Transactional
    public ResponseEntity<Object> checkSession(String accessToken, HttpServletRequest httpRequest) {
        log.debug("Check session request");

        try {
            // 1. Validasi JWT token format
            if (!jwtService.isTokenValid(accessToken)) {
                log.warn("Invalid JWT token");
                return responseHandler.handleResponse(
                        "Token tidak valid atau telah expired",
                        HttpStatus.BAD_REQUEST,
                        "AUTH_015",
                        null,
                        httpRequest
                );
            }

            // 2. Cek di database apakah token belum di-invalidate
            Optional<Sessions> sessionOpt = sessionRepository.findBySessionTokenAndTokenTypeAndExpiresAtAfterAndInvalidatedAtIsNull(
                    accessToken, AuthenticationConstant.TOKEN_TYPE_ACCESS, LocalDateTime.now());

            if (sessionOpt.isEmpty()) {
                log.warn("Token revoked or expired in database");
                return responseHandler.handleResponse(
                        "Session telah di-revoke",
                        HttpStatus.BAD_REQUEST,
                        "AUTH_024",
                        null,
                        httpRequest
                );
            }

            // 3. Update last accessed time
            Sessions session = sessionOpt.get();
            session.setLastAccessedAt(LocalDateTime.now());
            sessionRepository.save(session);

            // 4. Extract data dari JWT
            String userId = jwtService.extractUserId(accessToken);
            String email = jwtService.extractEmail(accessToken);
            String fullName = jwtService.extractFullName(accessToken);
            Date expiresAt = jwtService.extractExpiration(accessToken);

            // 5. Cek user masih aktif
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty() || userOpt.get().getStatus() != AuthenticationConstant.ACTIVE) {
                return responseHandler.handleResponse(
                        "User tidak aktif",
                        HttpStatus.BAD_REQUEST,
                        "AUTH_018",
                        null,
                        httpRequest
                );
            }

            User user = userOpt.get();

            // 6. Buat response
            SessionResponse sessionResponse = SessionResponse.builder()
                    .userId(userId)
                    .email(email)
                    .fullName(fullName != null ? fullName : user.getFullName())
                    .expiresAt(expiresAt.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                    .tokenValid(true)
                    .build();

            log.debug("Session check successful - userId: {}", userId);

            return responseHandler.handleResponse(
                    "Token valid",
                    HttpStatus.OK,
                    null,
                    sessionResponse,
                    httpRequest
            );

        } catch (Exception e) {
            log.error("Session check error: {}", e.getMessage());
            return responseHandler.handleResponse(
                    "Token tidak valid",
                    HttpStatus.BAD_REQUEST,
                    "AUTH_015",
                    null,
                    httpRequest
            );
        }
    }

    // ============ REFRESH TOKEN ============
    @Transactional
    public ResponseEntity<Object> refreshToken(String refreshToken, HttpServletRequest httpRequest) {
        log.info("Refresh token request");

        try {
            // 1. Validasi refresh token di JWT
            if (!jwtService.isTokenValid(refreshToken)) {
                log.warn("Invalid refresh token - JWT validation failed");
                return responseHandler.handleResponse(
                        "Refresh token tidak valid",
                        HttpStatus.BAD_REQUEST,
                        "AUTH_023",
                        null,
                        httpRequest
                );
            }

            // 2. Cek di database apakah refresh token masih valid
            Optional<Sessions> sessionOpt = sessionRepository.findBySessionTokenAndTokenTypeAndExpiresAtAfterAndInvalidatedAtIsNull(
                    refreshToken, AuthenticationConstant.TOKEN_TYPE_REFRESH, LocalDateTime.now());

            if (sessionOpt.isEmpty()) {
                log.warn("Refresh token not found or revoked");
                return responseHandler.handleResponse(
                        "Refresh token tidak valid",
                        HttpStatus.BAD_REQUEST,
                        "AUTH_023",
                        null,
                        httpRequest
                );
            }

            Sessions refreshTokenSession = sessionOpt.get();
            User user = refreshTokenSession.getUser();

            // 3. Cek user masih aktif
            if (user.getStatus() != AuthenticationConstant.ACTIVE) {
                log.warn("User not active during token refresh - userId: {}", user.getId());
                return responseHandler.handleResponse(
                        "Akun tidak aktif",
                        HttpStatus.BAD_REQUEST,
                        "AUTH_018",
                        null,
                        httpRequest
                );
            }

            // 4. Invalidate old refresh token
            refreshTokenSession.setInvalidatedAt(LocalDateTime.now());
            sessionRepository.save(refreshTokenSession);

            // 5. Generate new tokens
            String newAccessToken = jwtService.generateToken(user.getId(), user.getEmail(), user.getFullName());
            String newRefreshToken = jwtService.generateRefreshToken(user.getId());

            // 6. Save new tokens
            saveTokenToSession(user, newAccessToken, AuthenticationConstant.TOKEN_TYPE_ACCESS, AuthenticationConstant.ACCESS_TOKEN_EXPIRY_HOURS * 60L);
            saveTokenToSession(user, newRefreshToken, AuthenticationConstant.TOKEN_TYPE_REFRESH, AuthenticationConstant.REFRESH_TOKEN_EXPIRY_DAYS * 24L * 60L);

            // 7. Buat response
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("accessToken", newAccessToken);
            responseData.put("refreshToken", newRefreshToken);
            responseData.put("tokenType", "Bearer");
            responseData.put("expiresIn", AuthenticationConstant.ACCESS_TOKEN_EXPIRY_HOURS * 3600L);

            log.info("Token refreshed successfully - userId: {}", user.getId());

            return responseHandler.handleResponse(
                    "Token berhasil diperbarui",
                    HttpStatus.OK,
                    null,
                    responseData,
                    httpRequest
            );

        } catch (Exception e) {
            log.error("Token refresh error: {}", e.getMessage());
            return responseHandler.handleResponse(
                    "Refresh token tidak valid",
                    HttpStatus.BAD_REQUEST,
                    "AUTH_023",
                    null,
                    httpRequest
            );
        }
    }

    // ============ VALIDATE RESET TOKEN ============
    @Transactional(readOnly = true)
    public ResponseEntity<Object> validateResetToken(String token, HttpServletRequest httpRequest) {
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

    // ============ HELPER METHODS ============
    private boolean isValidRegistrationData(RegisterRequest request) {
        if (request.getFullName() == null || request.getFullName().trim().length() < 3) {
            return false;
        }

        if (request.getEmail() == null || !isValidEmail(request.getEmail())) {
            return false;
        }

        if (request.getPassword() == null || !isValidPassword(request.getPassword())) {
            return false;
        }

        return true;
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        // Regex untuk validasi email sederhana
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    private boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        // Cek minimal 2 dari 3 kriteria: uppercase, lowercase, digit
        boolean hasUpperCase = false;
        boolean hasLowerCase = false;
        boolean hasDigit = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUpperCase = true;
            }
            if (Character.isLowerCase(c)) {
                hasLowerCase = true;
            }
            if (Character.isDigit(c)) {
                hasDigit = true;
            }
        }

        int criteriaMet = 0;
        if (hasUpperCase) criteriaMet++;
        if (hasLowerCase) criteriaMet++;
        if (hasDigit) criteriaMet++;

        return criteriaMet >= 2; // Minimal 2 kriteria terpenuhi
    }

    private String generateSecureOtp() {
        // Generate 6-digit OTP
        java.security.SecureRandom random = new java.security.SecureRandom();
        int num = random.nextInt(1000000);
        return String.format("%06d", num);
    }
}