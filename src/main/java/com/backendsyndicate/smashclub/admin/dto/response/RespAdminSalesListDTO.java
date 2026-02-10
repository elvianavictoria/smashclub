package com.backendsyndicate.smashclub.admin.dto.response;

import com.backendsyndicate.smashclub.admin.dto.extra.ExtAdminTransactionMonthlyDTO;
import com.backendsyndicate.smashclub.admin.dto.relation.RelAdminSalesListDTO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Data
@Getter
@Setter
public class RespAdminSalesListDTO {
    private BigDecimal totalTransactionValue;
    private BigDecimal averageTransactionValue;
    private List<RelAdminSalesListDTO> transactions;
}
