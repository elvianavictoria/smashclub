package com.backendsyndicate.smashclub.admin.dto.extra;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ExtAdminTransactionDTO {
    private String fullName;
    private String transactionLabel;
    private byte status;
    private String statusDesc;
    private String startTime;
}
