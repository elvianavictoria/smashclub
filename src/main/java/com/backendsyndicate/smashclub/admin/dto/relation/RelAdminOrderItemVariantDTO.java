package com.backendsyndicate.smashclub.admin.dto.relation;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter
@Setter
public class RelAdminOrderItemVariantDTO {
    private String sku;
    private String name;
    private String variantImgLink;
}
