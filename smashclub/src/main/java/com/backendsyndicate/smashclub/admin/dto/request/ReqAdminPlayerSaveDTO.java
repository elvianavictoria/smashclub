package com.backendsyndicate.smashclub.admin.dto.request;

import com.backendsyndicate.smashclub.admin.dto.validation.ValAdminUserRoleDTO;
import com.backendsyndicate.smashclub.common.constant.AuthenticationConstant;
import com.backendsyndicate.smashclub.common.constant.CommonConstant;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class ReqAdminPlayerSaveDTO {
    @Pattern(regexp="^[a-zA-Z0-9._-]{3,16}$", message="Email must be alphanumeric")
    @NotBlank(message="Email is required!")
    @Email
    private String email;
    @Pattern(regexp="^[a-zA-Z]{3,16}$", message="Fullname must contain letters only!")
    @NotBlank(message="Fullname is required!")
    private String fullName;
    @NotNull(message="Status is required!")
    private int status = AuthenticationConstant.ACTIVE;
    private int failedLoginAttempt = 0;
//    private String passwordHash;
}
