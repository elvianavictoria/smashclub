package com.backendsyndicate.smashclub.payment.dto.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Data
@Getter
@Setter
public class RespGetBalanceInfoDTO {
    private BigDecimal userBalance;
    private List<RespGetBalanceLogDTO> walletLog;
}
