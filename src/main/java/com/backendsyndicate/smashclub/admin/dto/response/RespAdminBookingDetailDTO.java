package com.backendsyndicate.smashclub.admin.dto.response;

import com.backendsyndicate.smashclub.admin.dto.extra.ExtAdminBookingListDTO;
import com.backendsyndicate.smashclub.admin.dto.relation.RelAdminBookingCoachDetailDTO;
import com.backendsyndicate.smashclub.admin.dto.relation.RelAdminBookingCourtDTO;
import com.backendsyndicate.smashclub.admin.dto.relation.RelAdminBookingEquipmentDetailDTO;
import com.backendsyndicate.smashclub.admin.dto.relation.RelAdminBookingPlayerDTO;
import com.backendsyndicate.smashclub.common.constant.BookingConstant;
import com.backendsyndicate.smashclub.common.util.DatetimeFormatting;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Getter
@Setter
public class RespAdminBookingDetailDTO {
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
    private RelAdminBookingPlayerDTO user;
    private RelAdminBookingCourtDTO court;
    private List<RelAdminBookingCoachDetailDTO> coaches = new ArrayList<>();
    private List<RelAdminBookingEquipmentDetailDTO> equipments = new ArrayList<>();

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
