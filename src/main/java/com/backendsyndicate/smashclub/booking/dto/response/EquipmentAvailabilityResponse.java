package com.backendsyndicate.smashclub.booking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentAvailabilityResponse {
    private Long id;
    private String equipmentName;
    private String brand;
    private String type;
    private String categoryName;
    private BigDecimal price;
    private Integer stock;
    private Integer availableStock;
    private String equipmentImgLink;
    private String description;
    private Byte status;
}