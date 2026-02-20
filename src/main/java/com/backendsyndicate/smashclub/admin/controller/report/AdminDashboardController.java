package com.backendsyndicate.smashclub.admin.controller.report;

import com.backendsyndicate.smashclub.admin.service.report.AdminDashboardService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/admin/dashboard")
public class AdminDashboardController {
    @Autowired
    private AdminDashboardService adminDashboardService;

    @GetMapping
    public ResponseEntity<Object> dashboard(HttpServletRequest request) {
        return adminDashboardService.dashboard(request);
    }
}
