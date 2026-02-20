package com.backendsyndicate.smashclub.ecommerce.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Builder
@Data
@Getter
@Setter
public class RespCartItemDTO {
    private Long id;
    private RespCartDTO cart;
    private Long variantId;
    private int quantity;
}
