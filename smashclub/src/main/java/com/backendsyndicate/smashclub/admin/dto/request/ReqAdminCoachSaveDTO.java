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
        List<Object> validationObject = new ArrayList<>();

        if( coachCode == null || coachCode.isEmpty() ) {
            validationObject.add(Map.of(
                    "field", "coachCode",
                    "rejected_value", "",
                    "message", "Coach code is required!"
            ));
        }
        // Need regex validation

        if( coachName == null || coachName.isEmpty() ) {
            validationObject.add(Map.of(
                    "field", "coachName",
                    "rejected_value", "",
                    "message", "Coach name is required!"
            ));
        }
        // Need regex validation

        if( pricePerHour == null ) {
            validationObject.add(Map.of(
                    "field", "pricePerHour",
                    "rejected_value", "",
                    "message", "Price per hour is required!"
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
