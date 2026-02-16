package com.backendsyndicate.smashclub.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:other.properties")
public class OtherConfig {
    private static String corsUrlAllowed;

    public static String getCorsUrlAllowed() {
        return corsUrlAllowed;
    }

    @Value("${cors.url.allowed}")
    private void setCorsUrlAllowed(String corsUrlAllowed) {
        this.corsUrlAllowed = corsUrlAllowed;
    }
}
