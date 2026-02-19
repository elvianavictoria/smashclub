package com.backendsyndicate.smashclub.ecommerce.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqUpdateCartItemDTO {
    private Long cartItemId;
    private Integer quantity;
}
