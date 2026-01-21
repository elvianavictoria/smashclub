package com.backendsyndicate.smashclub.payment.service;

import com.backendsyndicate.smashclub.payment.core.IHistory;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TransactionService implements IHistory<Object> {
    @Override
    public ResponseEntity<Object> findAll(Pageable pageable, LocalDate startDate, LocalDate endDate, HttpServletRequest request) {
        return null;
    }

    @Override
    public ResponseEntity<Object> findByCode(String code, HttpServletRequest request) {
        return null;
    }
}
