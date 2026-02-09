package com.backendsyndicate.smashclub.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequest {

    @NotBlank(message = "Token harus diisi")
    private String token;

    @NotBlank(message = "Password baru harus diisi")
    @Size(min = 8, message = "Password baru minimal 8 karakter")
    private String newPassword;
}