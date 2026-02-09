package com.backendsyndicate.smashclub.admin.dto.response;

import com.backendsyndicate.smashclub.admin.dto.relation.RelAdminProductProductVariantDTO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class RespAdminProductDetailDTO {
    private Long id;
    private String productName;
    private String category;
    private byte status;
    private String defaultImgLink;
    private List<RelAdminProductProductVariantDTO> productVariants;
}
