package com.backendsyndicate.smashclub.ecommerce.core;

import org.springframework.http.ResponseEntity;

public interface ICart {
    public ResponseEntity<Object> updateCartItem(String userId, Long productId, Long productVariantId, int quantity);
    public ResponseEntity<Object> deleteCartItem(String userId, Long productId, Long productVariantId);
    public ResponseEntity<Object> clearCart(String userId);
}
