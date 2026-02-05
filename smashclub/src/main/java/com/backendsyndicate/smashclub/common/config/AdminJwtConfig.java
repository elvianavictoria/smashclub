package com.backendsyndicate.smashclub.common.config;
import com.backendsyndicate.smashclub.common.util.Logging;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:jwt.properties")
public class AdminJwtConfig {
    private static String secretKey;
    private static int expirationHours;
    private static String issuer;
    private static String audience;
    private static String enableEncrypt;

    public static String getSecretKey() {
        return secretKey;
    }

    @Value("${jwt.admin.secret-key}")
    private void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public static int getExpirationHours() {
        return expirationHours;
    }

    @Value("${jwt.admin.expiration-hours}")
    private void setExpirationHours(int expirationHours) {
        this.expirationHours = expirationHours;
    }

    public static String getIssuer() {
        return issuer;
    }

    @Value("${jwt.admin.issuer}")
    private void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public static String getAudience() {
        return audience;
    }

    @Value("${jwt.admin.audience}")
    private void setAudience(String audience) {
        this.audience = audience;
    }

    public static String getEnableEncrypt() {
        return enableEncrypt;
    }

    @Value("${jwt.enable.encrypt}")
    private void setEnableEncrypt(String enableEncrypt) {
        this.enableEncrypt = enableEncrypt;
    }
}
