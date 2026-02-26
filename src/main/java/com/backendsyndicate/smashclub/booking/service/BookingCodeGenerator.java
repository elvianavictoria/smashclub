package com.backendsyndicate.smashclub.booking.service;

import com.backendsyndicate.smashclub.booking.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class BookingCodeGenerator {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyMMdd");

    @Autowired
    private BookingRepository bookingRepository;

    public String generate() {
        LocalDate today = LocalDate.now();
        String datePart = today.format(DATE_FORMATTER);

        // Hitung jumlah booking yang sudah ada di hari ini
        Long countToday = bookingRepository.countTodayBooking(today);

        // Kalau null (misal belum ada booking), set ke 0
        long sequence = (countToday != null ? countToday : 0) + 1;

        String sequencePart = String.format("%04d", sequence);

        return "BK-" + datePart + "-" + sequencePart;
    }
}