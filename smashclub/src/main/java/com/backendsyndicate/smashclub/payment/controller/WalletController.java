package com.backendsyndicate.smashclub.payment.controller;

import com.backendsyndicate.smashclub.common.util.GlobalResponse;
import com.backendsyndicate.smashclub.payment.dto.request.ReqGetBalanceLogDTO;
import com.backendsyndicate.smashclub.payment.service.WalletService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping(name="wallet")
public class WalletController {
    private WalletService walletService;

    @GetMapping("/balance")
    public ResponseEntity<Object> getBalance(HttpServletRequest request) {
        String userId = "";
        return walletService.getBalance(userId, request);
    }

    @GetMapping("/balance/log")
    public ResponseEntity<Object> getBalanceLog(@Valid @RequestBody ReqGetBalanceLogDTO dto, HttpServletRequest request) {
        String userId = "";
        Pageable page = PageRequest.of(dto.getPage(), dto.getSize(), Sort.by("CreatedAt").descending());
        return walletService.getBalanceLog(userId, dto.getStartDate(), dto.getEndDate(), page, request);
    }

    @PostMapping("/balance/topup")
    public ResponseEntity<Object> topupBalance(@RequestBody BigDecimal balance, HttpServletRequest request) {
        String userId = "";
        return walletService.topupBalance(userId, balance, request);
    }
}
