package com.backendsyndicate.smashclub.auth.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    private void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    // Aktivasi akun
    public void sendActivationEmail(String to, String token) {
        String link = "https://smashclub.com/verify?token=" + token;
        String body =
                "Halo,\n\n" +
                        "Terima kasih telah mendaftar di SmashClub.\n\n" +
                        "Silakan klik tautan di bawah ini untuk mengaktifkan akun Anda:\n" +
                        link + "\n\n" +
                        "Tautan ini berlaku selama 24 jam.\n\n" +
                        "Jika Anda tidak merasa mendaftar di SmashClub, silakan abaikan email ini.\n\n" +
                        "Salam,\n" +
                        "Tim SmashClub";

        sendEmail(to, "Aktivasi Akun SmashClub", body);
    }

    // Reset password
    public void sendResetPasswordEmail(String to, String token) {
        String link = "https://smashclub.com/reset-password?token=" + token;
        String body =
                "Halo,\n\n" +
                        "Kami menerima permintaan untuk mengatur ulang password akun SmashClub Anda.\n\n" +
                        "Silakan klik tautan di bawah ini untuk melanjutkan proses reset password:\n" +
                        link + "\n\n" +
                        "Tautan ini hanya berlaku selama 15 menit.\n\n" +
                        "Jika Anda tidak merasa meminta reset password, abaikan email ini dan password Anda akan tetap aman.\n\n" +
                        "Salam,\n" +
                        "Tim SmashClub";

        sendEmail(to, "Reset Password SmashClub", body);
    }

    // OTP login
    public void sendOtpEmail(String to, String otp) {
        String body =
                "Halo,\n\n" +
                        "Gunakan kode OTP berikut untuk login ke akun SmashClub Anda:\n\n" +
                        otp + "\n\n" +
                        "Kode ini berlaku selama 5 menit.\n\n" +
                        "Jika Anda tidak melakukan percobaan login, silakan abaikan email ini.\n\n" +
                        "Salam,\n" +
                        "Tim SmashClub";

        sendEmail(to, "Kode OTP Login SmashClub", body);
    }
}