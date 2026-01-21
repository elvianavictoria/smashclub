package com.backendsyndicate.smashclub.payment.controller;

import com.backendsyndicate.smashclub.common.util.GlobalResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(name="wallet")
public class WalletController {
    @GetMapping("/balance")
    public ResponseEntity<Object> getBalance(HttpServletRequest request) {
        return GlobalResponse.notFound("DATA TIDAK DITEMUKAN!", "BLNC-E01", request);
    }

    @GetMapping("/balance/log")
    public ResponseEntity<Object> getBalanceLog(HttpServletRequest request) {
        return GlobalResponse.notFound("DATA TIDAK DITEMUKAN!", "BLNC-E02", request);
    }
}
