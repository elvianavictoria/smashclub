package com.backendsyndicate.smashclub.ecommerce.repo;

import com.backendsyndicate.smashclub.ecommerce.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface OrderRepo extends JpaRepository<Order, Long> {

    Page<Order> findByOrderDateBetween(LocalDate startedAt, LocalDate endedAt, Pageable pageable);
    Page<Order> findByStatus(byte status, Pageable pageable);
    Optional<Order> findByIdAndUserId(Long id, String userId);
    Page<Order> findByUserIdOrderByOrderDateDesc(String userId, Pageable pageable);
}
