package com.backendsyndicate.smashclub.admin.dto.request;

import com.backendsyndicate.smashclub.common.constant.CommonConstant;
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
        List<Object> validationObject = new ArrayList<>();

        if( courtCode == null || courtCode.isEmpty() ) {
            validationObject.add(Map.of(
                    "field", "courtCode",
                    "rejected_value", "",
                    "message", "Court code is required!"
            ));
        }
        // Need regex validation

        if( courtName == null || courtName.isEmpty() ) {
            validationObject.add(Map.of(
                    "field", "courtName",
                    "rejected_value", "",
                    "message", "Court name is required!"
            ));
        }
        // Need regex validation

        if( openTime == null ) {
            validationObject.add(Map.of(
                    "field", "openTime",
                    "rejected_value", "",
                    "message", "Open time is required!"
            ));
        }

        if( closeTime == null ) {
            validationObject.add(Map.of(
                    "field", "closeTime",
                    "rejected_value", "",
                    "message", "Close time is required!"
            ));
        }

        if( !List.of(CommonConstant.STATUS_ACTIVE, CommonConstant.STATUS_INACTIVE).contains((int) status) ) {
            validationObject.add(Map.of(
                    "field", "status",
                    "rejected_value", status,
                    "message", "Status is invalid!"
            ));
        }

        validation = validationObject;
        super.validate();
    }
}
