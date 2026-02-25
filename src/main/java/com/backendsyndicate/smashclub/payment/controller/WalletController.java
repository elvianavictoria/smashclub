package com.backendsyndicate.smashclub.payment.controller;

import com.backendsyndicate.smashclub.common.security.JwtService;
import com.backendsyndicate.smashclub.payment.dto.request.ReqGetBalanceLogDTO;
import com.backendsyndicate.smashclub.payment.dto.request.ReqTopupBalanceDTO;
import com.backendsyndicate.smashclub.payment.service.WalletService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("api/v1/wallet")
public class WalletController {
    @Autowired
    private WalletService walletService;
    @Autowired
    private JwtService jwtService;

    @GetMapping("/balance")
    public ResponseEntity<Object> getBalance(HttpServletRequest request) {
        String accessToken = request.getHeader("Authorization");
        String userId = jwtService.extractUserId(accessToken.replaceAll("Bearer ", ""));

        return walletService.getBalance(userId, request);
    }

    @GetMapping("/balance/log")
    public ResponseEntity<Object> getBalanceLog(@Valid @RequestParam ReqGetBalanceLogDTO dto, HttpServletRequest request) {
        String accessToken = request.getHeader("Authorization");
        String userId = jwtService.extractUserId(accessToken.replaceAll("Bearer ", ""));
        Pageable page = PageRequest.of(dto.getPage(), dto.getSize(), Sort.by("CreatedAt").descending());

        return walletService.getBalanceLog(userId, dto.getStartDate(), dto.getEndDate(), page, request);
    }

    @PostMapping("/balance/topup")
    public ResponseEntity<Object> topupBalance(@RequestBody ReqTopupBalanceDTO dto, HttpServletRequest request) {
        String accessToken = request.getHeader("Authorization");
        String userId = jwtService.extractUserId(accessToken.replaceAll("Bearer ", ""));

        return walletService.topupBalance(userId, dto.getBalance(), request);
    }
}
