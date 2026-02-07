package com.backendsyndicate.smashclub.external.config;

import com.backendsyndicate.smashclub.common.security.Crypto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:cloudinary.properties")
public class CloudinaryConfig {
    private static String apiKey;
    private static String apiSecret;
    private static String cloudName;

    @Value("${cloudinary.api.key}")
    private void setApiKey(String apiKey) {
        CloudinaryConfig.apiKey = Crypto.performDecrypt(apiKey);
    }

    @Value("${cloudinary.api.secret}")
    private void setApiSecret(String apiSecret) {
        CloudinaryConfig.apiSecret = Crypto.performDecrypt(apiSecret);
    }

    @Value("${cloudinary.api.cloud-name}")
    private void setCloudName(String cloudName) {
        CloudinaryConfig.cloudName = Crypto.performDecrypt(cloudName);
    }

    public static String getCloudinaryUrl() {
        return "cloudinary://" + apiKey + ":" + apiSecret + "@" + cloudName;
    }
}
