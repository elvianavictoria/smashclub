package com.backendsyndicate.smashclub.ecommerce.dto.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter
@Setter
public class RespCartVariantDTO {
    private Long id;
    private String variantName;
    private String sku;
    private BigDecimal price;
    private Integer stock;
    private String variantImgLink;
    private RespCartVariantProductDTO  product;
}
