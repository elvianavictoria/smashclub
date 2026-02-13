package com.backendsyndicate.smashclub.external.config;

import com.backendsyndicate.smashclub.common.security.Crypto;
import com.cloudinary.Cloudinary;
import com.xendit.XenditClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:xendit.properties")
public class XenditConfig {
    private static String secretKey;
    private static String publicKey;
    private static char useInvoice;

    private static String successRedirectUrl;
    private static String failedRedirectUrl;

    public static String getSecretKey() {
        return secretKey;
    }

    @Value("${xendit.api.secret-key}")
    private void setSecretKey(String secretKey) {
        XenditConfig.secretKey = Crypto.performDecrypt(secretKey);
    }

    public static String getPublicKey() {
        return publicKey;
    }

    @Value("${xendit.api.public-key}")
    private void setPublicKey(String publicKey) {
        XenditConfig.publicKey = Crypto.performDecrypt(publicKey);
    }

    public static char getUseInvoice() {
        return useInvoice;
    }

    @Value("${xendit.api.use-invoice}")
    private void setUseInvoice(char useInvoice) {
        XenditConfig.useInvoice = useInvoice;
    }

    public static String getSuccessRedirectUrl() {
        return successRedirectUrl;
    }

    @Value("${xendit.url.redirect-success}")
    private void setSuccessRedirectUrl(String successRedirectUrl) {
        XenditConfig.successRedirectUrl = successRedirectUrl;
    }

    public static String getFailedRedirectUrl() {
        return failedRedirectUrl;
    }

    @Value("${xendit.url.redirect-failed}")
    private void setFailedRedirectUrl(String failedRedirectUrl) {
        XenditConfig.failedRedirectUrl = failedRedirectUrl;
    }

    @Bean
    public XenditClient xenditClient() {
        return new XenditClient.Builder()
                .setApikey(this.secretKey)
                .build();
    }
}
