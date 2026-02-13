package com.backendsyndicate.smashclub.booking.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookingStatusUpdateRequest {
    @NotNull(message = "Status baru harus diisi")
    private Byte status;

    private String cancellationReason; // Optional, untuk pembatalan
}