package com.backendsyndicate.smashclub.admin.core;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface IAuth {
    public ResponseEntity<Object> login(String username, String password, HttpServletRequest request);
    public ResponseEntity<Object> logout(String authToken, HttpServletRequest request);
    public ResponseEntity<Object> isAuthenticated(String authToken, HttpServletRequest request);
}
