package com.backendsyndicate.smashclub.payment.core;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

public interface IHistory {
    public ResponseEntity<Object> findAll(Pageable pageable, LocalDate startDate, LocalDate endDate, HttpServletRequest request);
    public ResponseEntity<Object> findByCode(String code, HttpServletRequest request);
}
