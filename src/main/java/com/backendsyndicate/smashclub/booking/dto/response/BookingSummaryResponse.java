package com.backendsyndicate.smashclub.booking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingSummaryResponse {
    private CourtInfo court;
    private List<CoachDetailResponse> coaches;
    private List<EquipmentDetailResponse> equipment;

    private BigDecimal courtTotalPrice;
    private BigDecimal coachesTotalPrice;
    private BigDecimal equipmentTotalPrice;
    private BigDecimal grandTotal;

    private String bookingCode;
    private String estimatedDuration;

    private String paymentUrl;

    private String statusDescription;
}