package com.backendsyndicate.smashclub.payment.dto.response;

import com.backendsyndicate.smashclub.common.util.DatetimeFormatting;
import com.backendsyndicate.smashclub.payment.dto.extra.ExtTransactionItemDTO;
import com.backendsyndicate.smashclub.payment.dto.relation.RelTransactionUserDTO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter
@Setter
public class RespTransactionDetailDTO {
    private String transactionCode;
    private String transactionLabel;
    private String notes;
    private BigDecimal totalPrice;
    private byte transactionType;
    private String transactionTypeDesc;
    private byte status;
    private String statusDesc;
    private RelTransactionUserDTO user;
    private byte isRefunded;
    private String createdAt;
    private String updatedAt;

    private String paymentLink;
    private String referenceCode;

    private List<ExtTransactionItemDTO> items;

    public void setCreatedAt(LocalDateTime createdAt) {
        if( createdAt != null ) {
            this.createdAt = DatetimeFormatting.getDatetimeFormat(createdAt);
        }
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        if( updatedAt != null ) {
            this.updatedAt = DatetimeFormatting.getDatetimeFormat(updatedAt);
        }
    }
}
