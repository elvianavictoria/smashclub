package com.backendsyndicate.smashclub.admin.dto.request;

import com.backendsyndicate.smashclub.common.constant.CommonConstant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Getter
@Setter
public class ReqAdminCoachSaveDTO extends CustomRequestValidation {
    @NotNull(message="Coach code is required!")
    @NotBlank(message="Coach code is required!")
    @Pattern(regexp="^[a-zA-Z0-9._-]{3,16}$", message="Coach code must be alphanumeric!")
    private String coachCode;
    @NotNull(message="Coach name is required!")
    @NotBlank(message="Coach name is required!")
    @Pattern(regexp="^[a-zA-Z]{3,16}$", message="Coach name must contain letters only!")
    private String coachName;

    @NotNull(message="Price per hour is required!")
    private BigDecimal pricePerHour;

    @NotNull(message="Status is required!")
    private byte status;

    @Override
    public void validate() {
        List<Map<String, Object>> validationObject = new ArrayList<>();

        if( coachCode == null || coachCode.isEmpty() ) {
            validationObject.add(constructValidationItem("coachCode", "", "Coach code is required!"));
        }
        // Need regex validation
        // Need to guard length

        if( coachName == null || coachName.isEmpty() ) {
            validationObject.add(constructValidationItem("coachName", "", "Coach name is required!"));
        }
        // Need regex validation
        // Need to guard length

        if( pricePerHour == null ) {
            validationObject.add(constructValidationItem("pricePerHour", "", "Price per hour is required!"));
        }

        if( !List.of(CommonConstant.STATUS_ACTIVE, CommonConstant.STATUS_INACTIVE).contains((int) status) ) {
            validationObject.add(constructValidationItem("status", status, "Status is invalid!"));
        }

        validation = validationObject;
        super.validate();
    }
}
