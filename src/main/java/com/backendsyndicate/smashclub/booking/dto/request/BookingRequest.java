package com.backendsyndicate.smashclub.booking.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class BookingRequest {
    @NotNull(message = "Court ID harus diisi")
    private Long courtId;

    @NotNull(message = "Tanggal booking harus diisi")
    @FutureOrPresent(message = "Tanggal booking tidak boleh masa lalu")
    private LocalDate bookingDate;

    @NotNull(message = "Jam mulai harus diisi")
    private LocalTime startTime;

    @NotNull(message = "Jam selesai harus diisi")
    private LocalTime endTime;

    private List<@Valid CoachSelectionRequest> coaches;
    private List<@Valid EquipmentSelectionRequest> equipment;
}