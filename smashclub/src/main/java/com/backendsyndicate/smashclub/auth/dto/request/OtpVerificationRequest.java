package com.backendsyndicate.smashclub.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpVerificationRequest {

    @NotBlank(message = "User ID tidak boleh kosong")
    private String userId;

    @NotBlank(message = "OTP tidak boleh kosong")
    private String otp;
}