package com.backendsyndicate.smashclub.admin.core;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface ICRUD<T> {
    public ResponseEntity<Object> findAll(Pageable pageable, HttpServletRequest request);
    public ResponseEntity<Object> findById(Long id, HttpServletRequest request);
    public ResponseEntity<Object> save(T t, HttpServletRequest request);
    public ResponseEntity<Object> update(Long id, T t, HttpServletRequest request);
    public ResponseEntity<Object> delete(Long id, HttpServletRequest request);
}
