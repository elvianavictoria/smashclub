package com.backendsyndicate.smashclub.admin.core;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

public interface IStatistic {
    public ResponseEntity<Object> statistic(int yearStart, HttpServletRequest request);
    public ResponseEntity<Object> list(int yearStart, int monthStart, HttpServletRequest request);
}
