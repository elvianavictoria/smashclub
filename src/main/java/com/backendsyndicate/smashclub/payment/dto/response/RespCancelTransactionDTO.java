package com.backendsyndicate.smashclub.payment.dto.response;

import com.backendsyndicate.smashclub.payment.dto.relation.RelTransactionUserDTO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter
@Setter
public class RespCancelTransactionDTO {
    private String transactionCode;
    private boolean requested = false;
    private int transactionType;
    private BigDecimal totalPrice;
    private RelTransactionUserDTO user;
}
