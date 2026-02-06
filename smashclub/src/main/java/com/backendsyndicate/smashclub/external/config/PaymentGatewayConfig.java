package com.backendsyndicate.smashclub.external.config;

import com.backendsyndicate.smashclub.common.security.Crypto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:payment-gateway.properties")
public class PaymentGatewayConfig {
    private static String xenditSecretKey;
    private static String xenditPublicKey;
    private static char useInvoice;

    private static String successRedirectUrl;
    private static String failedRedirectUrl;

    public static String getXenditSecretKey() {
        return xenditSecretKey;
    }

    @Value("${xendit.api.secret-key}")
    private void setXenditSecretKey(String xenditSecretKey) {
        PaymentGatewayConfig.xenditSecretKey = Crypto.performDecrypt(xenditSecretKey);
    }

    public static String getXenditPublicKey() {
        return xenditPublicKey;
    }

    @Value("${xendit.api.public-key}")
    private void setXenditPublicKey(String xenditPublicKey) {
        PaymentGatewayConfig.xenditPublicKey = Crypto.performDecrypt(xenditPublicKey);
    }

    public static char getUseInvoice() {
        return useInvoice;
    }

    @Value("${xendit.api.use-invoice}")
    private void setUseInvoice(char useInvoice) {
        PaymentGatewayConfig.useInvoice = useInvoice;
    }

    public static String getSuccessRedirectUrl() {
        return successRedirectUrl;
    }

    @Value("${xendit.url.redirect-success}")
    private void setSuccessRedirectUrl(String successRedirectUrl) {
        PaymentGatewayConfig.successRedirectUrl = successRedirectUrl;
    }

    public static String getFailedRedirectUrl() {
        return failedRedirectUrl;
    }

    @Value("${xendit.url.redirect-failed}")
    private void setFailedRedirectUrl(String failedRedirectUrl) {
        PaymentGatewayConfig.failedRedirectUrl = failedRedirectUrl;
    }
}
