package com.backendsyndicate.smashclub.admin.dto.response;

import com.backendsyndicate.smashclub.admin.dto.extra.ExtAdminTransactionDTO;
import com.backendsyndicate.smashclub.admin.dto.extra.ExtAdminTransactionDailyDTO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Data
@Getter
@Setter
public class RespAdminDashboardDTO {
    private long courtCount = 0;
    private long coachCount = 0;
    private long transactionCount = 0;
    private List<ExtAdminTransactionDailyDTO> dailyTransactionCount = new ArrayList<>();
    private List<ExtAdminTransactionDTO> dailyTransaction = new ArrayList<>();
}
