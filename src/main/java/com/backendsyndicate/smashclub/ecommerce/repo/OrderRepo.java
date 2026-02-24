package com.backendsyndicate.smashclub.ecommerce.repo;

import com.backendsyndicate.smashclub.ecommerce.model.Order;
import com.backendsyndicate.smashclub.payment.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface OrderRepo extends JpaRepository<Order, Long> {

    Page<Order> findByOrderDateBetween(LocalDate startedAt, LocalDate endedAt, Pageable pageable);
    Page<Order> findByStatus(byte status, Pageable pageable);
    Optional<Order> findByIdAndUserId(Long id, String userId);
    Page<Order> findByUserIdOrderByOrderDateDesc(String userId, Pageable pageable);

    // Statistic Related
    @Query(value="SELECT AVG(o.totalPrice) FROM Order o WHERE o.orderDate BETWEEN ?1 AND ?2")
    BigDecimal averageTotalPriceByOrderDate(LocalDateTime startDate, LocalDateTime endDate);
    @Query(value="SELECT FORMAT(o.orderDate, 'MMMM yyyy') AS month, " +
            "COUNT(oi.quantity) AS totalSoldQuantity, SUM(o.totalPrice) AS totalOrderValue " +
            "FROM `Order` o JOIN o.orderItem oi " +
            "WHERE o.orderDate BETWEEN ?1 AND ?2 GROUP BY FORMAT(o.orderDate, 'MMMM yyyy')")
    List<Map<String, Object>> findAllGroupByOrderDateMonthly(LocalDateTime startDate, LocalDateTime endDate);
    // Order Filter by Date
    Page<Order> findAllByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

}
