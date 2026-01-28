package com.backendsyndicate.smashclub.auth.service;

import com.backendsyndicate.smashclub.common.constant.UserStatus;
import com.backendsyndicate.smashclub.common.util.GlobalResponse;
import com.backendsyndicate.smashclub.auth.dto.RegisterRequest;
import com.backendsyndicate.smashclub.auth.dto.LoginRequest;
import com.backendsyndicate.smashclub.auth.dto.ForgotPasswordRequest;
import com.backendsyndicate.smashclub.auth.model.User;
import com.backendsyndicate.smashclub.auth.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final EmailService emailService;

    public ResponseEntity<Object> register(RegisterRequest request, HttpServletRequest httpRequest) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return GlobalResponse.failed("Email sudah terdaftar", "AUTH_001", null, httpRequest);
        }

        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(request.getPassword()); // nanti pakai Crypto.hashPassword()
        user.setStatus(UserStatus.PENDING);
        userRepository.save(user);

        // generate verification token (pseudo)
        // emailVerificationRepository.saveToken(user.getId(), UUID.randomUUID().toString());

        // generate token aktivasi
        String token = UUID.randomUUID().toString();
        // simpan token ke email_verification_tokens table
        emailService.sendActivationEmail(user.getEmail(), token);

        return GlobalResponse.created("Registrasi berhasil, cek email untuk verifikasi", user, httpRequest);
    }

    public ResponseEntity<Object> login(LoginRequest request, HttpServletRequest httpRequest) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty()) {
            return GlobalResponse.failed("Email atau password salah", "AUTH_002", null, httpRequest);
        }

        User user = userOpt.get();
        // if (!Crypto.verifyPassword(request.getPassword(), user.getPasswordHash())) {
        //     return GlobalResponse.failed("Email atau password salah", "AUTH_003", null, httpRequest);
        // }

        if (user.getStatus() == UserStatus.PENDING) {
            return GlobalResponse.failed("Akun belum aktif, silakan verifikasi email",
                    "AUTH_004", null, httpRequest);
        } else if (user.getStatus() == UserStatus.LOCKED) {
            return GlobalResponse.failed("Akun terkunci, silakan hubungi admin atau tunggu waktu unlock",
                    "AUTH_006", null, httpRequest);
        }

        return GlobalResponse.success("Login berhasil, OTP dikirim ke email", null, httpRequest);
    }

    public ResponseEntity<Object> forgotPassword(ForgotPasswordRequest request, HttpServletRequest httpRequest) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty()) {
            return GlobalResponse.notFound("Email tidak ditemukan", "AUTH_005", httpRequest);
        }

        User user = userOpt.get();
        // passwordResetRepository.saveToken(user.getId(), UUID.randomUUID().toString());

        return GlobalResponse.success("Link reset password dikirim ke email", null, httpRequest);
    }
}