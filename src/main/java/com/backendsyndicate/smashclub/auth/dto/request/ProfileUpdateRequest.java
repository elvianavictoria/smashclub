package com.backendsyndicate.smashclub.auth.dto.request;

import jakarta.validation.constraints.Email;
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
public class ProfileUpdateRequest {

    @NotBlank(message = "Nama lengkap tidak boleh kosong")
    @Size(min = 3, max = 100, message = "Nama lengkap minimal 3 karakter dan maksimal 100 karakter")
    private String fullName;

    @Email(message = "Format email tidak valid")
    private String email;
}