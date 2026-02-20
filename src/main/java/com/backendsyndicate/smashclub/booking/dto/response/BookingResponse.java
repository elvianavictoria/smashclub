package com.backendsyndicate.smashclub.booking.dto.response;

import com.backendsyndicate.smashclub.booking.model.Booking;
import com.backendsyndicate.smashclub.payment.dto.response.RespCreateTransactionDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private Long id;
    private String bookingCode;
    private LocalDate bookingDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer durationHour;
    private BigDecimal basePrice;
    private BigDecimal totalPrice;
    private Byte status;
    private LocalDateTime createdAt;
    private RespCreateTransactionDTO respCreateTransactionDTO;

    // Court info
    private CourtInfo court;

    // Coach details
    private List<CoachDetailResponse> coaches;

    // Equipment details
    private List<EquipmentDetailResponse> equipment;

    // User info
    private UserInfo user;

    public static BookingResponse fromEntity(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .bookingCode(booking.getBookingCode())
                .bookingDate(booking.getBookingDate())
                .startTime(booking.getStartTime())
                .endTime(booking.getEndTime())
                .durationHour(booking.getDurationHour())
                .basePrice(booking.getBasePrice())
                .respCreateTransactionDTO(respCreateTransactionDTO.getPaymentData())
                .totalPrice(booking.getTotalPrice())
                .status(booking.getStatus())
                .createdAt(booking.getCreatedAt())
                .build();
    }
}