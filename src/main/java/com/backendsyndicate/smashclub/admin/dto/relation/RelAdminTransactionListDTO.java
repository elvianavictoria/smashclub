package com.backendsyndicate.smashclub.admin.dto.relation;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter
@Setter
public class RelAdminTransactionListDTO {
    private String transactionCode;
    private String transactionLabel;
    private RelAdminTransactionPlayerDTO user;
    private BigDecimal totalPrice;
    private int status;
    private String statusDesc;
    private String createdAt;
    private String updatedAt;
}
