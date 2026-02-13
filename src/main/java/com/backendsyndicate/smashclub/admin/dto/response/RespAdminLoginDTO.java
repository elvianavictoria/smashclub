package com.backendsyndicate.smashclub.admin.dto.response;

import com.backendsyndicate.smashclub.admin.dto.relation.RelAdminLoginRoleDTO;
import com.backendsyndicate.smashclub.admin.dto.relation.RelAdminUserRoleDTO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RespAdminLoginDTO {
    private String accessToken;
    private String profilePicture;
    private String username;
    private String fullname;
    private RelAdminLoginRoleDTO adminRole;
}
