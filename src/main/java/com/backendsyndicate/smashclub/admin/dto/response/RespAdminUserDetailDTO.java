package com.backendsyndicate.smashclub.admin.dto.response;

import com.backendsyndicate.smashclub.admin.dto.relation.RelAdminUserRoleDTO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RespAdminUserDetailDTO {
    private int id;
    private String profilePicture;
    private String username;
    private String fullName;
    private int status;
    private RelAdminUserRoleDTO adminRole;
}
