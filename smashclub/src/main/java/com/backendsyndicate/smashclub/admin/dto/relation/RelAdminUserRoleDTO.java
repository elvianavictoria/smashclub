package com.backendsyndicate.smashclub.admin.dto.relation;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RelAdminUserRoleDTO {
    private int id;
    private String roleCode;
    private String roleName;
}
