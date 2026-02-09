package com.backendsyndicate.smashclub.admin.dto.request;

import com.backendsyndicate.smashclub.admin.dto.validation.ValAdminUserRoleDTO;
import com.backendsyndicate.smashclub.common.constant.CommonConstant;
import com.backendsyndicate.smashclub.common.util.CustomRegex;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Getter
@Setter
public class ReqAdminUserSaveDTO extends CustomRequestValidation {
    @Pattern(regexp="^[a-zA-Z0-9._-]{8,16}$", message="Username must be alphanumeric")
    @NotBlank(message="Username is required!")
    private String username;
    @NotBlank(message="Fullname is required!")
    private String fullName;
    @NotNull(message="Status is required!")
    private int status = CommonConstant.STATUS_ACTIVE;
    private String password;
    @NotNull(message="Role is required!")
    private ValAdminUserRoleDTO adminRole;

    @Override
    public void validate() {
        List<Map<String, Object>> validationObject = new ArrayList<>();

        if( username == null || username.isEmpty() ) {
            validationObject.add(constructValidationItem("username", "", "Username is required!"));
        }
        // Need regex validation and length
        else if( !CustomRegex.alphanumericRegex(username, 3, 16) ) {
            validationObject.add(constructValidationItem("username", username, "Username length must be between 3 to 16 and alphanumeric!"));
        }

        if( fullName == null || fullName.isEmpty() ) {
            validationObject.add(constructValidationItem("fullName", "", "Fullname is required!"));
        }
        // Need regex validation and length
        else if( !CustomRegex.stringRegex(fullName, 3, 16) ) {
            validationObject.add(constructValidationItem("fullName", fullName, "Fullname length must be between 3 to 16 and alphanumeric!"));
        }

        // Need regex validation and length
        if( password != null && !CustomRegex.stringRegex(password, 8, 16) ) {
            validationObject.add(constructValidationItem("password", password, "Password length must be between 8 to 16 and alphanumeric!"));
        }

        if( !List.of(CommonConstant.STATUS_ACTIVE, CommonConstant.STATUS_INACTIVE).contains(status) ) {
            validationObject.add(constructValidationItem("status", status, "Status is invalid!"));
        }

        validation = validationObject;
        super.validate();
    }
}
