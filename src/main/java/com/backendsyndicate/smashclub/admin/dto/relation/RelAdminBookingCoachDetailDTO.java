package com.backendsyndicate.smashclub.admin.dto.relation;

import com.backendsyndicate.smashclub.booking.model.Coach;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter
@Setter
public class RelAdminBookingCoachDetailDTO {
    private RelAdminBookingCoachDTO coach;
    private BigDecimal coachPrice;
}
