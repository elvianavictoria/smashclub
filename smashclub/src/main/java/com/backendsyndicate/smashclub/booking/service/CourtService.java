package com.backendsyndicate.smashclub.booking.service;

import com.backendsyndicate.smashclub.booking.core.IQuery;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public class CourtService implements IQuery {
    @Override
    public ResponseEntity<Object> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public ResponseEntity<Object> findById(Long id) {
        return null;
    }
}
