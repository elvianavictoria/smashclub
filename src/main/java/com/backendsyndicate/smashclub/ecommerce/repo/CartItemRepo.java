package com.backendsyndicate.smashclub.ecommerce.repo;

import com.backendsyndicate.smashclub.ecommerce.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepo extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCart_IdAndVariant_Id(Long cartId, Long variantId);

    Optional<CartItem> findByIdAndCart_Id(Long cartItemId, Long cartId);

    void deleteByCart_Id(Long cartId);
}
