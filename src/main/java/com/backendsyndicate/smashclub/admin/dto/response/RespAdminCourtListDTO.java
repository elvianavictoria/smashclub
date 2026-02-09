package com.backendsyndicate.smashclub.admin.dto.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

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
    private String createdAt;
    private String updatedAt;
}
