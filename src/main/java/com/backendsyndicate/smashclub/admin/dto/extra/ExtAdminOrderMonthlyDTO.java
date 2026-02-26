package com.backendsyndicate.smashclub.admin.dto.extra;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter
@Setter
public class ExtAdminOrderMonthlyDTO {
    private String month = "";
    private int totalSoldQuantity = 0;
    private BigDecimal totalOrderValue = BigDecimal.ZERO;
}