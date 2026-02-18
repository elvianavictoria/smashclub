package com.backendsyndicate.smashclub.ecommerce.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqOrderItemDTO {
    private Long variantId;
    private int quantity;
}
