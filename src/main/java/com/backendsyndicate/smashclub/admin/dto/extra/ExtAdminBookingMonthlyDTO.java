package com.backendsyndicate.smashclub.admin.dto.extra;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter
@Setter
public class ExtAdminBookingMonthlyDTO {
    private String month = "";
    private int totalCount = 0;
    private double averageHour = 0.0;
    private double occupancyRate = 0.0;
    private BigDecimal totalPrice = BigDecimal.ZERO;
}
