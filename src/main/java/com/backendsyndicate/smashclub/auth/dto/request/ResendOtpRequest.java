package com.backendsyndicate.smashclub.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResendOtpRequest {

    @NotBlank(message = "User ID harus diisi")
    private String userId;
}