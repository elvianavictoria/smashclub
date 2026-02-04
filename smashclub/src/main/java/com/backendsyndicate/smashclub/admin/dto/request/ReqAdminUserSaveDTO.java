package com.backendsyndicate.smashclub.admin.dto.request;

import com.backendsyndicate.smashclub.admin.dto.validation.ValAdminUserRoleDTO;
import com.backendsyndicate.smashclub.common.constant.CommonConstant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ReqAdminUserSaveDTO {
    @Pattern(regexp="^[a-zA-Z0-9._-]{3,16}$", message="Role code must be alphanumeric")
    @NotBlank(message="Username is required!")
    private String username;
    @NotBlank(message="Fullname is required!")
    private String fullName;
    @NotNull(message="Status is required!")
    private int status = CommonConstant.STATUS_ACTIVE;
    private String password;
    @NotNull(message="Role is required!")
    private ValAdminUserRoleDTO adminRole;
}
