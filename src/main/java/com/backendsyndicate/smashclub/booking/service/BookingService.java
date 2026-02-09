package com.backendsyndicate.smashclub.booking.service;

import com.backendsyndicate.smashclub.booking.core.IBooking;
import com.backendsyndicate.smashclub.booking.model.Booking;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public class BookingService implements IBooking {
    @Override
    public ResponseEntity<Object> findAll(String userId, Pageable pageable) {
        return null;
    }

    @Override
    public ResponseEntity<Object> findByBookingCode(String userId, String bookingCode) {
        return null;
    }

    @Override
    public ResponseEntity<Object> createBooking(String userId, Booking booking) {
        return null;
    }

    @Override
    public ResponseEntity<Object> cancelBooking(String userId, String bookingCode) {
        return null;
    }
}
