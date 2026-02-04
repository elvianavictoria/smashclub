package com.backendsyndicate.smashclub.admin.controller.internal;

import com.backendsyndicate.smashclub.admin.service.internal.AdminPermissionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("admin/permissions")
public class AdminPermissionController {
    @Autowired
    private AdminPermissionService adminPermissionService;

    @GetMapping
    public ResponseEntity<Object> adminPermissionList(HttpServletRequest request) {
        return adminPermissionService.findAll(request);
    }
}
