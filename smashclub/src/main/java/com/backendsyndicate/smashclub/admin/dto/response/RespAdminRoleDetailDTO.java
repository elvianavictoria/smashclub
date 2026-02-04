package com.backendsyndicate.smashclub.admin.dto.response;

import com.backendsyndicate.smashclub.admin.dto.relation.RelRoleMenuDTO;
import com.backendsyndicate.smashclub.admin.dto.relation.RelRolePermissionDTO;
import com.backendsyndicate.smashclub.admin.model.AdminMenu;
import com.backendsyndicate.smashclub.admin.model.AdminPermission;
import jakarta.persistence.*;
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
    private Set<RelRoleMenuDTO> menuSet;
    private Set<RelRolePermissionDTO> permissionSet;
}
