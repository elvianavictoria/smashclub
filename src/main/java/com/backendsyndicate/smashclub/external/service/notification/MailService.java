package com.backendsyndicate.smashclub.external.service.notification;

import com.backendsyndicate.smashclub.common.service.TemplateService;
import com.backendsyndicate.smashclub.common.util.Logging;
import com.backendsyndicate.smashclub.external.config.SMTPConfig;
import com.backendsyndicate.smashclub.external.dto.MailAuthenticatorDTO;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Properties;

@Service
public class MailService {
    @Autowired
    private TemplateService templateService;
    private final Properties props = new Properties();
    private MailAuthenticatorDTO authenticator;

    private String generateErrorCode(String methodNo, String errorNo) {
        return "MAIL-" + methodNo + "E" + errorNo;
    }

    private MimeMessage initMimeMessage() {
        Logging.printConsole("Creating mime message!");
        props.put("mail.smtp.host", SMTPConfig.getHost());
        props.put("mail.smtp.port", SMTPConfig.getPortTLS());
        props.put("mail.smtp.auth", SMTPConfig.isAuth());
        props.put("mail.smtp.starttls.enable", SMTPConfig.isStartTlsEnable());

        authenticator = new MailAuthenticatorDTO(SMTPConfig.getUsername(), SMTPConfig.getPassword());

        Session session = Session.getDefaultInstance(props, authenticator);
        return new MimeMessage(session);
    }

    public boolean sendMail(String templateCode, String recipientEmail, String subject, Map<String, Object> data) {
        try {
            MimeMessage message = this.initMimeMessage();

            message.setFrom(new InternetAddress(SMTPConfig.getUsername()));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
            message.setSubject(subject);

            Logging.printConsole("Choosing content!");
            String htmlContent = templateService.templateSelector(templateCode, data);
            if( htmlContent.isEmpty() ) {
                Logging.handleException("MailService", "sendMail(String templateCode, String recipientEmail, String subject, Map<String, Object> data)", 47, generateErrorCode("01", "001"), "Failed to find template!");

                return false;
            }

            message.setContent(htmlContent, "text/html; charset=utf-8");

            Transport.send(message);
        } catch(Exception e) {
            Logging.handleException("MailService", "sendMail(String templateCode, String recipientEmail, String subject, Map<String, Object> data)", 39, generateErrorCode("01", "010"), e.getMessage());

            return false;
        }

        return true;
    }
}
