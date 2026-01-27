package com.backendsyndicate.smashclub.ecommerce.service;

import com.backendsyndicate.smashclub.ecommerce.core.IOrder;
import org.springframework.http.ResponseEntity;

public class OrderService implements IOrder {
    @Override
    public ResponseEntity<Object> createOrder(String userId, Object orderItem) {
        return null;
    }
}
