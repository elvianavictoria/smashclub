package com.backendsyndicate.smashclub.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequest {

    @NotBlank(message = "Password saat ini harus diisi")
    private String currentPassword;

    @NotBlank(message = "Password baru harus diisi")
    @Size(min = 8, message = "Password baru minimal 8 karakter")
    private String newPassword;

    private boolean logoutOtherDevices = false;
}