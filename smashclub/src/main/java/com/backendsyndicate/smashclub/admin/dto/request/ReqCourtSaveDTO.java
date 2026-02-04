package com.backendsyndicate.smashclub.admin.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Getter
@Setter
public class ReqCourtSaveDTO {
    @Pattern(regexp="^[a-zA-Z0-9._-]{3,16}$", message="Court code must be alphanumeric")
    private String courtCode;
    @Pattern(regexp="^[a-zA-Z0-9._-]{3,16}$", message="Court name must be alphanumeric")
    private String courtName;

    private LocalTime openTime;
    private LocalTime closeTime;


    private byte status;
}
