package com.backendsyndicate.smashclub.admin.controller.report;

import com.backendsyndicate.smashclub.admin.dto.request.ReqAdminRefundRequestProcessDTO;
import com.backendsyndicate.smashclub.admin.service.report.AdminRefundRequestService;
import com.backendsyndicate.smashclub.admin.service.report.AdminSalesService;
import com.backendsyndicate.smashclub.common.constant.PermissionConstant;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("api/v1/admin/refund-request")
public class AdminRefundRequestController {
    @Autowired
    private AdminRefundRequestService adminRefundRequestService;

//    @PreAuthorize("hasAuthority('" + PermissionConstant.REFUND_REQUEST_READ_CODE + "')")
    @GetMapping
    public ResponseEntity<Object> refundRequestList(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam int page,
            @RequestParam int size,
            HttpServletRequest request
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return adminRefundRequestService.list(startDate, endDate, keyword, status, pageable, request);
    }

//    @PreAuthorize("hasAuthority('" + PermissionConstant.REFUND_REQUEST_EDIT_CODE + "')")
    @PostMapping("/process/{id}")
    public ResponseEntity<Object> refundRequestProcess(@PathVariable long id, @RequestBody ReqAdminRefundRequestProcessDTO dto, HttpServletRequest request) {
        return adminRefundRequestService.process(id, dto.getRefundStatus(), dto.getRefundNotes(), request);
    }
}
