package com.backendsyndicate.smashclub.payment.core;

import com.backendsyndicate.smashclub.payment.dto.request.ReqUpdateBalanceDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

public interface IWallet {
    public ResponseEntity<Object> getBalance(String userId, HttpServletRequest request);
    public ResponseEntity<Object> getBalanceLog(String userId, LocalDate startDate, LocalDate endDate, Pageable pageable, HttpServletRequest request);
    public ResponseEntity<Object> updateBalance(String userId, ReqUpdateBalanceDTO reqUpdateBalanceDTO, HttpServletRequest request);
}
