package com.backendsyndicate.smashclub.admin.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ReqAdminRefundRequestProcessDTO {
    @NotNull(message="Refund status is required!")
    private int refundStatus;
    private String refundNotes;
}
