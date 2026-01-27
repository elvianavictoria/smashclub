package com.backendsyndicate.smashclub.ecommerce.service;

import com.backendsyndicate.smashclub.ecommerce.core.ICart;
import org.springframework.http.ResponseEntity;

public class CartService implements ICart {
    @Override
    public ResponseEntity<Object> updateCartItem(String userId, Long productId, Long productVariantId, int quantity) {
        return null;
    }

    @Override
    public ResponseEntity<Object> deleteCartItem(String userId, Long productId, Long productVariantId) {
        return null;
    }

    @Override
    public ResponseEntity<Object> clearCart(String userId) {
        return null;
    }
}
