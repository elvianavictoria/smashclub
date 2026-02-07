package com.backendsyndicate.smashclub.admin.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ReqAdminEquipmentCategorySaveDTO {
    @Pattern(regexp="^[a-zA-Z0-9._-]{3,16}$", message="Category name must be alphanumeric")
    private String categoryName;
    private int status;
}
