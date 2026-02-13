package com.backendsyndicate.smashclub.payment.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter
@Setter
public class ReqUpdateBalanceDTO {
    private boolean isAddition;
    private BigDecimal value;
}
