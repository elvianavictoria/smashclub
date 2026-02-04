package com.backendsyndicate.smashclub.admin.dto.response;

import com.backendsyndicate.smashclub.admin.dto.relation.RelPermissionMenuDTO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RespAdminPermissionListDTO {
    private int id;
    private String permissionName;
    private RelPermissionMenuDTO menu;
//    private int status;
}
