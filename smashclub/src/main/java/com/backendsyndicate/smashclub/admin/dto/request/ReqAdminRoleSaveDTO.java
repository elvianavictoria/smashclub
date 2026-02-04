package com.backendsyndicate.smashclub.admin.dto.request;

import com.backendsyndicate.smashclub.admin.model.AdminMenu;
import com.backendsyndicate.smashclub.admin.model.AdminPermission;
import com.backendsyndicate.smashclub.common.constant.CommonConstant;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Data
@Getter
@Setter
public class ReqAdminRoleSaveDTO {
    @Pattern(regexp="^[a-zA-Z0-9._-]{3,16}$", message="Role code must be alphanumeric")
    private String roleCode;
    @Pattern(regexp="^[a-zA-Z0-9._-]{3,16}$", message="Role code must be alphanumeric")
    private String roleName;
    private int status = CommonConstant.STATUS_ACTIVE;
    private Set<AdminMenu> menuSet;
    private Set<AdminPermission> permissionSet;
}
