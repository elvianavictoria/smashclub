package com.backendsyndicate.smashclub.admin.dto.request;

import com.backendsyndicate.smashclub.admin.dto.validation.ValAdminEquipmentEquipmentCategoryDTO;
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
public class ReqAdminEquipmentSaveDTO extends CustomRequestValidation {
    @NotNull(message = "Equipment name is required!")
    @NotBlank(message = "Equipment name is required!")
    @Pattern(regexp="^[a-zA-Z0-9._-]{3,16}$", message="Equipment name must be alphanumeric")
    private String equipmentName;
    @NotNull(message = "Brand is required!")
    @NotBlank(message = "Brand is required!")
    @Pattern(regexp="^[a-zA-Z0-9._-]{3,16}$", message="Brand must be alphanumeric")
    private String brand;
    @NotNull(message = "Type is required!")
    @NotBlank(message = "Type is required!")
    @Pattern(regexp="^[a-zA-Z0-9._-]{3,16}$", message="Type must be alphanumeric")
    private String type;

    @NotNull(message = "Price is required!")
    private BigDecimal price;
    @NotNull(message = "Stock is required!")
    private int stock;
    private String description = "";
    @NotNull(message = "Status is required!")
    private int status;
    
    private ValAdminEquipmentEquipmentCategoryDTO equipmentCategory;

    @Override
    public void validate() {
        List<Map<String, Object>> validationObject = new ArrayList<>();

        if( equipmentName == null || equipmentName.isEmpty() ) {
            validationObject.add(constructValidationItem("equipmentName", "", "Equipment name is required!"));
        }
        // Need regex validation
        
        if( brand == null || brand.isEmpty() ) {
            validationObject.add(constructValidationItem("brand", "", "Brand is required!"));
        }
        // Need regex validation
        // Need to guard length

        if( type == null || type.isEmpty() ) {
            validationObject.add(constructValidationItem("type", "", "Type is required!"));
        }
        // Need regex validation
        // Need to guard length

        if( price == null ) {
            validationObject.add(constructValidationItem("price", "", "Price is required!"));
        }

        if( stock < 0 ) {
            validationObject.add(constructValidationItem("stock", stock, "Stock is required!"));
        }

        if( !List.of(CommonConstant.STATUS_ACTIVE, CommonConstant.STATUS_INACTIVE).contains((int) status) ) {
            validationObject.add(constructValidationItem("status", status, "Status is invalid!"));
        }

        validation = validationObject;
        super.validate();
    }
}
