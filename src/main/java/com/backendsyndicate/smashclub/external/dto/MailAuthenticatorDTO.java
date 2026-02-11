package com.backendsyndicate.smashclub.external.dto;

import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;

public class MailAuthenticatorDTO extends Authenticator {
    private String username;
    private String password;

    public MailAuthenticatorDTO(String user, String pass) {
        this.username = user;
        this.password = pass;
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, password);
    }
}
