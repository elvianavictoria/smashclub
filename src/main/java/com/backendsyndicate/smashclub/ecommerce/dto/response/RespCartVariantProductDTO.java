package com.backendsyndicate.smashclub.ecommerce.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RespCartVariantProductDTO {
    private Long id;
    private String productName;
    private String productDesc;
    private String defaultImgLink;
    private String category;
    private byte status;
}
