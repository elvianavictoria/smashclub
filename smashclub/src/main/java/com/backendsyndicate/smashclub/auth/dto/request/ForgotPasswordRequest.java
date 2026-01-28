package com.backendsyndicate.smashclub.auth.dto;

import lombok.Data;

@Data
public class ForgotPasswordRequest {
    private String email;
}