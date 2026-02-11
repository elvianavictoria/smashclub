package com.backendsyndicate.smashclub.admin.dto.response;

import com.backendsyndicate.smashclub.admin.dto.relation.RelAdminRefundTransactionDTO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RespAdminRefundRequestListDTO {
    private long id;
    private RelAdminRefundTransactionDTO transaction;
    private String refundReason;
    private byte refundStatus;
    private String refundStatusDesc;
    private String createdAt;
}
