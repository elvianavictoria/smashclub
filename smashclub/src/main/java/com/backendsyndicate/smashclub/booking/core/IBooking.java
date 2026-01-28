package com.backendsyndicate.smashclub.booking.core;

import com.backendsyndicate.smashclub.booking.model.Booking;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface IBooking {
    public ResponseEntity<Object> findAll(String userId, Pageable pageable);
    public ResponseEntity<Object> findByBookingCode(String userId, String bookingCode);
    public ResponseEntity<Object> createBooking(String userId, Booking booking);
    public ResponseEntity<Object> cancelBooking(String userId, String bookingCode);
}
