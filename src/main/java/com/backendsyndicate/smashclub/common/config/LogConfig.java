package com.backendsyndicate.smashclub.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:log.properties")
public class LogConfig {
    private static boolean enableLog;

    public static boolean isEnableLog() {
        return enableLog;
    }

    @Value("${log.enable}")
    public void setEnableLog(boolean enableLog) {
        this.enableLog = enableLog;
    }

}
