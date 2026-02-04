package com.backendsyndicate.smashclub.admin.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter
@Setter
public class ReqAdminCoachSaveDTO {
    @Pattern(regexp="^[a-zA-Z0-9._-]{3,16}$", message="Coach code must be alphanumeric")
    private String coachCode;
    @Pattern(regexp="^[a-zA-Z0-9._-]{3,16}$", message="Coach name must be alphanumeric")
    private String coachName;

    private BigDecimal pricePerHour;
    private byte status;
}
