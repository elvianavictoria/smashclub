package com.backendsyndicate.smashclub.admin.controller;

import com.backendsyndicate.smashclub.admin.dto.request.ReqLoginDTO;
import com.backendsyndicate.smashclub.admin.service.AdminAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("admin/auth")
public class AdminAuthController {
    @Autowired
    private AdminAuthService adminAuthService;

    @PostMapping
    public ResponseEntity<Object> authenticate(HttpServletRequest request) {
        String authToken = "";
        return adminAuthService.isAuthenticated(authToken, request);
    }

    @PostMapping("login")
    public ResponseEntity<Object> login(@Valid @RequestBody ReqLoginDTO dto, HttpServletRequest request) {
        return adminAuthService.login(dto.getUsername(), dto.getPassword(), request);
    }

    @PostMapping("logout")
    public ResponseEntity<Object> logout(HttpServletRequest request) {
        String authToken = "";
        return adminAuthService.logout(authToken, request);
    }
}
