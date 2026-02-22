package com.backendsyndicate.smashclub.ecommerce.dto.response;

import lombok.*;

//@Builder
@Data
@Getter
@Setter
@NoArgsConstructor
public class RespCartItemDTO {
    private Long id;
    private RespCartVariantDTO variant;
    private int quantity;
}
