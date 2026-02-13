package com.backendsyndicate.smashclub.payment.dto.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Data
@Getter
@Setter
public class RespRefundTransactionDTO {
    private String transactionCode;
    private boolean requested = false;
}
