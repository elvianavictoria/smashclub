package com.backendsyndicate.smashclub.payment.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Data
@Getter
@Setter
public class ReqGetBalanceLogDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    private int page;
    private int size;
}
