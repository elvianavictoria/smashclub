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
            // Cek format header
            if (authorizationHeader == null) {
                log.warn("Authorization header is null");
                return null;
            }

            if (!authorizationHeader.startsWith("Bearer ")) {
                log.warn("Authorization header does not start with Bearer");
                return null;
            }

            // Extract token
            String token = authorizationHeader.substring(7);
            if (token.trim().isEmpty()) {
                log.warn("Token is empty after Bearer prefix");
                return null;
            }

            // Dapatkan user ID
            String userId = authService.getUserIdFromToken(token);

            // LOG UNTUK TRACKING
            if (userId == null) {
                log.warn("getUserIdFromToken returned null for token: {}", token);
            } else {
                log.debug("Successfully extracted userId: {}", userId);
            }
            return userId;

        } catch (Exception e) {
            log.error("Unexpected error extracting user ID: {}", e.getMessage());
            return null;
        }
    }

    // ProfileController.java

    @PostMapping("/profile-picture")
    public ResponseEntity<Object> uploadProfilePicture(
            @RequestHeader("Authorization") String authorizationHeader,
            @ModelAttribute ProfilePictureRequest request,  // ← Pakai @ModelAttribute untuk multipart
            HttpServletRequest httpRequest
    ) {
        String userId = extractUserIdFromToken(authorizationHeader);
        if (userId == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return authService.uploadProfilePicture(userId, request.getProfilePicture(), httpRequest);
    }

    @DeleteMapping("/profile-picture")
    public ResponseEntity<Object> deleteProfilePicture(
            @RequestHeader("Authorization") String authorizationHeader,
            HttpServletRequest httpRequest
    ) {
        String userId = extractUserIdFromToken(authorizationHeader);
        if (userId == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return authService.deleteProfilePicture(userId, httpRequest);
    }
}