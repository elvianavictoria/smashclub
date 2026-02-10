package com.backendsyndicate.smashclub.admin.dto.relation;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class RelAdminSalesListDTO {
    private String transactionCode;
    private String transactionLabel;
    private RelAdminSalesPlayerDTO user;
    private BigDecimal totalPrice;
    private String createdAt;
    private String updatedAt;
}
