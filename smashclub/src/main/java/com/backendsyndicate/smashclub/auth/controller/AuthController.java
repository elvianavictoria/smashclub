package com.backendsyndicate.smashclub.auth.controller;

import com.backendsyndicate.smashclub.auth.dto.ApiResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @PostMapping("/register")
    public ApiResponse register() {
        return new ApiResponse("SUCCESS", "Register endpoint hit", null);
    }

    @PostMapping("/login")
    public ApiResponse login() {
        return new ApiResponse("SUCCESS", "Login endpoint hit", null);
    }

    @PostMapping("/forgot-password")
    public ApiResponse forgotPassword() {
        return new ApiResponse("SUCCESS", "Forgot password endpoint hit", null);
    }
}