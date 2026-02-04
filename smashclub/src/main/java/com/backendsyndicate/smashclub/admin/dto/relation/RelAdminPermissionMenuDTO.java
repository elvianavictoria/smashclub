package com.backendsyndicate.smashclub.admin.dto.relation;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RelAdminPermissionMenuDTO {
    private int id;
    private String menuCode;
    private String menuName;
    private int parentId;
}
