package com.backendsyndicate.smashclub.admin.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ReqAdminProductSaveDTO {
    @NotNull(message="Product name is required!")
    @NotBlank(message="Product name is required!")
    @Pattern(regexp="^[a-zA-Z0-9._-]{3,16}$", message="Product name must be alphanumeric")
    private String productName;

    @NotNull(message="Product description is required!")
    @NotBlank(message="Product description is required!")
    private String productDesc;

    @NotNull(message="Category is required!")
    @NotBlank(message="Category is required!")
    private String category;

    @NotNull(message="Status is required!")
    @NotBlank(message="Status is required!")
    private byte status;

    private String defaultImgLink = "";
}
