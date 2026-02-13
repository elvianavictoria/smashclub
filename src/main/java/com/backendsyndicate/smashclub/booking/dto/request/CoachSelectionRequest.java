package com.backendsyndicate.smashclub.booking.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CoachSelectionRequest {
    @NotNull(message = "Coach ID harus diisi")
    private Long coachId;

    @NotNull(message = "Durasi coach harus diisi")
    private Integer durationHours;
}