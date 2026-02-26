package com.backendsyndicate.smashclub.ecommerce.repo;

import com.backendsyndicate.smashclub.ecommerce.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface OrderItemRepo extends JpaRepository<OrderItem,Long> {
    List<OrderItem> findByOrderId(Long orderId);

    // Statistic Related
    @Query(value="SELECT COALESCE(SUM(oi.quantity), 0) FROM OrderItem oi JOIN oi.order o WHERE o.orderDate BETWEEN ?1 AND ?2")
    int sumQuantityByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    @Query(value="SELECT COALESCE(oi.variant.product.category, 'Others') AS category, " +
            "COALESCE(SUM(oi.quantity), 0) AS soldQuantity " +
            "FROM OrderItem oi " +
            "JOIN oi.order o " +
            "JOIN oi.variant pv " +
            "JOIN oi.variant.product p " +
            "WHERE o.orderDate BETWEEN :startDate AND :endDate GROUP BY p.category " +
            "ORDER BY soldQuantity DESC")
    List<Map<String, Object>> findAllGroupByProduct_Category(LocalDateTime startDate, LocalDateTime endDate);
}
