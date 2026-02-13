package com.backendsyndicate.smashclub.admin.dto.request;

import com.backendsyndicate.smashclub.common.constant.CommonConstant;
import com.backendsyndicate.smashclub.common.util.CustomRegex;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Getter
@Setter
public class ReqAdminCourtSaveDTO extends CustomRequestValidation {
    @NotNull(message="Court code is required!")
    @NotBlank(message="Court code is required!")
    @Pattern(regexp="^[a-zA-Z0-9._-]{3,16}$", message="Court code must be alphanumeric")
    private String courtCode;
    @NotNull(message="Court name is required!")
    @NotBlank(message="Court name is required!")
    @Pattern(regexp="^[a-zA-Z0-9._-]{3,16}$", message="Court name must be alphanumeric")
    private String courtName;

    @NotNull(message="Open time is required!")
    @NotBlank(message="Open time is required!")
    private LocalTime openTime;
    @NotNull(message="Close time is required!")
    @NotBlank(message="Close time is required!")
    private LocalTime closeTime;

    @NotNull(message="Status is required!")
    private byte status;


    @Override
    public void validate() {
        List<Map<String, Object>> validationObject = new ArrayList<>();

        if( courtCode == null || courtCode.isEmpty() ) {
            validationObject.add(constructValidationItem("courtCode", "", "Court code is required!"));
        }
        // Need regex validation and length
        else if( !CustomRegex.alphanumericRegex(courtCode, 3, 50) ) {
            validationObject.add(constructValidationItem("courtCode", courtCode, "Court code length must be between 3 to 50 and alphanumeric!"));
        }

        if( courtName == null || courtName.isEmpty() ) {
            validationObject.add(constructValidationItem("courtName", "", "Court name is required!"));
        }
        // Need regex validation and length
        else if( !CustomRegex.stringRegex(courtName, 3, 100) ) {
            validationObject.add(constructValidationItem("courtName", courtName, "Court name length must be between 3 to 100 and alphanumeric!"));
        }

        if( openTime == null ) {
            validationObject.add(constructValidationItem("openTime", "", "Open time is required!"));
        }

        if( closeTime == null ) {
            validationObject.add(constructValidationItem("closeTime", "", "Close time is required!"));
        }

        if( !List.of(CommonConstant.STATUS_ACTIVE, CommonConstant.STATUS_INACTIVE).contains((int) status) ) {
            validationObject.add(constructValidationItem("status", status, "Status is invalid!"));
        }

        validation = validationObject;
        super.validate();
    }
}
