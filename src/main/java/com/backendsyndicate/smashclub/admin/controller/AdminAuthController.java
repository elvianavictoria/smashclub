package com.backendsyndicate.smashclub.admin.controller;

import com.backendsyndicate.smashclub.admin.dto.request.ReqAdminLoginDTO;
import com.backendsyndicate.smashclub.admin.dto.request.ReqAdminUserSaveDTO;
import com.backendsyndicate.smashclub.admin.dto.validation.ValAdminUserRoleDTO;
import com.backendsyndicate.smashclub.admin.model.AdminUser;
import com.backendsyndicate.smashclub.admin.service.AdminAuthService;
import com.backendsyndicate.smashclub.common.util.GlobalResponse;
import com.backendsyndicate.smashclub.common.util.Util;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/v1/admin/auth")
public class AdminAuthController {
    @Autowired
    private AdminAuthService adminAuthService;
    private ModelMapper modelMapper = new ModelMapper();

    @PostMapping
    public ResponseEntity<Object> authenticate(HttpServletRequest request) {
        String authToken = request.getHeader("Authorization");
        authToken = authToken.replaceAll("Bearer ", "");

        return adminAuthService.isAuthenticated(authToken, request);
    }

    @PostMapping("login")
    public ResponseEntity<Object> login(@Valid @RequestBody ReqAdminLoginDTO dto, HttpServletRequest request) {
        return adminAuthService.login(dto.getUsername(), dto.getPassword(), request);
    }

    @PostMapping("logout")
    public ResponseEntity<Object> logout(HttpServletRequest request) {
        String authToken = "";
        return adminAuthService.logout(authToken, request);
    }

    // Edit AdminUser Profile
    @PutMapping("update")
    public ResponseEntity<Object> updateProfile(
            @RequestParam(required = false) MultipartFile profilePicture,
            @RequestParam String username,
            @RequestParam String fullName,
            @RequestParam(required = false) String password,
            HttpServletRequest request
    ) {
        String authToken = request.getHeader("Authorization");
        authToken = authToken.replaceAll("Bearer ", "");

        ReqAdminUserSaveDTO dto = new ReqAdminUserSaveDTO();
        dto.setUsername(username);
        dto.setFullName(fullName);
        dto.setPassword(password);
        dto.validate();

        if( !dto.isValidated() ) {
            return GlobalResponse.failed("Format tidak valid!", "X01001", dto.getValidation(), request);
        }

        AdminUser user = modelMapper.map(dto, AdminUser.class);
        return adminAuthService.update(authToken, user, profilePicture, request);
    }
}
