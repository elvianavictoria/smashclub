package com.backendsyndicate.smashclub.auth.service;

import com.backendsyndicate.smashclub.auth.dto.request.RegisterRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ValidationService {

    public boolean isValidRegistrationData(RegisterRequest request) {
        if (request.getFullName() == null || request.getFullName().trim().length() < 3) {
            return false;
        }

        if (request.getEmail() == null || !isValidEmail(request.getEmail())) {
            return false;
        }

        if (request.getPassword() == null || !isValidPassword(request.getPassword())) {
            return false;
        }

        return true;
    }

    public boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        // Regex untuk validasi email sederhana
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    public boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        // Cek minimal 2 dari 3 kriteria: uppercase, lowercase, digit
        boolean hasUpperCase = false;
        boolean hasLowerCase = false;
        boolean hasDigit = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUpperCase = true;
            }
            if (Character.isLowerCase(c)) {
                hasLowerCase = true;
            }
            if (Character.isDigit(c)) {
                hasDigit = true;
            }
        }

        int criteriaMet = 0;
        if (hasUpperCase) criteriaMet++;
        if (hasLowerCase) criteriaMet++;
        if (hasDigit) criteriaMet++;

        return criteriaMet >= 2; // Minimal 2 kriteria terpenuhi
    }
}