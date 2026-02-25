package com.backendsyndicate.smashclub.admin.controller.report;

import com.backendsyndicate.smashclub.admin.service.report.AdminBookingService;
import com.backendsyndicate.smashclub.common.constant.PermissionConstant;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/admin/booking")
public class AdminBookingController {
    @Autowired
    private AdminBookingService adminBookingService;

    @PreAuthorize("hasAuthority('" + PermissionConstant.BOOKING_SALES_READ_CODE + "')")
    @GetMapping
    public ResponseEntity<Object> bookingStatistic(@RequestParam int year, HttpServletRequest request) {
        return adminBookingService.statistic(year, request);
    }

    @PreAuthorize("hasAuthority('" + PermissionConstant.BOOKING_SALES_READ_CODE + "')")
    @GetMapping("list")
    public ResponseEntity<Object> bookingDaily(
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) String keyword,
            HttpServletRequest request
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return adminBookingService.list(year, month, keyword, pageable, request);
    }

    @PreAuthorize("hasAuthority('" + PermissionConstant.BOOKING_SALES_DETAIL_CODE + "')")
    @GetMapping("detail/{bookingCode}")
    public ResponseEntity<Object> bookingDetail(@PathVariable String bookingCode, HttpServletRequest request) {
        return adminBookingService.detail(bookingCode, request);
    }

    @PreAuthorize("hasAuthority('" + PermissionConstant.BOOKING_SALES_PROCESS_CODE + "')")
    @PostMapping("process/{bookingCode}")
    public ResponseEntity<Object> bookingProcess(@PathVariable String bookingCode, @RequestBody int status, HttpServletRequest request) {
        return adminBookingService.process(bookingCode, status, request);
    }
}
