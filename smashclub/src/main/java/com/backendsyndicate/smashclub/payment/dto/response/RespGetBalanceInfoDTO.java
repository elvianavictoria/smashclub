package com.backendsyndicate.smashclub.payment.dto.response;

import java.math.BigDecimal;
import java.util.List;

public class RespGetBalanceInfoDTO {
    private BigDecimal userBalance;
    private List<RespGetBalanceLogDTO> walletLog;
}
