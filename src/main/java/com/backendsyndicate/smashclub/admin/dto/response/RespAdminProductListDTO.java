package com.backendsyndicate.smashclub.admin.dto.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RespAdminProductListDTO {
    private Long id;
    private String productName;
    private String category;
    private byte status;
    private String defaultImgLink;
}
