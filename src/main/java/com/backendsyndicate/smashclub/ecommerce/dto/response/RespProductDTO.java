package com.backendsyndicate.smashclub.ecommerce.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RespProductDTO {
    private Long id;
    private String productName;
    private String productDesc;
    private String defaultImgLink;
    private String category;
    private List<RespProductVariantDTO> productVariants;
    private byte status;
}
