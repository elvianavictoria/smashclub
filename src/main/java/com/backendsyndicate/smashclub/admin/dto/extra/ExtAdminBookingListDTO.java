package com.backendsyndicate.smashclub.admin.dto.extra;

import com.backendsyndicate.smashclub.common.constant.BookingConstant;
import com.backendsyndicate.smashclub.common.util.DatetimeFormatting;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Getter
@Setter
public class ExtAdminBookingListDTO {
    private String bookingCode;
    private String bookingDate;
    private String startTime;
    private String endTime;
    private int durationHour;
    private BigDecimal basePrice;
    private BigDecimal totalPrice;
    private byte status = BookingConstant.BOOKING_PENDING;
    private String statusDesc = "";
    private String createdAt;
    private String updatedAt;

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = DatetimeFormatting.getDateFormat(bookingDate);
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = DatetimeFormatting.getClockFormat(startTime);
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = DatetimeFormatting.getClockFormat(endTime);
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = DatetimeFormatting.getDatetimeFormat(createdAt);
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = DatetimeFormatting.getDatetimeFormat(updatedAt);
    }
}
