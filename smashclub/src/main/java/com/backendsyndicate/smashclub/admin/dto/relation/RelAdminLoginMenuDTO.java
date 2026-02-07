package com.backendsyndicate.smashclub.admin.dto.relation;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Data
@Getter
@Setter
public class RelAdminLoginMenuDTO {
    private int id;
    private String menuCode;
    private String menuName;
    private int parentId;
    private RelAdminMenuMenuCategoryDTO category;
    private Set<RelAdminRolePermissionDTO> permissions;
}
