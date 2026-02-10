package com.backendsyndicate.smashclub.admin.dto.response;

import com.backendsyndicate.smashclub.admin.dto.extra.ExtAdminTransactionItemDTO;
import com.backendsyndicate.smashclub.admin.dto.relation.RelAdminSalesListDTO;
import com.backendsyndicate.smashclub.admin.dto.relation.RelAdminSalesPlayerDTO;
import jakarta.persistence.Column;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter
@Setter
public class RespAdminSalesDetailDTO {
    private Long id;
    private String transactionCode;
    private String transactionLabel;
    private BigDecimal totalPrice;
    private String paymentLink;
    private byte status;
    private byte isRefunded;
    private String referenceCode;
    private String notes;
    private int transactionType;
    private int paymentMethodID;
    private RelAdminSalesPlayerDTO user;
    private String createdAt;
    private String updatedAt;
    private List<ExtAdminTransactionItemDTO> items;
}
