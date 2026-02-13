package com.backendsyndicate.smashclub.booking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourtAvailabilityResponse {
    private Long id;
    private String courtCode;
    private String courtName;
    private String courtImgLink;
    private LocalTime openTime;
    private LocalTime closeTime;
    private BigDecimal pricePerHour;
    private Byte status;
    private List<TimeSlot> availableSlots;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimeSlot {
        private LocalTime startTime;
        private LocalTime endTime;
        private boolean available;
        private BigDecimal price;
        private String status; // "AVAILABLE", "BOOKED", "PENDING"
    }
}