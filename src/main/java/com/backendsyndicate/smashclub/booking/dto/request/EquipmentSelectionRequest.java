package com.backendsyndicate.smashclub.booking.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EquipmentSelectionRequest {
    @NotNull(message = "Equipment ID harus diisi")
    private Long equipmentId;

    @NotNull(message = "Quantity harus diisi")
    @Min(value = 1, message = "Quantity minimal 1")
    private Integer quantity;
}