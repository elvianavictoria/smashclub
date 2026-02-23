package com.backendsyndicate.smashclub.booking.controller;

import com.backendsyndicate.smashclub.booking.dto.request.BookingRequest;
import com.backendsyndicate.smashclub.booking.dto.request.BookingStatusUpdateRequest;
import com.backendsyndicate.smashclub.booking.dto.request.CourtAvailabilityRequest;
import com.backendsyndicate.smashclub.booking.service.BookingService;
import com.backendsyndicate.smashclub.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;

@RestController
@RequestMapping("/api/v1/booking")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;
    private final AuthService authService;

    // ============ CHECK COURT AVAILABILITY ============
    @PostMapping("/availability/courts")
    public ResponseEntity<Object> checkCourtAvailability(
            @Valid @RequestBody CourtAvailabilityRequest request,
            HttpServletRequest httpRequest) {

        return bookingService.checkCourtAvailability(request, httpRequest);
    }

    // ============ GET AVAILABLE COACHES ============
    @GetMapping("/availability/coaches")
    public ResponseEntity<Object> getAvailableCoaches(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime,
            HttpServletRequest httpRequest) {

        return bookingService.getAvailableCoaches(date, startTime, endTime, httpRequest);
    }

    // ============ GET AVAILABLE EQUIPMENT ============
    @GetMapping("/availability/equipment")
    public ResponseEntity<Object> getAvailableEquipment(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime,
            @RequestParam(required = false) Integer requiredQuantity,
            HttpServletRequest httpRequest) {
        return bookingService.getAvailableEquipment(date, startTime, endTime, requiredQuantity, httpRequest);
    }

    // ============ GET BOOKING SUMMARY ============
    @PostMapping("/summary")
    public ResponseEntity<Object> getBookingSummary(
            @Valid @RequestBody BookingRequest request,
            HttpServletRequest httpRequest) {

        return bookingService.getBookingSummary(request, httpRequest);
    }

    // ============ CREATE BOOKING ============
    @PostMapping
    public ResponseEntity<Object> createBooking(
            @Valid @RequestBody BookingRequest request,
            @RequestHeader("Authorization") String authorizationHeader,
            HttpServletRequest httpRequest) {

        String userId = extractUserIdFromToken(authorizationHeader);
        if (userId == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return bookingService.createBooking(request, userId, httpRequest);
    }

    // ============ GET BOOKING DETAILS ============
    @GetMapping("/{bookingCode}")
    public ResponseEntity<Object> getBookingDetails(
            @PathVariable String bookingCode,
            HttpServletRequest httpRequest) {

        return bookingService.getBookingDetails(bookingCode, httpRequest);
    }

    // ============ GET MY BOOKINGS ============
    @GetMapping("/my-bookings")
    public ResponseEntity<Object> getMyBookings(
            @RequestHeader("Authorization") String authorizationHeader,
            HttpServletRequest httpRequest) {

        String userId = extractUserIdFromToken(authorizationHeader);
        if (userId == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return bookingService.getUserBookings(userId, httpRequest);
    }

    // ============ UPDATE BOOKING STATUS ============
    @PutMapping("/{bookingCode}/status")
    public ResponseEntity<Object> updateBookingStatus(
            @PathVariable String bookingCode,
            @Valid @RequestBody BookingStatusUpdateRequest request,
            HttpServletRequest httpRequest) {

        return bookingService.updateBookingStatus(bookingCode, request, httpRequest);
    }

    // ============ GET ALL COURTS (Public) ============
    @GetMapping("/courts")
    public ResponseEntity<Object> getAllCourts(HttpServletRequest httpRequest) {
        return bookingService.getAllCourts(httpRequest);
    }

    // ============ START BOOKING (Check in / Ambil Barang) ============
    @PutMapping("/{bookingCode}/start")
    public ResponseEntity<Object> startBooking(
            @PathVariable String bookingCode,
            HttpServletRequest httpRequest) {
        return bookingService.startBooking(bookingCode, httpRequest);
    }

    // ============ COMPLETE BOOKING (Selesai / Kembalikan Barang) ============
    @PutMapping("/{bookingCode}/complete")
    public ResponseEntity<Object> completeBooking(
            @PathVariable String bookingCode,
            HttpServletRequest httpRequest) {
        return bookingService.completeBooking(bookingCode, httpRequest);
    }

    // ============ CREATE PAYMENT FOR BOOKING ============
    @PostMapping("/{bookingCode}/payment")
    public ResponseEntity<Object> createBookingPayment(
            @PathVariable String bookingCode,
            @RequestParam int paymentMethodId,
            @RequestHeader("Authorization") String authorizationHeader,
            HttpServletRequest httpRequest) {

        String userId = extractUserIdFromToken(authorizationHeader);
        if (userId == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return bookingService.createBookingPayment(bookingCode, paymentMethodId, httpRequest);
    }

    // ============ HELPER METHOD ============
    private String extractUserIdFromToken(String authorizationHeader) {
        try {
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String token = authorizationHeader.substring(7);
                return authService.getUserIdFromToken(token);
            }
        } catch (Exception e) {
            log.error("Error extracting user ID from token: {}", e.getMessage());
        }
        return null;
    }
}