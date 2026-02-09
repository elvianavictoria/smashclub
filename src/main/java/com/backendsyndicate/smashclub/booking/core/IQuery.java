package com.backendsyndicate.smashclub.booking.core;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface IQuery<T> {
    public ResponseEntity<Object> findAll(Pageable pageable);
    public ResponseEntity<Object> findById(Long id);
}
