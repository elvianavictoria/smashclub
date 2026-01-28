package com.backendsyndicate.smashclub.payment.repo;

import com.backendsyndicate.smashclub.payment.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Optional;

public interface TransactionRepo extends JpaRepository<Transaction, Long> {
    @Query(value="SELECT COUNT(t) FROM Transaction t WHERE cast(t.createdAt as string) LIKE CONCAT(CURRENT_DATE, '%')")
    Long countTodayTransaction();

    Page<Transaction> findByCreatedAtBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

    Optional<Transaction> findByTransactionCode(String transactionCode);
}
