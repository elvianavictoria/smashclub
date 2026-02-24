package com.backendsyndicate.smashclub.admin.dto.response;


import com.backendsyndicate.smashclub.admin.dto.extra.ExtAdminBookingMonthlyDTO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class RespAdminBookingStatisticDTO {
    private int totalBookingCount = 0;
    private double averageBookingHours = 0.0;
    private double occupancyRate = 0.0;
    private List<ExtAdminBookingMonthlyDTO> monthlyBookingStatistic;
}
