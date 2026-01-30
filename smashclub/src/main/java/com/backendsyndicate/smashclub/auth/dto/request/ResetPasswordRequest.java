package com.backendsyndicate.smashclub.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest {

    @NotBlank(message = "Token tidak boleh kosong")
    private String token;

    @NotBlank(message = "Password baru tidak boleh kosong")
    @Size(min = 8, message = "Password minimal 8 karakter")
    private String newPassword;
}