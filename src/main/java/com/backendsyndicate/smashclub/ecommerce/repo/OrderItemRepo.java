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
    @Query(value="SELECT SUM(oi.quantity) FROM OrderItem oi JOIN Order o WHERE o.orderDate BETWEEN ?1 AND ?2")
    int sumQuantityByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    @Query(value="SELECT oi.variant.product.category, COUNT(oi.variant.product.category) AS soldQuantity FROM OrderItem oi WHERE oi.order.orderDate BETWEEN ?1 AND ?2 GROUP BY oi.variant.product.category")
    List<Map<String, Object>> findAllGroupByProduct_Category(LocalDateTime startDate, LocalDateTime endDate);
}
