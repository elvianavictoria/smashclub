package com.backendsyndicate.smashclub.auth.controller;

import com.backendsyndicate.smashclub.auth.dto.request.*;
import com.backendsyndicate.smashclub.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
@Slf4j
public class ProfileController {

    private final AuthService authService;

    // ============ GET PROFILE ============
    @GetMapping
    public ResponseEntity<Object> getProfile(
            @RequestHeader("Authorization") String authorizationHeader,
            HttpServletRequest httpRequest
    ) {
        String userId = extractUserIdFromToken(authorizationHeader);
        if (userId == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return authService.getProfile(userId, httpRequest);
    }

    // ============ UPDATE PROFILE ============
    @PutMapping
    public ResponseEntity<Object> updateProfile(
            @RequestHeader("Authorization") String authorizationHeader,
            @Valid @RequestBody ProfileUpdateRequest request,
            HttpServletRequest httpRequest
    ) {
        String userId = extractUserIdFromToken(authorizationHeader);
        if (userId == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return authService.updateProfile(userId, request, httpRequest);
    }

    // ============ CHANGE PASSWORD ============
    @PostMapping("/change-password")
    public ResponseEntity<Object> changePassword(
            @RequestHeader("Authorization") String authorizationHeader,
            @Valid @RequestBody ChangePasswordRequest request,
            HttpServletRequest httpRequest
    ) {
        String userId = extractUserIdFromToken(authorizationHeader);
        if (userId == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return authService.changePassword(userId, request, httpRequest);
    }

    // ============ VERIFY EMAIL CHANGE ============
    @PostMapping("/verify-email-change")
    public ResponseEntity<Object> verifyEmailChange(
            @Valid @RequestBody VerifyEmailChangeRequest request,
            HttpServletRequest httpRequest
    ) {
        return authService.verifyEmailChange(request, httpRequest);
    }

    // ============ CANCEL PENDING EMAIL CHANGE ============
    @PostMapping("/cancel-email-change")
    public ResponseEntity<Object> cancelEmailChange(
            @RequestHeader("Authorization") String authorizationHeader,
            HttpServletRequest httpRequest
    ) {
        String userId = extractUserIdFromToken(authorizationHeader);
        if (userId == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return authService.cancelEmailChange(userId, httpRequest);
    }

    // ============ HELPER METHOD ============
    private String extractUserIdFromToken(String authorizationHeader) {
        try {
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String token = authorizationHeader.substring(7);
                // Extract user ID from JWT token
                return authService.getUserIdFromToken(token); // Butuh method di AuthService
            }
        } catch (Exception e) {
            log.error("Error extracting user ID from token: {}", e.getMessage());
        }
        return null;
    }
}