package com.backendsyndicate.smashclub.ecommerce.repo;

import com.backendsyndicate.smashclub.ecommerce.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepo extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartIdAndVariantId(Long cartId, Long variantId);

    Optional<CartItem> findByIdAndCartId(Long cartItemId, Long cartId);
}
