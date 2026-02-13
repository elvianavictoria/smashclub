package com.backendsyndicate.smashclub.admin.dto.extra;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter
@Setter
public class ExtAdminTransactionItemDTO {
    private String itemName;
    private int itemQty;
    private BigDecimal itemPrice;
    private String itemUnit;
}
