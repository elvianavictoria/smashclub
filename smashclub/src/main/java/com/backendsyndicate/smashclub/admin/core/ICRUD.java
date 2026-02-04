package com.backendsyndicate.smashclub.admin.core;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface ICRUD<T, TID> {
    public ResponseEntity<Object> findAll(String keyword, Pageable pageable, HttpServletRequest request);
    public ResponseEntity<Object> findById(TID id, HttpServletRequest request);
    public ResponseEntity<Object> save(T t, HttpServletRequest request);
    public ResponseEntity<Object> update(TID id, T t, HttpServletRequest request);
    public ResponseEntity<Object> delete(TID id, HttpServletRequest request);
}
