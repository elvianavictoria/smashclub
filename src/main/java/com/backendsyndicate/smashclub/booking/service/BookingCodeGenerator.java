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
        long todayBookingCount = bookingRepository.countByBookingDate(today);

        // Sequence number = jumlah hari ini + 1
        long sequence = todayBookingCount + 1;

        String sequencePart = String.format("%04d", sequence);

        return "BK-" + datePart + "-" + sequencePart;
    }
}