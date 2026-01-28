package com.backendsyndicate.smashclub.payment.dto.response;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class RespGetBalanceLogDTO {
    private Long id;
    private BigDecimal previousBalance;
    private BigDecimal currentBalance;
    private BigDecimal usageValue;
    private boolean usageType;
    private String refID;
    private Timestamp createdAt;
}
