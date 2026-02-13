package com.backendsyndicate.smashclub.admin.dto.response;

import com.backendsyndicate.smashclub.admin.dto.relation.RelAdminTransactionListDTO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;

@Data
@Getter
@Setter
public class RespAdminTransactionListDTO {
    private BigDecimal totalTransactionValue;
    private BigDecimal averageTransactionValue;
    private Page<RelAdminTransactionListDTO> transactions;
}
