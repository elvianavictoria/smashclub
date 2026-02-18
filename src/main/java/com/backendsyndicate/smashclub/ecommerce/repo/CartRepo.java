package com.backendsyndicate.smashclub.ecommerce.repo;

import com.backendsyndicate.smashclub.ecommerce.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepo extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserIdAndStatus(String userId, int  cartStatus);
}
