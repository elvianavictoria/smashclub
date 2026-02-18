package com.backendsyndicate.smashclub.ecommerce.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class RespOrderItemDTO {
    private Long variantId;
    private String variantName;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal totalPrice;
}
