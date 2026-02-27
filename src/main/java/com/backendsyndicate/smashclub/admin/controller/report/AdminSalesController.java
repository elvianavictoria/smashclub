package com.backendsyndicate.smashclub.admin.controller.report;

import com.backendsyndicate.smashclub.admin.service.report.AdminSalesService;
import com.backendsyndicate.smashclub.common.constant.PermissionConstant;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("api/v1/admin/sales")
public class AdminSalesController {
    @Autowired
    private AdminSalesService adminSalesService;

//    @PreAuthorize("hasAuthority('" + PermissionConstant.SALES_READ_CODE + "')")
    @GetMapping
    public ResponseEntity<Object> salesStatistic(@RequestParam int year, HttpServletRequest request) {
        return adminSalesService.statistic(year, request);
    }

//    @PreAuthorize("hasAuthority('" + PermissionConstant.SALES_READ_CODE + "')")
    @GetMapping("list")
    public ResponseEntity<Object> salesDaily(
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam String keyword,
            HttpServletRequest request
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("CreatedAt").descending());
        return adminSalesService.list(year, month, keyword, pageable, request);
    }

//    @PreAuthorize("hasAuthority('" + PermissionConstant.SALES_DETAIL_CODE + "')")
    @GetMapping("detail/{transactionCode}")
    public ResponseEntity<Object> salesDetail(@PathVariable String transactionCode, HttpServletRequest request) {
        return adminSalesService.detail(transactionCode, request);
    }
}
