package com.backendsyndicate.smashclub.admin.dto.request;

import com.backendsyndicate.smashclub.admin.dto.validation.ValAdminRoleMenuDTO;
import com.backendsyndicate.smashclub.admin.dto.validation.ValAdminRolePermissionDTO;
import com.backendsyndicate.smashclub.common.constant.CommonConstant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Data
@Getter
@Setter
public class ReqAdminRoleSaveDTO {
    @NotNull(message="Role code is required!")
    @NotBlank(message="Role code is required!")
    @Pattern(regexp="^[a-zA-Z0-9._-]{3,16}$", message="Role code must be alphanumeric")
    private String roleCode;
    @NotNull(message="Role name is required!")
    @NotBlank(message="Role name is required!")
    @Pattern(regexp="^[a-zA-Z0-9._-]{3,16}$", message="Role code must be alphanumeric")
    private String roleName;
    @NotNull(message="Status is required!")
    private int status = CommonConstant.STATUS_ACTIVE;
    private Set<ValAdminRoleMenuDTO> menuSet;
    private Set<ValAdminRolePermissionDTO> permissionSet;
}
