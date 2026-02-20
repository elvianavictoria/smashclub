package com.backendsyndicate.smashclub.admin.dto.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter
@Setter
public class RespAdminCourtListDTO {
    private Long id;
    private String courtCode;
    private String courtName;
    private String openTime;
    private String closeTime;
    private byte status;
    private String courtImgLink;
    private BigDecimal pricePerHour;
    private String createdAt;
    private String updatedAt;
}
