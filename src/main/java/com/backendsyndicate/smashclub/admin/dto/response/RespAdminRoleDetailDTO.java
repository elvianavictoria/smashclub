package com.backendsyndicate.smashclub.admin.dto.response;

import com.backendsyndicate.smashclub.admin.dto.relation.RelAdminRoleMenuDTO;
import com.backendsyndicate.smashclub.admin.dto.relation.RelAdminRolePermissionDTO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Data
@Getter
@Setter
public class RespAdminRoleDetailDTO {
    private int id;
    private String roleCode;
    private String roleName;
    private int status;
    private Set<RelAdminRoleMenuDTO> menuSet;
    private Set<RelAdminRolePermissionDTO> permissionSet;
}
