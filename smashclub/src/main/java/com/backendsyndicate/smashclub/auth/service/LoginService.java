package com.backendsyndicate.smashclub.auth.service;

import com.backendsyndicate.smashclub.auth.dto.request.LoginRequest;
import com.backendsyndicate.smashclub.auth.model.User;
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
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginService {

    private final UserRepository userRepository;
    private final OtpService otpService;
    private final PasswordHasher passwordHasher;

    @Transactional
    public ResponseEntity<Object> login(LoginRequest request, HttpServletRequest httpRequest,
                                        com.backendsyndicate.smashclub.common.handler.ResponseHandler responseHandler) {
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
            return handleFailedLogin(user, ipAddress, responseHandler, httpRequest);
        }

        // 5. PASSWORD BENAR → sekarang cek status lainnya
        return handleSuccessfulPasswordVerification(user, ipAddress, userAgent, responseHandler, httpRequest);
    }

    private ResponseEntity<Object> handleFailedLogin(User user, String ipAddress,
                                                     com.backendsyndicate.smashclub.common.handler.ResponseHandler responseHandler,
                                                     HttpServletRequest httpRequest) {
        // Increment failed attempts
        int newFailedAttempts = user.getFailedLoginAttempt() + 1;
        user.setFailedLoginAttempt(newFailedAttempts);

        // Lock jika >= 5 kali gagal
        if (newFailedAttempts >= com.backendsyndicate.smashclub.common.constant.AuthenticationConstant.MAX_FAILED_ATTEMPTS) {
            user.setStatus(com.backendsyndicate.smashclub.common.constant.AuthenticationConstant.LOCKED);
            user.setLockedUntil(LocalDateTime.now()
                    .plusMinutes(com.backendsyndicate.smashclub.common.constant.AuthenticationConstant.LOCK_DURATION_MINUTES));
            userRepository.save(user);

            log.warn("Account locked - userId: {}, email: {}, IP: {}, failedAttempts: {}",
                    user.getId(), user.getEmail(), ipAddress, newFailedAttempts);

            return responseHandler.handleResponse(
                    String.format("Akun terkunci selama %d menit karena terlalu banyak percobaan gagal",
                            com.backendsyndicate.smashclub.common.constant.AuthenticationConstant.LOCK_DURATION_MINUTES),
                    HttpStatus.BAD_REQUEST,
                    "AUTH_005",
                    null,
                    httpRequest
            );
        }

        userRepository.save(user);

        // Beri hint sisa percobaan
        int remainingAttempts = com.backendsyndicate.smashclub.common.constant.AuthenticationConstant.MAX_FAILED_ATTEMPTS - newFailedAttempts;
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
                                                                        String userAgent,
                                                                        com.backendsyndicate.smashclub.common.handler.ResponseHandler responseHandler,
                                                                        HttpServletRequest httpRequest) {
        // 1. Reset failed attempts karena password benar
        user.setFailedLoginAttempt(0);
        userRepository.save(user);

        // 2. Cek PENDING status (Email belum diverifikasi)
        if (user.getStatus() == com.backendsyndicate.smashclub.common.constant.AuthenticationConstant.PENDING) {
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
        if (user.getStatus() == com.backendsyndicate.smashclub.common.constant.AuthenticationConstant.LOCKED) {
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
                user.setStatus(com.backendsyndicate.smashclub.common.constant.AuthenticationConstant.ACTIVE);
                user.setLockedUntil(null);
                userRepository.save(user);
                log.info("Auto-unlocked account - userId: {}, email: {}", user.getId(), user.getEmail());
            }
        }

        // 4. Delegate to OTP service untuk generate dan kirim OTP
        return otpService.generateAndSendOtp(user, ipAddress, responseHandler, httpRequest);
    }
}