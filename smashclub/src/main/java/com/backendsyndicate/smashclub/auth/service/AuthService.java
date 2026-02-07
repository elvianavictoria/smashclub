package com.backendsyndicate.smashclub.auth.service;

import com.backendsyndicate.smashclub.auth.dto.request.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final RegistrationService registrationService;
    private final LoginService loginService;
    private final OtpService otpService;
    private final PasswordService passwordService;
    private final TokenService tokenService;
    private final SessionService sessionService;
    private final com.backendsyndicate.smashclub.common.handler.ResponseHandler responseHandler;

    // ============ REGISTRATION ============
    @Transactional
    public ResponseEntity<Object> register(RegisterRequest request, HttpServletRequest httpRequest) {
        return registrationService.register(request, httpRequest, responseHandler);
    }

    // ============ LOGIN ============
    @Transactional
    public ResponseEntity<Object> login(LoginRequest request, HttpServletRequest httpRequest) {
        return loginService.login(request, httpRequest, responseHandler);
    }

    // ============ VERIFY OTP ============
    @Transactional
    public ResponseEntity<Object> verifyOtp(OtpVerificationRequest request, HttpServletRequest httpRequest) {
        return otpService.verifyOtp(request, httpRequest, responseHandler);
    }

    // ============ VERIFY EMAIL ============
    @Transactional
    public ResponseEntity<Object> verifyEmail(String token, HttpServletRequest httpRequest) {
        return registrationService.verifyEmail(token, httpRequest, responseHandler);
    }

    // ============ FORGOT PASSWORD ============
    @Transactional
    public ResponseEntity<Object> forgotPassword(ForgotPasswordRequest request, HttpServletRequest httpRequest) {
        return passwordService.forgotPassword(request, httpRequest, responseHandler);
    }

    // ============ RESET PASSWORD ============
    @Transactional
    public ResponseEntity<Object> resetPassword(ResetPasswordRequest request, HttpServletRequest httpRequest) {
        return passwordService.resetPassword(request, httpRequest, responseHandler);
    }

    // ============ LOGOUT ============
    @Transactional
    public ResponseEntity<Object> logout(String refreshToken, HttpServletRequest httpRequest) {
        return sessionService.logout(refreshToken, httpRequest, responseHandler);
    }

    // ============ LOGOUT ALL SESSIONS ============
    @Transactional
    public ResponseEntity<Object> logoutAll(String userId, HttpServletRequest httpRequest) {
        return sessionService.logoutAll(userId, httpRequest, responseHandler);
    }

    // ============ RESEND VERIFICATION EMAIL ============
    @Transactional
    public ResponseEntity<Object> resendVerificationEmail(String email, HttpServletRequest httpRequest) {
        return registrationService.resendVerificationEmail(email, httpRequest, responseHandler);
    }

    // ============ RESEND OTP ============
    @Transactional
    public ResponseEntity<Object> resendOtp(ResendOtpRequest request, HttpServletRequest httpRequest) {
        return otpService.resendOtp(request, httpRequest, responseHandler);
    }

    // ============ CHECK SESSION ============
    @Transactional
    public ResponseEntity<Object> checkSession(String accessToken, HttpServletRequest httpRequest) {
        return sessionService.checkSession(accessToken, httpRequest, responseHandler);
    }

    // ============ REFRESH TOKEN ============
    @Transactional
    public ResponseEntity<Object> refreshToken(String refreshToken, HttpServletRequest httpRequest) {
        return tokenService.refreshToken(refreshToken, httpRequest, responseHandler);
    }

    // ============ VALIDATE RESET TOKEN ============
    @Transactional(readOnly = true)
    public ResponseEntity<Object> validateResetToken(String token, HttpServletRequest httpRequest) {
        return passwordService.validateResetToken(token, httpRequest, responseHandler);
    }
}