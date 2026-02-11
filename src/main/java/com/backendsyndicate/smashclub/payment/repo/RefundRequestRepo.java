package com.backendsyndicate.smashclub.payment.repo;

import com.backendsyndicate.smashclub.payment.model.RefundRequest;
import com.backendsyndicate.smashclub.payment.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface RefundRequestRepo extends JpaRepository<RefundRequest, Long> {
    // Refund Request Filter by Date and Status
    Page<RefundRequest> findAllByCreatedAtBetweenAndRefundStatus(LocalDateTime startDate, LocalDateTime endDate, byte refundStatus, Pageable pageable);
    // Refund Request Filter by Date
    Page<RefundRequest> findAllByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
}
