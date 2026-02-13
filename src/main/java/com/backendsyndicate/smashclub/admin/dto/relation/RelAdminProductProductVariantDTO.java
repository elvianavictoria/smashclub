package com.backendsyndicate.smashclub.admin.dto.relation;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter
@Setter
public class RelAdminProductProductVariantDTO {
    private int id;
    private String name;
    private BigDecimal price;
    private String variantImgLink;
    private String sku;
    private int stock;
}
