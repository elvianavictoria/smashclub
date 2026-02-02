package com.backendsyndicate.smashclub.auth.controller;

import com.backendsyndicate.smashclub.auth.dto.*;
import com.backendsyndicate.smashclub.auth.dto.ForgotPasswordRequest;
import com.backendsyndicate.smashclub.auth.dto.LoginRequest;
import com.backendsyndicate.smashclub.auth.dto.RegisterRequest;
import com.backendsyndicate.smashclub.auth.dto.request.OtpVerificationRequest;
import com.backendsyndicate.smashclub.auth.dto.request.ResendOtpRequest;
import com.backendsyndicate.smashclub.auth.dto.request.ResetPasswordRequest;
import com.backendsyndicate.smashclub.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // ============ REGISTRATION ============
    @PostMapping("/register")
    public ResponseEntity<Object> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest httpRequest
    ) {
        return authService.register(request, httpRequest);
    }

    // ============ LOGIN ============
    @PostMapping("/login")
    public ResponseEntity<Object> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest
    ) {
        return authService.login(request, httpRequest);
    }

    // ============ VERIFY OTP ============
    @PostMapping("/verify-otp")
    public ResponseEntity<Object> verifyOtp(
            @Valid @RequestBody OtpVerificationRequest request,
            HttpServletRequest httpRequest
    ) {
        return authService.verifyOtp(request, httpRequest);
    }

    // ============ VERIFY EMAIL ============
    @GetMapping("/verify-email")
    public ResponseEntity<Object> verifyEmail(
            @RequestParam("token") String token,
            HttpServletRequest httpRequest
    ) {
        return authService.verifyEmail(token, httpRequest);
    }

    // ============ FORGOT PASSWORD ============
    @PostMapping("/forgot-password")
    public ResponseEntity<Object> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request,
            HttpServletRequest httpRequest
    ) {
        return authService.forgotPassword(request, httpRequest);
    }

    // ============ RESET PASSWORD ============
    @PostMapping("/reset-password")
    public ResponseEntity<Object> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request,
            HttpServletRequest httpRequest
    ) {
        return authService.resetPassword(request, httpRequest);
    }

    // ============ VALIDATE RESET TOKEN ============
    @GetMapping("/validate-reset-token")
    public ResponseEntity<Object> validateResetToken(
            @RequestParam("token") String token,
            HttpServletRequest httpRequest
    ) {
        return authService.validateResetToken(token, httpRequest);
    }

    // ============ RESEND VERIFICATION EMAIL ============
    @PostMapping("/resend-verification")
    public ResponseEntity<Object> resendVerificationEmail(
            @RequestParam("email") String email,
            HttpServletRequest httpRequest
    ) {
        return authService.resendVerificationEmail(email, httpRequest);
    }

    // ============ RESEND OTP ============
    @PostMapping("/resend-otp")
    public ResponseEntity<Object> resendOtp(
            @Valid @RequestBody ResendOtpRequest request,
            HttpServletRequest httpRequest
    ) {
        return authService.resendOtp(request, httpRequest);
    }

    // ============ LOGOUT ============
    @PostMapping("/logout")
    public ResponseEntity<Object> logout(
            @RequestParam("refreshToken") String refreshToken,
            HttpServletRequest httpRequest
    ) {
        return authService.logout(refreshToken, httpRequest);
    }

    // ============ LOGOUT ALL SESSIONS (Optional) ============
    @PostMapping("/logout-all")
    public ResponseEntity<Object> logoutAll(
            @RequestParam("userId") String userId,
            HttpServletRequest httpRequest
    ) {
        // Note: Butuh authorization check
        return authService.logoutAll(userId, httpRequest);
    }

    // ============ CHECK SESSION ============
    @GetMapping("/check-session")
    public ResponseEntity<Object> checkSession(
            @RequestHeader("Authorization") String authorizationHeader,
            HttpServletRequest httpRequest
    ) {
        String token = extractTokenFromHeader(authorizationHeader);
        return authService.checkSession(token, httpRequest);
    }

    // ============ REFRESH TOKEN ============
    @PostMapping("/refresh-token")
    public ResponseEntity<Object> refreshToken(
            @RequestParam("refreshToken") String refreshToken,
            HttpServletRequest httpRequest
    ) {
        return authService.refreshToken(refreshToken, httpRequest);
    }

    // ============ HELPER METHOD ============
    private String extractTokenFromHeader(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return authorizationHeader;
    }
}