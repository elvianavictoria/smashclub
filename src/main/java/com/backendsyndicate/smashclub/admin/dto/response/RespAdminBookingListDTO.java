package com.backendsyndicate.smashclub.admin.dto.response;

import com.backendsyndicate.smashclub.admin.dto.extra.ExtAdminBookingListDTO;
import com.backendsyndicate.smashclub.admin.dto.extra.ExtAdminBookingMonthlyDTO;
import com.backendsyndicate.smashclub.admin.dto.relation.RelAdminTransactionListDTO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

@Data
@Getter
@Setter
public class RespAdminBookingListDTO {

    private int totalBookingCount = 0;
    private double averageBookingHours = 0.0;
    private BigDecimal occupancyRate = BigDecimal.ZERO;
    private Page<ExtAdminBookingListDTO> bookings;
}
