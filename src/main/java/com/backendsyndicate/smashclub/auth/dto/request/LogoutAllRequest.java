package com.backendsyndicate.smashclub.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LogoutAllRequest {

    @NotBlank(message = "User ID harus diisi")
    private String userId;
}