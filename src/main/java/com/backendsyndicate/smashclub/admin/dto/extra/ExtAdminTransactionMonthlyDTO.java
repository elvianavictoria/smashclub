package com.backendsyndicate.smashclub.admin.dto.extra;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter
@Setter
public class ExtAdminTransactionMonthlyDTO {
    private String month;
    private int totalTransactionCount;
    private BigDecimal totalTransactionValue;
    private BigDecimal avgTransactionValue;
}
