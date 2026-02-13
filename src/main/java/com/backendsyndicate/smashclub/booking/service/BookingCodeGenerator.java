package com.backendsyndicate.smashclub.booking.service;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class BookingCodeGenerator {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyMMdd");
    private static final AtomicInteger counter = new AtomicInteger(1);

    public String generate() {
        String datePart = LocalDateTime.now().format(DATE_FORMATTER);
        String sequencePart = String.format("%04d", counter.getAndIncrement() % 10000);
        return "BK-" + datePart + "-" + sequencePart;
    }
}