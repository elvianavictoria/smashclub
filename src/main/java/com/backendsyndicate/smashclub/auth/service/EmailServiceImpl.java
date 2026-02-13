package com.backendsyndicate.smashclub.auth.service;

import com.backendsyndicate.smashclub.auth.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Value("${app.backend.url:http://localhost:8080}")
    private String backendUrl;

    @Value("${app.name:SmashClub}")
    private String appName;

    // ============ PRIVATE HELPER METHOD ============
    @Async
    private void sendEmail(String toEmail, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body, true);

            mailSender.send(message);
            log.info("Email sent to: {}, subject: {}", toEmail, subject);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", toEmail, e.getMessage(), e);
            throw new RuntimeException("Failed to send email to " + toEmail, e);
        }
    }

    // ============ ACTIVATION EMAIL ============
    @Async
    @Override
    public void sendActivationEmail(String toEmail, String token) {
        try {
            String activationLink = backendUrl + "/api/v1/auth/verify-email?token=" + token;
            String subject = "Verifikasi Email - " + appName;
            String emailContent = buildActivationEmail(activationLink);

            sendEmail(toEmail, subject, emailContent);
        } catch (Exception e) {
            log.error("Failed to send activation email to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Failed to send email");
        }
    }

    // ============ OTP EMAIL ============
    @Async
    @Override
    public void sendOtpEmail(String toEmail, String otp) {
        try {
            String subject = "OTP Login - " + appName;
            String emailContent = buildOtpEmail(otp);

            sendEmail(toEmail, subject, emailContent);
        } catch (Exception e) {
            log.error("Failed to send OTP email to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Failed to send email");
        }
    }

    // ============ RESET PASSWORD EMAIL ============
    @Async
    @Override
    public void sendResetPasswordEmail(String toEmail, String token) {
        try {
            String resetLink = frontendUrl + "/reset-password?token=" + token;
            String subject = "Reset Password - " + appName;
            String emailContent = buildResetPasswordEmail(resetLink);

            sendEmail(toEmail, subject, emailContent);
        } catch (Exception e) {
            log.error("Failed to send reset password email to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Failed to send email");
        }
    }

    // ============ EMAIL CHANGE VERIFICATION ============
    @Async
    public void sendEmailChangeVerificationEmail(String newEmail, String token) {
        try {
            String verificationLink = backendUrl + "/verify-email-change?token=" + token;
            String subject = "Verifikasi Perubahan Email - " + appName;
            String body = "<h3>Verifikasi Perubahan Email</h3>" +
                    "<p>Kami menerima permintaan untuk mengubah email akun Anda.</p>" +
                    "<p>Email baru: <strong>" + newEmail + "</strong></p>" +
                    "<p>Silakan klik link berikut untuk memverifikasi perubahan email:</p>" +
                    "<p><a href=\"" + verificationLink + "\">" + verificationLink + "</a></p>" +
                    "<p>Link ini akan kadaluarsa dalam 24 jam.</p>" +
                    "<p>Jika Anda tidak meminta perubahan email ini, abaikan email ini.</p>";

            sendEmail(newEmail, subject, body);
            log.info("Email change verification sent to new email: {}", newEmail);
        } catch (Exception e) {
            log.error("Failed to send email change verification to {}: {}", newEmail, e.getMessage());
            throw new RuntimeException("Failed to send email change verification");
        }
    }

    // ============ EMAIL CHANGE NOTIFICATION ============
    @Async
    public void sendEmailChangeNotificationEmail(String oldEmail, String newEmail) {
        try {
            String subject = "Notifikasi Perubahan Email - " + appName;
            String body = "<h3>Email Anda Telah Diubah</h3>" +
                    "<p>Email akun Anda telah diubah dari:</p>" +
                    "<p><strong>" + oldEmail + "</strong></p>" +
                    "<p>menjadi:</p>" +
                    "<p><strong>" + newEmail + "</strong></p>" +
                    "<p>Jika ini bukan Anda, segera hubungi support kami.</p>";

            sendEmail(oldEmail, subject, body);
            log.info("Email change notification sent to old email: {}", oldEmail);
        } catch (Exception e) {
            log.error("Failed to send email change notification to {}: {}", oldEmail, e.getMessage());
            // Non-critical, just log - don't throw
        }
    }

    // ============ EMAIL CHANGE CONFIRMATION ============
    @Async
    public void sendEmailChangeConfirmationEmail(String newEmail) {
        try {
            String subject = "Perubahan Email Berhasil - " + appName;
            String body = "<h3>Email Berhasil Diubah</h3>" +
                    "<p>Email akun Anda telah berhasil diubah menjadi:</p>" +
                    "<p><strong>" + newEmail + "</strong></p>" +
                    "<p>Sekarang Anda dapat login menggunakan email ini.</p>";

            sendEmail(newEmail, subject, body);
            log.info("Email change confirmation sent to new email: {}", newEmail);
        } catch (Exception e) {
            log.error("Failed to send email change confirmation to {}: {}", newEmail, e.getMessage());
            // Non-critical, just log - don't throw
        }
    }

    // ============ PASSWORD CHANGE NOTIFICATION ============
    @Async
    public void sendPasswordChangeNotificationEmail(String email) {
        try {
            String subject = "Notifikasi Perubahan Password - " + appName;
            String body = "<h3>Password Anda Telah Diubah</h3>" +
                    "<p>Password akun Anda telah berhasil diubah.</p>" +
                    "<p>Jika ini bukan Anda, segera hubungi support kami.</p>";

            sendEmail(email, subject, body);
            log.info("Password change notification sent to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send password change notification to {}: {}", email, e.getMessage());
            // Non-critical, just log - don't throw
        }
    }

    // ============ EMAIL TEMPLATES ============
    private String buildActivationEmail(String activationLink) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head><style>body{font-family:Arial,sans-serif;}</style></head>" +
                "<body>" +
                "<h2>Verifikasi Email Anda</h2>" +
                "<p>Terima kasih telah mendaftar di " + appName + ".</p>" +
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
                "<p>Gunakan kode OTP berikut untuk login ke akun " + appName + " Anda:</p>" +
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
                "<p>Anda telah meminta reset password untuk akun " + appName + " Anda.</p>" +
                "<p>Silakan klik link berikut untuk reset password:</p>" +
                "<p><a href=\"" + resetLink + "\">" + resetLink + "</a></p>" +
                "<p>Link ini akan kadaluarsa dalam 1 jam.</p>" +
                "<p>Jika Anda tidak meminta reset password, abaikan email ini.</p>" +
                "</body></html>";
    }
}