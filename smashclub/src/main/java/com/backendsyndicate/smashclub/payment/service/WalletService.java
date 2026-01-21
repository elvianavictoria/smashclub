package com.backendsyndicate.smashclub.payment.service;

import com.backendsyndicate.smashclub.payment.core.IWallet;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WalletService implements IWallet {
    @Override
    public ResponseEntity<Object> getBalance(Long userId, HttpServletRequest request) {
        return null;
    }

    @Override
    public ResponseEntity<Object> getBalanceLog(Long userId, HttpServletRequest request) {
        return null;
    }

    @Override
    public boolean updateBalance(Long userId, boolean isAddition, double value) {
        return false;
    }
}
