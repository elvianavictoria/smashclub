package com.backendsyndicate.smashclub.auth.service;

import com.backendsyndicate.smashclub.auth.dto.request.RegisterRequest;
import com.backendsyndicate.smashclub.common.util.ValidationError;
import com.backendsyndicate.smashclub.common.util.CustomRegex;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ValidationService {

    public List<ValidationError> validateRegistrationData(RegisterRequest request) {
        List<ValidationError> errors = new ArrayList<>();

        // Validasi Full Name dengan CustomRegex
        if (request.getFullName() == null || request.getFullName().trim().isEmpty()) {
            errors.add(ValidationError.builder()
                    .field("fullName")
                    .message("Nama lengkap harus diisi")
                    .rejected_value(request.getFullName())
                    .build());
        } else {
            String fullName = request.getFullName().trim();
            if (fullName.length() < 3 || fullName.length() > 100) {
                errors.add(ValidationError.builder()
                        .field("fullName")
                        .message("Nama lengkap minimal 3 karakter dan maksimal 100 karakter")
                        .rejected_value(fullName)
                        .build());
            } else if (!CustomRegex.stringRegex(fullName, 3, 100)) {
                errors.add(ValidationError.builder()
                        .field("fullName")
                        .message("Nama lengkap hanya boleh mengandung huruf dan spasi")
                        .rejected_value(fullName)
                        .build());
            }
        }

        // Validasi Email
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            errors.add(ValidationError.builder()
                    .field("email")
                    .message("Email harus diisi")
                    .rejected_value(request.getEmail())
                    .build());
        } else {
            String email = request.getEmail().toLowerCase().trim();
            if (!isValidEmail(email)) {
                errors.add(ValidationError.builder()
                        .field("email")
                        .message("Format email tidak valid")
                        .rejected_value(email)
                        .build());
            }
        }

        // Validasi Password
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            errors.add(ValidationError.builder()
                    .field("password")
                    .message("Password harus diisi")
                    .rejected_value("******")
                    .build());
        } else {
            String password = request.getPassword();

            // Cek panjang minimal
            if (password.length() < 8) {
                errors.add(ValidationError.builder()
                        .field("password")
                        .message("Password minimal 8 karakter")
                        .rejected_value("******")
                        .build());
            } else {
                // Cek kompleksitas password
                boolean hasUpperCase = false;
                boolean hasLowerCase = false;
                boolean hasDigit = false;

                for (char c : password.toCharArray()) {
                    if (Character.isUpperCase(c)) hasUpperCase = true;
                    if (Character.isLowerCase(c)) hasLowerCase = true;
                    if (Character.isDigit(c)) hasDigit = true;
                }

                int criteriaMet = 0;
                if (hasUpperCase) criteriaMet++;
                if (hasLowerCase) criteriaMet++;
                if (hasDigit) criteriaMet++;

                if (criteriaMet < 2) {
                    errors.add(ValidationError.builder()
                            .field("password")
                            .message("Password harus mengandung minimal 2 dari: huruf besar, huruf kecil, angka")
                            .rejected_value("******")
                            .build());
                }
            }
        }

        return errors;
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        // Regex untuk email (bisa pakai dari RegexConstant kalau ada)
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }
}