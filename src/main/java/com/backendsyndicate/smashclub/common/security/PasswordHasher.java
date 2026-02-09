package com.backendsyndicate.smashclub.common.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordHasher {

    private final PasswordEncoder passwordEncoder;

    public PasswordHasher() {
        // BCrypt dengan strength 12 (recommended for 2024)
        this.passwordEncoder = new BCryptPasswordEncoder(12);
    }

    /**
     * Hash password untuk disimpan di database
     * @param plainPassword password plain text dari user
     * @return hashed password (bcrypt format)
     */
    public String hash(String plainPassword) {
        return passwordEncoder.encode(plainPassword);
    }

    /**
     * Verifikasi password user dengan hash di database
     * @param plainPassword password dari input user
     * @param hashedPassword hash yang disimpan di database
     * @return true jika cocok, false jika tidak
     */
    public boolean verify(String plainPassword, String hashedPassword) {
        return passwordEncoder.matches(plainPassword, hashedPassword);
    }

    /**
     * Cek apakah string adalah hash BCrypt valid
     * @param hash string yang akan dicek
     * @return true jika format BCrypt valid
     */
    public boolean isValidBcryptHash(String hash) {
        if (hash == null || hash.length() < 10) return false;
        // Format BCrypt: $2a$, $2b$, $2y$ + cost + salt + hash
        return hash.startsWith("$2a$") || hash.startsWith("$2b$") || hash.startsWith("$2y$");
    }
}