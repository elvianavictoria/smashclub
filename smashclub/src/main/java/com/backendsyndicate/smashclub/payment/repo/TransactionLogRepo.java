package com.backendsyndicate.smashclub.payment.repo;

import com.backendsyndicate.smashclub.payment.model.Transaction;
import com.backendsyndicate.smashclub.payment.model.TransactionLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Optional;

public interface TransactionLogRepo extends JpaRepository<TransactionLog, Long> {
    Page<TransactionLog> findByCreatedAtBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);
}
