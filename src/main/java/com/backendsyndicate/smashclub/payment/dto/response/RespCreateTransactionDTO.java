package com.backendsyndicate.smashclub.payment.dto.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Data
@Getter
@Setter
public class RespCreateTransactionDTO {
    private String transactionCode;
    private Map<String, Object> paymentData;
}
