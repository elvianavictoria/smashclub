package com.backendsyndicate.smashclub.common.service;

import com.backendsyndicate.smashclub.common.util.DatetimeFormatting;
import com.backendsyndicate.smashclub.common.util.Logging;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.util.Map;

@Service
public class TemplateService {
    private ClassLoaderTemplateResolver templateResolver;
    private TemplateEngine templateEngine;

    public static final String TEMPLATE_PAYMENT_NOTIFY_PAID = "PAYMENT_NOTIFY_PAID";
    public static final String TEMPLATE_PAYMENT_NOTIFY_UNPAID = "PAYMENT_NOTIFY_UNPAID";
    public static final String TEMPLATE_PAYMENT_NOTIFY_EXPIRED = "PAYMENT_NOTIFY_EXPIRED";
    public static final String TEMPLATE_REFUND_NOTIFY_APPROVED = "REFUND_NOTIFY_APPROVED";
    public static final String TEMPLATE_REFUND_NOTIFY_REJECTED = "REFUND_NOTIFY_REJECTED";

    public static final String TEMPLATE_AUTH_NOTIFY_EMAIL_ACTIVATION = "AUTH_NOTIFY_EMAIL_ACTIVATION";
    public static final String TEMPLATE_AUTH_NOTIFY_EMAIL_OTP = "AUTH_NOTIFY_EMAIL_OTP";
    public static final String TEMPLATE_AUTH_NOTIFY_RESET_PASSWORD = "AUTH_NOTIFY_RESET_PASSWORD";
    public static final String TEMPLATE_AUTH_NOTIFY_EMAIL_CHANGE_VERIFICATION = "NOTIFY_EMAIL_CHANGE_VERIFICATION";
    public static final String TEMPLATE_AUTH_NOTIFY_EMAIL_CHANGE_NOTIFICATION = "NOTIFY_EMAIL_CHANGE_NOTIFICATION";
    public static final String TEMPLATE_AUTH_NOTIFY_EMAIL_CHANGE_CONFIRMATION = "NOTIFY_EMAIL_CHANGE_CONFIRMATION";
    public static final String TEMPLATE_AUTH_NOTIFY_PASSWORD_CHANGE_NOTIFICATION = "NOTIFY_PASSWORD_CHANGE_NOTIFICATION";

    public TemplateService() {
        createTemplateResolver();
        createTemplateEngine();
    }

    private void createTemplateResolver() {
        this.templateResolver = new ClassLoaderTemplateResolver();
        this.templateResolver.setPrefix("templates/");
        this.templateResolver.setSuffix(".html");
        this.templateResolver.setTemplateMode("HTML");
    }

    private void createTemplateEngine() {
        this.templateEngine = new TemplateEngine();
        this.templateEngine.setTemplateResolver(templateResolver);
    }

    private Context createContext(Map<String, Object> data) {
        Context context = new Context();
        for( Map.Entry<String, Object> entries: data.entrySet() ) {
            String key = entries.getKey();
            Object value = entries.getValue();

            context.setVariable(key, value);
        }

        return context;
    }

    public String templateSelector(String templateCode, Map<String, Object> data) {
        String emailContent = "";

        switch( templateCode ) {
            case TEMPLATE_PAYMENT_NOTIFY_UNPAID:
                emailContent = paymentNotifyUnpaidContent(data);
                break;
            case TEMPLATE_PAYMENT_NOTIFY_PAID:
                emailContent = paymentNotifyPaidContent(data);
                break;
            case TEMPLATE_PAYMENT_NOTIFY_EXPIRED:
                emailContent = paymentNotifyExpiredContent(data);
                break;
            case TEMPLATE_REFUND_NOTIFY_APPROVED:
                emailContent = refundNotifyApprovedContent(data);
                break;
            case TEMPLATE_REFUND_NOTIFY_REJECTED:
                emailContent = refundNotifyRejectedContent(data);
                break;
            case TEMPLATE_AUTH_NOTIFY_EMAIL_ACTIVATION:
                emailContent = authNotifyEmailActivationContent(data);
                break;
            case TEMPLATE_AUTH_NOTIFY_EMAIL_OTP:
                emailContent = authNotifyEmailOtpContent(data);
                break;
            case TEMPLATE_AUTH_NOTIFY_RESET_PASSWORD:
                emailContent = authNotifyResetPasswordContent(data);
                break;
            case TEMPLATE_AUTH_NOTIFY_EMAIL_CHANGE_VERIFICATION:
                emailContent = authNotifyEmailChangeVerificationContent(data);
                break;
            case TEMPLATE_AUTH_NOTIFY_EMAIL_CHANGE_NOTIFICATION:
                emailContent = authNotifyEmailChangeNotificationContent(data);
                break;
            case TEMPLATE_AUTH_NOTIFY_EMAIL_CHANGE_CONFIRMATION:
                emailContent = authNotifyEmailChangeConfirmationContent(data);
                break;
            case TEMPLATE_AUTH_NOTIFY_PASSWORD_CHANGE_NOTIFICATION:
                emailContent = authNotifyPasswordChangeNotificationContent(data);
                break;
            default:
                break;
        }

        return emailContent;
    }

    private String paymentNotifyUnpaidContent(Map<String, Object> data) {
        String fileName = "mail/payment/notify-payment-unpaid";

        Context context = createContext(data);
        return this.templateEngine.process(fileName, context);
    }

    private String paymentNotifyPaidContent(Map<String, Object> data) {
        String fileName = "mail/payment/notify-payment-paid";

        Context context = createContext(data);
        return this.templateEngine.process(fileName, context);
    }

    private String paymentNotifyExpiredContent(Map<String, Object> data) {
        String fileName = "mail/payment/notify-payment-expired";

        Context context = createContext(data);
        return this.templateEngine.process(fileName, context);
    }

    private String refundNotifyApprovedContent(Map<String, Object> data) {
        String fileName = "mail/refund/notify-refund-approved";

        Context context = createContext(data);
        return this.templateEngine.process(fileName, context);
    }

    private String refundNotifyRejectedContent(Map<String, Object> data) {
        String fileName = "mail/refund/notify-refund-rejected";

        Context context = createContext(data);
        return this.templateEngine.process(fileName, context);
    }

    private String authNotifyEmailActivationContent(Map<String, Object> data) {
        String fileName = "mail/auth/notify-email-activation";

        Context context = createContext(data);
        return this.templateEngine.process(fileName, context);
    }

    private String authNotifyEmailOtpContent(Map<String, Object> data) {
        String fileName = "mail/auth/notify-email-otp";

        Context context = createContext(data);
        return this.templateEngine.process(fileName, context);
    }

    private String authNotifyResetPasswordContent(Map<String, Object> data) {
        String fileName = "mail/auth/notify-reset-password";

        Context context = createContext(data);
        return this.templateEngine.process(fileName, context);
    }

    private String authNotifyEmailChangeVerificationContent(Map<String, Object> data) {
        String fileName = "mail/auth/notify-email-change-verification";

        Context context = createContext(data);
        return this.templateEngine.process(fileName, context);
    }

    private String authNotifyEmailChangeNotificationContent(Map<String, Object> data) {
        String fileName = "mail/auth/notify-email-change-notification";

        Context context = createContext(data);
        return this.templateEngine.process(fileName, context);
    }

    private String authNotifyEmailChangeConfirmationContent(Map<String, Object> data) {
        String fileName = "mail/auth/notify-email-change-confirmation";

        Context context = createContext(data);
        return this.templateEngine.process(fileName, context);
    }

    private String authNotifyPasswordChangeNotificationContent(Map<String, Object> data) {
        String fileName = "mail/auth/notify-password-change-notification";

        Context context = createContext(data);
        return this.templateEngine.process(fileName, context);
    }
}
