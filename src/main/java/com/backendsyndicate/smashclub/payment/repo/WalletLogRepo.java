package com.backendsyndicate.smashclub.payment.repo;

import com.backendsyndicate.smashclub.payment.model.WalletLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface WalletLogRepo extends JpaRepository<WalletLog, Long> {
    Page<WalletLog> findByWallet_UserIdAndCreatedAtBetween(String userId, LocalDateTime startDate, LocalDateTime endDate, Pageable page);
    @Query(value="SELECT COUNT(l) FROM WalletLog l WHERE cast(l.createdAt as string) LIKE CONCAT(CURRENT_DATE, '%')")
    Long countTodayLog();
}
