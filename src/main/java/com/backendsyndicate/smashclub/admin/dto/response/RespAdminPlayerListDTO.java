package com.backendsyndicate.smashclub.admin.dto.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RespAdminPlayerListDTO {
    private String id;
    private String fullName;
    private String email;
    private byte status;
    private String createdAt;
    private String lockedUntil;
}
