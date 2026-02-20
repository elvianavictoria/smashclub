package com.backendsyndicate.smashclub.admin.dto.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter
@Setter
public class RespAdminCoachListDTO {
    private Long id;
    private String coachCode;
    private String coachName;
    private BigDecimal pricePerHour;
    private byte status;
    private String coachImgLink;
    private String createdAt;
    private String updatedAt;
}
