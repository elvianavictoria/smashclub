package com.backendsyndicate.smashclub.ecommerce.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqBuyNowDTO {
    private Long variantId;
    @NotNull
    @Min(1)
    private int quantity;
}
