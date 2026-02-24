package com.backendsyndicate.smashclub.payment.repo;

import com.backendsyndicate.smashclub.payment.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TransactionRepo extends JpaRepository<Transaction, Long> {
    @Query(value="SELECT COUNT(t) FROM Transaction t WHERE cast(t.createdAt as string) LIKE CONCAT(CURRENT_DATE, '%')")
    Long countTodayTransaction();

    // Transaction Filter by Date
    Page<Transaction> findAllByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    // Transaction Filter by Date and Transaction Code
    Page<Transaction> findAllByCreatedAtBetweenAndTransactionCodeContainsIgnoreCase(LocalDateTime startDate, LocalDateTime endDate, String transactionCode, Pageable pageable);
    // Transaction Detail
    Optional<Transaction> findByTransactionCode(String transactionCode);
    // Get By Reference Code
    Optional<Transaction> findByReferenceCode(String referenceCode);

    // Statistic Related
    @Query(value="SELECT SUM(t.totalPrice) FROM Transaction t WHERE t.createdAt BETWEEN ?1 AND ?2")
    BigDecimal sumTotalPriceByCreatedAt(LocalDateTime startDate, LocalDateTime endDate);
    @Query(value="SELECT AVG(t.totalPrice) FROM Transaction t WHERE t.createdAt BETWEEN ?1 AND ?2")
    BigDecimal averageTotalPriceByCreatedAt(LocalDateTime startDate, LocalDateTime endDate);
    @Query(value="SELECT FORMAT(t.createdAt, 'MMMM yyyy') AS month, COUNT(t) AS totalTransactionCount, SUM(t.totalPrice) AS totalTransactionValue, AVG(t.totalPrice) AS avgTransactionValue FROM Transaction t WHERE t.createdAt BETWEEN ?1 AND ?2 GROUP BY FORMAT(t.createdAt, 'MMMM yyyy')")
    List<Map<String, Object>> findAllGroupByCreatedAtMonthly(LocalDateTime startDate, LocalDateTime endDate);

    // For booking part

    @Query(value="SELECT FORMAT(t.createdAt, 'dddd, dd MMMM yyyy') AS month, COUNT(t) AS totalTransactionCount, SUM(t.totalPrice) AS totalTransactionValue, AVG(t.totalPrice) AS avgTransactionValue FROM Transaction t WHERE t.createdAt BETWEEN ?1 AND ?2 GROUP BY FORMAT(t.createdAt, 'dddd, dd MMMM yyyy')")
    List<Map<String, Object>> findAllGroupByCreatedAtDaily(LocalDateTime startDate, LocalDateTime endDate);
}
