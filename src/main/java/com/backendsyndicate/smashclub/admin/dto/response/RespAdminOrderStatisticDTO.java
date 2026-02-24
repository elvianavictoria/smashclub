package com.backendsyndicate.smashclub.admin.dto.response;

import com.backendsyndicate.smashclub.admin.dto.extra.ExtAdminOrderCategoryRankingDTO;
import com.backendsyndicate.smashclub.admin.dto.extra.ExtAdminOrderMonthlyDTO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Getter
@Setter
public class RespAdminOrderStatisticDTO {
    private int totalQtySold = 0;
    private BigDecimal averageOrderValue = BigDecimal.ZERO;
    private List<ExtAdminOrderCategoryRankingDTO> soldCategoryRanking = new ArrayList<>();
    private List<ExtAdminOrderMonthlyDTO> monthlyOrders = new ArrayList<>();
}
