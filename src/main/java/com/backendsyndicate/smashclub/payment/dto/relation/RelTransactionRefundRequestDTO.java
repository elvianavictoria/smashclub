package com.backendsyndicate.smashclub.payment.dto.relation;

import jakarta.persistence.Column;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RelTransactionRefundRequestDTO {
    private Long refundId = 0L;
    private byte refundStatus = 0;
    private String refundReason = "";
    private String refundNotes = "";
}
