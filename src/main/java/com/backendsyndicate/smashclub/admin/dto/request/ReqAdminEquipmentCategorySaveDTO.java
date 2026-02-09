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
public class ReqAdminEquipmentCategorySaveDTO {
    @NotNull(message="Category name is required!")
    @NotBlank(message="Category name is required!")
    @Pattern(regexp="^[a-zA-Z0-9._-]{3,16}$", message="Category name must be alphanumeric")
    private String categoryName;
    @NotNull
    private int status;
}
