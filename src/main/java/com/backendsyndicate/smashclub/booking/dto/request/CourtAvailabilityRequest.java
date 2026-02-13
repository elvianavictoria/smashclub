package com.backendsyndicate.smashclub.booking.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CourtAvailabilityRequest {
    @NotNull(message = "Tanggal harus diisi")
    private LocalDate date;

    private Long courtId; // Optional, jika kosong tampilkan semua court
}