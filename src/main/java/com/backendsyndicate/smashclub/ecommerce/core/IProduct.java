package com.backendsyndicate.smashclub.ecommerce.core;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface IProduct {
    public ResponseEntity<Object> findAll(Pageable pageable, HttpServletRequest request);
    public ResponseEntity<Object> findByProductId(Long productId);
}
