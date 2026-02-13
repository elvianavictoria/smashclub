package com.backendsyndicate.smashclub.booking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentDetailResponse {
    private Long id;
    private String equipmentName;
    private String brand;
    private String type;
    private String categoryName;
    private BigDecimal pricePerUnit;
    private Integer quantity;
    private LocalTime startTime;
    private LocalTime endTime;
    private BigDecimal equipmentPrice;
}