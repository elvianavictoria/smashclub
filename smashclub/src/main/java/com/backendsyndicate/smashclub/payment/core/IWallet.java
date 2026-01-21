package com.backendsyndicate.smashclub.payment.core;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface IWallet {
    public ResponseEntity<Object> getBalance(Long userId, HttpServletRequest request);
    public ResponseEntity<Object> getBalanceLog(Long userId, HttpServletRequest request);
    public boolean updateBalance(Long userId, boolean isAddition, double value);
}
