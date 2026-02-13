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
public class CoachAvailabilityResponse {
    private Long id;
    private String coachCode;
    private String coachName;
    private String coachImgLink;
    private BigDecimal pricePerHour;
    private Byte status;
    private boolean available;
    private List<String> occupiedTimes; // Format: "HH:mm-HH:mm"
}