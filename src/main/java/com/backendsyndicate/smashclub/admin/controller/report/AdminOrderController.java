package com.backendsyndicate.smashclub.admin.controller.report;

import com.backendsyndicate.smashclub.admin.service.report.AdminOrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/admin/order")
public class AdminOrderController {
    @Autowired
    private AdminOrderService adminOrderService;

//    @PreAuthorize("hasAuthority('" + PermissionConstant.ORDER_READ_CODE + "')")
    @GetMapping
    public ResponseEntity<Object> orderStatistic(@RequestParam int year, HttpServletRequest request) {
        return adminOrderService.statistic(year, request);
    }

//    @PreAuthorize("hasAuthority('" + PermissionConstant.ORDER_READ_CODE + "')")
    @GetMapping("list")
    public ResponseEntity<Object> orderDaily(
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) String keyword,
            HttpServletRequest request
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return adminOrderService.list(year, month, keyword, pageable, request);
    }

//    @PreAuthorize("hasAuthority('" + PermissionConstant.ORDER_DETAIL_CODE + "')")
    @GetMapping("detail/{orderCode}")
    public ResponseEntity<Object> orderDetail(@PathVariable String orderCode, HttpServletRequest request) {
        return adminOrderService.detail(orderCode, request);
    }

    //    @PreAuthorize("hasAuthority('" + PermissionConstant.ORDER_PROCESS_CODE + "')")
//    @PostMapping("process/{orderCode}")
//    public ResponseEntity<Object> orderProcess(@PathVariable String orderCode, @RequestBody int status, HttpServletRequest request) {
//        return adminOrderService.process(orderCode, status, request);
//    }
}
