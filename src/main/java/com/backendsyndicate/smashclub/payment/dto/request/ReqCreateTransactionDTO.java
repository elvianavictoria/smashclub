package com.backendsyndicate.smashclub.payment.dto.request;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter
@Setter
public class ReqCreateTransactionDTO {
    private String customerId;
    private BigDecimal totalPrice;
    private String referenceCode;
    private byte transactionType;
    private byte paymentMethodId;
}
