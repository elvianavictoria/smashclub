package com.backendsyndicate.smashclub.payment.dto.response;

import com.backendsyndicate.smashclub.common.util.DatetimeFormatting;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class RespGetBalanceLogDTO {
    private Long id;
    private BigDecimal previousBalance;
    private BigDecimal currentBalance;
    private BigDecimal usageValue;
    private boolean usageType;
    private String refID;
    private String createdAt;

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = DatetimeFormatting.getDatetimeFormat(createdAt);
    }
}
