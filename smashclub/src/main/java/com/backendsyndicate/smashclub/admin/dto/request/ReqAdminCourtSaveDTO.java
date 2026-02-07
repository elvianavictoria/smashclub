package com.backendsyndicate.smashclub.admin.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Data
@Getter
@Setter
public class ReqAdminCourtSaveDTO {
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
    @NotBlank(message="Status is required!")
    private byte status;
}
