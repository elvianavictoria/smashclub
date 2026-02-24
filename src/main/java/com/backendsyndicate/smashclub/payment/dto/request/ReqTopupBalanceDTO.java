package com.backendsyndicate.smashclub.payment.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter
@Setter
public class ReqTopupBalanceDTO {
    private BigDecimal balance = BigDecimal.ZERO;
}
