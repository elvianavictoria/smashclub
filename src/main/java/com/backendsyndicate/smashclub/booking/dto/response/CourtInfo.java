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
public class CourtInfo {
    private Long id;
    private String courtCode;
    private String courtName;
    private String courtImgLink;
    private LocalTime openTime;
    private LocalTime closeTime;
    private BigDecimal pricePerHour; // Price per hour
    private Byte status;
}