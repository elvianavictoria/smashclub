package com.backendsyndicate.smashclub.ecommerce.service;

import com.backendsyndicate.smashclub.ecommerce.core.IProduct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public class ProductService implements IProduct {
    @Override
    public ResponseEntity<Object> findAll(Pageable pageable, HttpServletRequest request) {
        return null;
    }

    @Override
    public ResponseEntity<Object> findByProductId(Long productId) {
        return null;
    }
}
