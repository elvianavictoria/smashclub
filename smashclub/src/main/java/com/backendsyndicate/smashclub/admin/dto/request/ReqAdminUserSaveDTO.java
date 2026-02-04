package com.backendsyndicate.smashclub.admin.dto.request;

import com.backendsyndicate.smashclub.admin.model.AdminMenu;
import com.backendsyndicate.smashclub.admin.model.AdminPermission;
import com.backendsyndicate.smashclub.admin.model.AdminRole;
import com.backendsyndicate.smashclub.common.constant.CommonConstant;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Getter
@Setter
public class ReqAdminUserSaveDTO {
    @Pattern(regexp="^[a-zA-Z0-9._-]{3,16}$", message="Role code must be alphanumeric")
    private String username;
    private String fullName;
    private int status = CommonConstant.STATUS_ACTIVE;
    private AdminRole adminRole;
}
