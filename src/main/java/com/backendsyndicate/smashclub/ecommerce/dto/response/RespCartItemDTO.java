package com.backendsyndicate.smashclub.ecommerce.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Builder
@Data
@Getter
@Setter
public class RespCartItemDTO {
    private RespCartDTO cart;
    private Long variantId;
    private int quantity;
    private BigDecimal priceSnapshot;
}
