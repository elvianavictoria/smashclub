package com.backendsyndicate.smashclub.admin.dto.response;

import com.backendsyndicate.smashclub.admin.dto.relation.RelAdminUserRoleDTO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RespAdminUserListDTO {
    private Long id;
    private String username;
    private String fullName;
    private String profilePicture;
    private int status = 0;
    private String createdAt;
    private String updatedAt;
    private RelAdminUserRoleDTO adminRole;
}
