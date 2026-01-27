package com.backendsyndicate.smashclub.ecommerce.core;

import org.springframework.http.ResponseEntity;

public interface IOrder {
    public ResponseEntity<Object> createOrder(String userId, Object orderItem);
}
