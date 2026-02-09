package com.backendsyndicate.smashclub.admin.dto.response;

import jakarta.persistence.Column;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RespAdminRoleListDTO {
    private int id;
    private String roleCode;
    private String roleName;
    private int status;
}
