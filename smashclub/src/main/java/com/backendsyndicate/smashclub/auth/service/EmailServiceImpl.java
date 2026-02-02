package com.backendsyndicate.smashclub.auth.service.impl;

import com.backendsyndicate.smashclub.auth.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Async
    @Override
    public void sendActivationEmail(String toEmail, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(toEmail);
            helper.setSubject("Verifikasi Email - SmashClub");

            String activationLink = "http://localhost:8080/api/v1/auth/verify-email?token=" + token;
            String emailContent = buildActivationEmail(activationLink);

            helper.setText(emailContent, true);

            mailSender.send(message);
            log.info("Activation email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send activation email to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Failed to send email");
        }
    }

    @Async
    @Override
    public void sendOtpEmail(String toEmail, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(toEmail);
            helper.setSubject("OTP Login - SmashClub");

            String emailContent = buildOtpEmail(otp);

            helper.setText(emailContent, true);

            mailSender.send(message);
            log.info("OTP email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send OTP email to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Failed to send email");
        }
    }

    @Async
    @Override
    public void sendResetPasswordEmail(String toEmail, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(toEmail);
            helper.setSubject("Reset Password - SmashClub");

            String resetLink = "http://localhost:3000/reset-password?token=" + token;
            String emailContent = buildResetPasswordEmail(resetLink);

            helper.setText(emailContent, true);

            mailSender.send(message);
            log.info("Reset password email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send reset password email to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Failed to send email");
        }
    }

    private String buildActivationEmail(String activationLink) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head><style>body{font-family:Arial,sans-serif;}</style></head>" +
                "<body>" +
                "<h2>Verifikasi Email Anda</h2>" +
                "<p>Terima kasih telah mendaftar di SmashClub.</p>" +
                "<p>Silakan klik link berikut untuk verifikasi email Anda:</p>" +
                "<p><a href=\"" + activationLink + "\">" + activationLink + "</a></p>" +
                "<p>Link ini akan kadaluarsa dalam 24 jam.</p>" +
                "</body></html>";
    }

    private String buildOtpEmail(String otp) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head><style>body{font-family:Arial,sans-serif;}</style></head>" +
                "<body>" +
                "<h2>Kode OTP Login</h2>" +
                "<p>Gunakan kode OTP berikut untuk login ke akun SmashClub Anda:</p>" +
                "<h1 style=\"background:#f0f0f0;padding:10px;display:inline-block;\">" + otp + "</h1>" +
                "<p>Kode ini akan kadaluarsa dalam 10 menit.</p>" +
                "<p>Jangan berikan kode ini kepada siapapun.</p>" +
                "</body></html>";
    }

    private String buildResetPasswordEmail(String resetLink) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head><style>body{font-family:Arial,sans-serif;}</style></head>" +
                "<body>" +
                "<h2>Reset Password</h2>" +
                "<p>Anda telah meminta reset password untuk akun SmashClub Anda.</p>" +
                "<p>Silakan klik link berikut untuk reset password:</p>" +
                "<p><a href=\"" + resetLink + "\">" + resetLink + "</a></p>" +
                "<p>Link ini akan kadaluarsa dalam 1 jam.</p>" +
                "<p>Jika Anda tidak meminta reset password, abaikan email ini.</p>" +
                "</body></html>";
    }
}