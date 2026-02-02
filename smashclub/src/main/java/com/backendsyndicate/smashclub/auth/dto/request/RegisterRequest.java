package com.backendsyndicate.smashclub.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Nama lengkap harus diisi")
    @Size(min = 3, max = 100, message = "Nama lengkap minimal 3 karakter dan maksimal 100 karakter")
    private String fullName;

    @NotBlank(message = "Email harus diisi")
    @Email(message = "Format email tidak valid")
    private String email;

    @NotBlank(message = "Password harus diisi")
    @Size(min = 8, message = "Password minimal 8 karakter")
    private String password;
}