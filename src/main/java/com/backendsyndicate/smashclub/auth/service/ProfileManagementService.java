package com.backendsyndicate.smashclub.auth.service;

import com.backendsyndicate.smashclub.auth.dto.request.*;
import com.backendsyndicate.smashclub.auth.dto.response.ProfileResponse;
import com.backendsyndicate.smashclub.auth.model.EmailChangeToken;
import com.backendsyndicate.smashclub.auth.model.User;
import com.backendsyndicate.smashclub.auth.repository.EmailChangeTokenRepository;
import com.backendsyndicate.smashclub.auth.repository.UserRepository;
import com.backendsyndicate.smashclub.common.constant.AuthenticationConstant;
import com.backendsyndicate.smashclub.common.security.JwtService;
import com.backendsyndicate.smashclub.common.security.PasswordHasher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileManagementService {

    private final UserRepository userRepository;
    private final EmailChangeTokenRepository emailChangeTokenRepository;
    private final PasswordHasher passwordHasher;
    private final JwtService jwtService;
    private final EmailServiceImpl emailServiceImpl;
    private final SessionService sessionService;

    // ============ GET PROFILE ============
    @Transactional(readOnly = true)
    public ResponseEntity<Object> getProfile(String userId, HttpServletRequest httpRequest,
                                             com.backendsyndicate.smashclub.common.handler.ResponseHandler responseHandler) {
        log.info("Get profile request - userId: {}", userId);

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return responseHandler.handleResponse(
                    "User tidak ditemukan",
                    HttpStatus.NOT_FOUND,
                    "PROFILE_001",
                    null,
                    httpRequest
            );
        }

        User user = userOpt.get();
        ProfileResponse profileResponse = ProfileResponse.fromUser(user);

        // Check if there's pending email change
        Optional<EmailChangeToken> pendingEmailChange = emailChangeTokenRepository
                .findByUserIdAndUsedAtIsNull(userId);

        if (pendingEmailChange.isPresent()) {
            Map<String, Object> data = new HashMap<>();
            data.put("profile", profileResponse);
            data.put("pendingEmailChange", true);
            data.put("pendingNewEmail", pendingEmailChange.get().getNewEmail());

            return responseHandler.handleResponse(
                    "Profile berhasil diambil. Ada perubahan email yang belum diverifikasi",
                    HttpStatus.OK,
                    null,
                    data,
                    httpRequest
            );
        }

        return responseHandler.handleResponse(
                "Profile berhasil diambil",
                HttpStatus.OK,
                null,
                profileResponse,
                httpRequest
        );
    }

    // ============ UPDATE PROFILE (FULL NAME ONLY) ============
    @Transactional
    public ResponseEntity<Object> updateProfile(String userId, ProfileUpdateRequest request,
                                                HttpServletRequest httpRequest,
                                                com.backendsyndicate.smashclub.common.handler.ResponseHandler responseHandler) {
        log.info("Update profile request - userId: {}", userId);

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return responseHandler.handleResponse(
                    "User tidak ditemukan",
                    HttpStatus.NOT_FOUND,
                    "PROFILE_001",
                    null,
                    httpRequest
            );
        }

        User user = userOpt.get();

        // Update full name
        if (request.getFullName() != null && !request.getFullName().trim().isEmpty()) {
            if (request.getFullName().trim().length() < 3) {
                return responseHandler.handleResponse(
                        "Nama lengkap minimal 3 karakter",
                        HttpStatus.BAD_REQUEST,
                        "PROFILE_002",
                        null,
                        httpRequest
                );
            }
            user.setFullName(request.getFullName().trim());
        }

        // Update email jika ada perubahan (butuh verification)
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            String newEmail = request.getEmail().toLowerCase().trim();

            // Cek apakah email sama dengan yang sekarang
            if (newEmail.equals(user.getEmail())) {
                return responseHandler.handleResponse(
                        "Email baru sama dengan email saat ini",
                        HttpStatus.BAD_REQUEST,
                        "PROFILE_003",
                        null,
                        httpRequest
                );
            }

            // Cek format email
            if (!isValidEmail(newEmail)) {
                return responseHandler.handleResponse(
                        "Format email tidak valid",
                        HttpStatus.BAD_REQUEST,
                        "PROFILE_004",
                        null,
                        httpRequest
                );
            }

            // Cek apakah email sudah digunakan oleh user lain
            Optional<User> existingUser = userRepository.findByEmail(newEmail);
            if (existingUser.isPresent() && !existingUser.get().getId().equals(userId)) {
                return responseHandler.handleResponse(
                        "Email sudah digunakan oleh user lain",
                        HttpStatus.BAD_REQUEST,
                        "PROFILE_005",
                        null,
                        httpRequest
                );
            }

            // Generate token untuk email change verification
            String token = UUID.randomUUID().toString();

            // Invalidate existing email change tokens
            emailChangeTokenRepository.invalidateUserTokens(userId, LocalDateTime.now());

            // Create new email change token
            EmailChangeToken emailChangeToken = EmailChangeToken.builder()
                    .user(user)
                    .token(token)
                    .oldEmail(user.getEmail())
                    .newEmail(newEmail)
                    .expiresAt(LocalDateTime.now().plusHours(24)) // 24 jam expiry
                    .createdAt(LocalDateTime.now())
                    .build();

            emailChangeTokenRepository.save(emailChangeToken);

            // Send verification email to new email
            try {
                emailServiceImpl.sendEmailChangeVerificationEmail(newEmail, token);
                log.info("Email change verification sent - userId: {}, newEmail: {}", userId, newEmail);
            } catch (Exception e) {
                log.error("Failed to send email change verification - newEmail: {}, error: {}", newEmail, e.getMessage());
            }

            // Update user
            user.setUpdatedDate(LocalDateTime.now());
            userRepository.save(user);

            // Return response
            Map<String, Object> data = new HashMap<>();
            data.put("userId", user.getId());
            data.put("fullNameUpdated", request.getFullName() != null);
            data.put("emailChangeInitiated", true);
            data.put("message", "Perubahan nama lengkap berhasil. Verifikasi email baru telah dikirim ke " + newEmail);

            return responseHandler.handleResponse(
                    "Perubahan profil berhasil. Verifikasi email baru diperlukan",
                    HttpStatus.OK,
                    null,
                    data,
                    httpRequest
            );
        }

        // Jika hanya update full name
        user.setUpdatedDate(LocalDateTime.now());
        userRepository.save(user);

        ProfileResponse profileResponse = ProfileResponse.fromUser(user);

        return responseHandler.handleResponse(
                "Profil berhasil diperbarui",
                HttpStatus.OK,
                null,
                profileResponse,
                httpRequest
        );
    }

    // ============ VERIFY EMAIL CHANGE ============
    @Transactional
    public ResponseEntity<Object> verifyEmailChange(VerifyEmailChangeRequest request,
                                                    HttpServletRequest httpRequest,
                                                    com.backendsyndicate.smashclub.common.handler.ResponseHandler responseHandler) {
        log.info("Verify email change request - token: {}", request.getToken());

        Optional<EmailChangeToken> tokenOpt = emailChangeTokenRepository
                .findByTokenAndUsedAtIsNull(request.getToken());

        if (tokenOpt.isEmpty()) {
            log.warn("Invalid email change token - token: {}", request.getToken());
            return responseHandler.handleResponse(
                    "Token perubahan email tidak valid",
                    HttpStatus.BAD_REQUEST,
                    "PROFILE_006",
                    null,
                    httpRequest
            );
        }

        EmailChangeToken emailChangeToken = tokenOpt.get();

        // Cek expired
        if (emailChangeToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.warn("Expired email change token - token: {}", request.getToken());
            return responseHandler.handleResponse(
                    "Token perubahan email telah kadaluarsa",
                    HttpStatus.BAD_REQUEST,
                    "PROFILE_007",
                    null,
                    httpRequest
            );
        }

        User user = emailChangeToken.getUser();
        String newEmail = emailChangeToken.getNewEmail();

        // Cek lagi apakah email masih available
        Optional<User> existingUser = userRepository.findByEmail(newEmail);
        if (existingUser.isPresent() && !existingUser.get().getId().equals(user.getId())) {
            // Email sudah diambil oleh user lain sebelum verifikasi
            emailChangeToken.setUsedAt(LocalDateTime.now());
            emailChangeTokenRepository.save(emailChangeToken);

            return responseHandler.handleResponse(
                    "Email sudah digunakan oleh user lain. Silakan gunakan email yang berbeda",
                    HttpStatus.BAD_REQUEST,
                    "PROFILE_005",
                    null,
                    httpRequest
            );
        }

        // Update user email
        String oldEmail = user.getEmail();
        user.setEmail(newEmail);
        user.setUpdatedDate(LocalDateTime.now());
        userRepository.save(user);

        // Mark token as used
        emailChangeToken.setUsedAt(LocalDateTime.now());
        emailChangeTokenRepository.save(emailChangeToken);

        // Send notification email to old email
        try {
            emailServiceImpl.sendEmailChangeNotificationEmail(oldEmail, newEmail);
            log.info("Email change notification sent to old email - oldEmail: {}", oldEmail);
        } catch (Exception e) {
            log.error("Failed to send email change notification - oldEmail: {}, error: {}", oldEmail, e.getMessage());
        }

        // Send confirmation email to new email
        try {
            emailServiceImpl.sendEmailChangeConfirmationEmail(newEmail);
            log.info("Email change confirmation sent to new email - newEmail: {}", newEmail);
        } catch (Exception e) {
            log.error("Failed to send email change confirmation - newEmail: {}, error: {}", newEmail, e.getMessage());
        }

        log.info("Email changed successfully - userId: {}, oldEmail: {}, newEmail: {}",
                user.getId(), oldEmail, newEmail);

        // Invalidate all sessions (force logout from all devices)
        sessionService.invalidateAllUserTokens(user.getId());

        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("oldEmail", oldEmail);
        data.put("newEmail", newEmail);
        data.put("message", "Email berhasil diubah. Anda perlu login ulang dengan email baru.");

        return responseHandler.handleResponse(
                "Email berhasil diubah",
                HttpStatus.OK,
                null,
                data,
                httpRequest
        );
    }

    // ============ CHANGE PASSWORD ============
    @Transactional
    public ResponseEntity<Object> changePassword(String userId, ChangePasswordRequest request,
                                                 HttpServletRequest httpRequest,
                                                 com.backendsyndicate.smashclub.common.handler.ResponseHandler responseHandler) {
        log.info("Change password request - userId: {}", userId);

        // Validasi input
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return responseHandler.handleResponse(
                    "Password baru dan konfirmasi password tidak cocok",
                    HttpStatus.BAD_REQUEST,
                    "PROFILE_008",
                    null,
                    httpRequest
            );
        }

        if (request.getCurrentPassword().equals(request.getNewPassword())) {
            return responseHandler.handleResponse(
                    "Password baru tidak boleh sama dengan password saat ini",
                    HttpStatus.BAD_REQUEST,
                    "PROFILE_009",
                    null,
                    httpRequest
            );
        }

        // Validasi strength password baru
        if (!isValidPassword(request.getNewPassword())) {
            return responseHandler.handleResponse(
                    "Password baru minimal 8 karakter dan harus mengandung minimal 2 dari: huruf besar, huruf kecil, angka",
                    HttpStatus.BAD_REQUEST,
                    "PROFILE_010",
                    null,
                    httpRequest
            );
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return responseHandler.handleResponse(
                    "User tidak ditemukan",
                    HttpStatus.NOT_FOUND,
                    "PROFILE_001",
                    null,
                    httpRequest
            );
        }

        User user = userOpt.get();

        // Verify current password
        boolean currentPasswordValid = passwordHasher.verify(request.getCurrentPassword(), user.getPasswordHash());
        if (!currentPasswordValid) {
            log.warn("Invalid current password for change password - userId: {}", userId);

            // Increment failed attempts (similar to login)
            int newFailedAttempts = user.getFailedLoginAttempt() + 1;
            user.setFailedLoginAttempt(newFailedAttempts);

            // Lock jika >= 5 kali gagal
            if (newFailedAttempts >= AuthenticationConstant.MAX_FAILED_ATTEMPTS) {
                user.setStatus(AuthenticationConstant.LOCKED);
                user.setLockedUntil(LocalDateTime.now()
                        .plusMinutes(AuthenticationConstant.LOCK_DURATION_MINUTES));
                userRepository.save(user);

                return responseHandler.handleResponse(
                        String.format("Akun terkunci selama %d menit karena terlalu banyak percobaan gagal",
                                AuthenticationConstant.LOCK_DURATION_MINUTES),
                        HttpStatus.BAD_REQUEST,
                        "PROFILE_011",
                        null,
                        httpRequest
                );
            }

            userRepository.save(user);

            int remainingAttempts = AuthenticationConstant.MAX_FAILED_ATTEMPTS - newFailedAttempts;
            String message = "Password saat ini salah";
            if (remainingAttempts > 0) {
                message += String.format(". Sisa percobaan: %d", remainingAttempts);
            }

            return responseHandler.handleResponse(
                    message,
                    HttpStatus.BAD_REQUEST,
                    "PROFILE_012",
                    null,
                    httpRequest
            );
        }

        // Update password
        user.setPasswordHash(passwordHasher.hash(request.getNewPassword()));
        user.setUpdatedDate(LocalDateTime.now());
        user.setFailedLoginAttempt(0); // Reset failed attempts
        user.setLockedUntil(null); // Clear any lock
        userRepository.save(user);

        // Send notification email
        try {
            emailServiceImpl.sendPasswordChangeNotificationEmail(user.getEmail());
            log.info("Password change notification sent - userId: {}, email: {}", userId, user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send password change notification - email: {}, error: {}", user.getEmail(), e.getMessage());
        }

        // Invalidate all sessions except current one (optional)
        // Atau bisa force logout dari semua device
        sessionService.invalidateAllUserTokens(user.getId());

        log.info("Password changed successfully - userId: {}", userId);

        return responseHandler.handleResponse(
                "Password berhasil diubah. Anda perlu login ulang.",
                HttpStatus.OK,
                null,
                null,
                httpRequest
        );
    }

    // ============ CANCEL PENDING EMAIL CHANGE ============
    @Transactional
    public ResponseEntity<Object> cancelEmailChange(String userId, HttpServletRequest httpRequest,
                                                    com.backendsyndicate.smashclub.common.handler.ResponseHandler responseHandler) {
        log.info("Cancel email change request - userId: {}", userId);

        Optional<EmailChangeToken> tokenOpt = emailChangeTokenRepository
                .findByUserIdAndUsedAtIsNull(userId);

        if (tokenOpt.isEmpty()) {
            return responseHandler.handleResponse(
                    "Tidak ada perubahan email yang tertunda",
                    HttpStatus.BAD_REQUEST,
                    "PROFILE_013",
                    null,
                    httpRequest
            );
        }

        EmailChangeToken token = tokenOpt.get();
        token.setUsedAt(LocalDateTime.now());
        emailChangeTokenRepository.save(token);

        log.info("Email change cancelled - userId: {}", userId);

        return responseHandler.handleResponse(
                "Perubahan email dibatalkan",
                HttpStatus.OK,
                null,
                null,
                httpRequest
        );
    }

    // ============ HELPER METHODS ============
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    private boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasUpperCase = false;
        boolean hasLowerCase = false;
        boolean hasDigit = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpperCase = true;
            if (Character.isLowerCase(c)) hasLowerCase = true;
            if (Character.isDigit(c)) hasDigit = true;
        }

        int criteriaMet = 0;
        if (hasUpperCase) criteriaMet++;
        if (hasLowerCase) criteriaMet++;
        if (hasDigit) criteriaMet++;

        return criteriaMet >= 2;
    }
}