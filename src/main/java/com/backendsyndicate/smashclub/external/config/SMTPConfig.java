package com.backendsyndicate.smashclub.external.config;

import com.backendsyndicate.smashclub.common.security.Crypto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Properties;

@Configuration
@PropertySource("classpath:smtp.properties")
public class SMTPConfig {
    private static String username;
    private static String password;
    private static String host;
    private static int port;
    private static int portSSL;
    private static int portTLS;
    private static boolean auth;
    private static boolean startTlsEnable;
    private static String smtpSocketFactoryClass;
    private static int smtpTimeout;

    public static String getUsername() {
        return username;
    }

    @Value("${email.username}")
    private void setUsername(String username) {
        this.username = Crypto.performDecrypt(username);
    }

    public static String getPassword() {
        return password;
    }

    @Value("${email.password}")
    private void setPassword(String password) {
        this.password = Crypto.performDecrypt(password);
    }

    public static String getHost() {
        return host;
    }

    @Value("${email.host}")
    private void setHost(String host) {
        this.host = host;
    }

    public static int getPort() {
        return port;
    }

    @Value("${email.port}")
    private void setPort(int port) {
        this.port = port;
    }

    public static int getPortSSL() {
        return portSSL;
    }

    @Value("${email.port.ssl}")
    private void setPortSSL(int portSSL) {
        this.portSSL = portSSL;
    }

    public static int getPortTLS() {
        return portTLS;
    }

    @Value("${email.port.tls}")
    private void setPortTLS(int portTLS) {
        this.portTLS = portTLS;
    }

    public static boolean isAuth() {
        return auth;
    }

    @Value("${email.auth}")
    private void setAuth(boolean auth) {
        this.auth = auth;
    }

    public static boolean isStartTlsEnable() {
        return startTlsEnable;
    }

    @Value("${email.starttls.enable}")
    private void setStartTlsEnable(boolean startTlsEnable) {
        this.startTlsEnable = startTlsEnable;
    }

    public static String getSmtpSocketFactoryClass() {
        return smtpSocketFactoryClass;
    }

    @Value("${email.smtp.socket.factory.class}")
    private void setSmtpSocketFactoryClass(String smtpSocketFactoryClass) {
        this.smtpSocketFactoryClass = smtpSocketFactoryClass;
    }

    public static int getSmtpTimeout() {
        return smtpTimeout;
    }

    @Value("${email.smtp.timeout}")
    private static void setSmtpTimeout(int smtpTimeout) {
        SMTPConfig.smtpTimeout = smtpTimeout;
    }
}
