package com.backendsyndicate.smashclub.admin.dto.response;

import com.backendsyndicate.smashclub.admin.dto.extra.ExtAdminTransactionItemDTO;
import com.backendsyndicate.smashclub.admin.dto.relation.RelAdminTransactionPlayerDTO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Getter
@Setter
public class RespAdminTransactionDetailDTO {
    private Long id;
    private String transactionCode;
    private String transactionLabel;
    private BigDecimal totalPrice;
    private String paymentLink;
    private byte status;
    private String statusDesc;
    private byte isRefunded;
    private String referenceCode;
    private String notes;
    private int transactionType;
    private String transactionTypeDesc;
    private int paymentMethodID;
    private RelAdminTransactionPlayerDTO user;
    private String createdAt;
    private String updatedAt;
    private List<ExtAdminTransactionItemDTO> items = new ArrayList<>();
}
