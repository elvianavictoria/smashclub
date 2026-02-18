package com.backendsyndicate.smashclub.ecommerce.core;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface IProduct<Object> {
    public ResponseEntity<Object> findAll(String keyword, Pageable pageable, HttpServletRequest request);
//    public ResponseEntity<Object> findAllByProductNameContainsIgnoreCaseOrCategoryContainsIgnoreCaseAndIsActiveTrue(Pageable pageable, String name, String category, HttpServletRequest request );
    ResponseEntity<Object> findById(Long id, HttpServletRequest request);
}
