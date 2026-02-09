package com.backendsyndicate.smashclub.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OtpVerificationRequest {

    @NotBlank(message = "User ID harus diisi")
    private String userId;

    @NotBlank(message = "OTP harus diisi")
    @Size(min = 6, max = 6, message = "OTP harus 6 digit")
    @Pattern(regexp = "\\d{6}", message = "OTP harus berupa angka")
    private String otp;
}