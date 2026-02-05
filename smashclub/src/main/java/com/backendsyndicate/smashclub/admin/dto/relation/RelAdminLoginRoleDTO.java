package com.backendsyndicate.smashclub.admin.dto.relation;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Data
@Getter
@Setter
public class RelAdminLoginRoleDTO {
    private int id;
    private String roleCode;
    private String roleName;
    private Set<RelAdminLoginMenuDTO> menuSet;
    private Set<RelAdminLoginPermissionDTO> permissionSet;
}
