package com.backendsyndicate.smashclub.auth.service;

public interface EmailService {
    void sendActivationEmail(String toEmail, String token);
    void sendOtpEmail(String toEmail, String otp);
    void sendResetPasswordEmail(String toEmail, String token);

    // ============ TAMBAHAN UNTUK PROFILE MANAGEMENT ============
    void sendEmailChangeVerificationEmail(String newEmail, String token);
    void sendEmailChangeNotificationEmail(String oldEmail, String newEmail);
    void sendEmailChangeConfirmationEmail(String newEmail);
    void sendPasswordChangeNotificationEmail(String email);
}