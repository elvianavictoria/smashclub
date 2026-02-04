package com.backendsyndicate.smashclub.admin.dto.request;

import com.backendsyndicate.smashclub.booking.model.EquipmentCategory;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter
@Setter
public class ReqEquipmentCategorySaveDTO {
    @Pattern(regexp="^[a-zA-Z0-9._-]{3,16}$", message="Category name must be alphanumeric")
    private String categoryName;
    private int status;
}
