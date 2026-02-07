package com.backendsyndicate.smashclub.admin.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ReqAdminLoginDTO {
    @NotNull
    @NotBlank
    private String username;
    @NotNull
    @NotBlank
    private String password;
}
