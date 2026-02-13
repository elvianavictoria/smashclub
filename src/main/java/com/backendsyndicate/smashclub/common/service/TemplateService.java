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
    public static final String TEMPLATE_REFUND_NOTIFY_APPROVED = "REFUND_NOTIFY_APPROVED";
    public static final String TEMPLATE_REFUND_NOTIFY_REJECTED = "REFUND_NOTIFY_REJECTED";

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
            default:
                break;
        }

        return emailContent;
    }

    public String paymentNotifyUnpaidContent(Map<String, Object> data) {
        String fileName = "mail/notify-payment-unpaid";

        Context context = createContext(data);
        return this.templateEngine.process(fileName, context);
    }

    public String paymentNotifyPaidContent(Map<String, Object> data) {
        String fileName = "mail/notify-payment-paid";

        Context context = createContext(data);
        return this.templateEngine.process(fileName, context);
    }
}
