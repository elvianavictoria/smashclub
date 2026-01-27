package com.backendsyndicate.smashclub.payment.repo;

import com.backendsyndicate.smashclub.payment.model.WalletLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WalletLogRepo extends JpaRepository<WalletLog, Long> {
    Page<WalletLog> findByUserIdAndCreatedAtBetween(String userId, LocalDate startDate, LocalDate endDate, Pageable page);
}
