package com.backendsyndicate.smashclub.auth.service;

public interface EmailService {
    void sendActivationEmail(String toEmail, String token);
    void sendOtpEmail(String toEmail, String otp);
    void sendResetPasswordEmail(String toEmail, String token);
}