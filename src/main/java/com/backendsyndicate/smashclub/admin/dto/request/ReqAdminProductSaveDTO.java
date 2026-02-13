package com.backendsyndicate.smashclub.admin.dto.request;

import com.backendsyndicate.smashclub.admin.core.IValidation;
import com.backendsyndicate.smashclub.admin.dto.validation.ValAdminProductProductVariantDTO;
import com.backendsyndicate.smashclub.common.constant.CommonConstant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Getter
@Setter
public class ReqAdminProductSaveDTO extends CustomRequestValidation {
    @NotNull(message="Product name is required!")
    @NotBlank(message="Product name is required!")
    @Pattern(regexp="^[a-zA-Z0-9._-]{3,16}$", message="Product name must be alphanumeric")
    private String productName;

//    @NotNull(message="Product description is required!")
//    @NotBlank(message="Product description is required!")
    private String productDesc;

    @NotNull(message="Category is required!")
    @NotBlank(message="Category is required!")
    private String category;

    @NotNull(message="Status is required!")
    private byte status;

    @NotNull(message="Product variants are required!")
    private List<ValAdminProductProductVariantDTO> productVariants;

    @Override
    public void validate() {
        List<Map<String, Object>> validationObject = new ArrayList<>();

        if( productName == null || productName.isEmpty() ) {
            validationObject.add(constructValidationItem("productName", "", "Product name is required!"));
        }
        // Need regex validation
        // Need to guard length

        if( category == null || category.isEmpty() ) {
            validationObject.add(constructValidationItem("category", "", "Category is required!"));
        }
        // Need to guard length
        else if( category.length() > 100 ) {
            validationObject.add(constructValidationItem("category", "", "Category length must not exceed 100 characters!"));
        }
        // Need regex validation

        // Guard productDesc length
        if( productDesc.length() > 2000 ) {
            validationObject.add(constructValidationItem("productDesc", "", "Product description length must not exceed 2000 characters!"));
        }

        if( !List.of(CommonConstant.STATUS_ACTIVE, CommonConstant.STATUS_INACTIVE).contains((int) status) ) {
            validationObject.add(constructValidationItem("status", status, "Status is invalid!"));
        }

        // Validate Variants
        for (ValAdminProductProductVariantDTO dtoItem : productVariants) {
            dtoItem.validate();

            if( !dtoItem.isValidated() ) {
                for (int j = 0; j < dtoItem.getValidation().size(); j++) {
                    Map<String, Object> validationItem = dtoItem.getValidation().get(j);
                    validationObject.add(constructValidationItem(
                            String.format("productVariants[%d].%s", j, validationItem.get("field")),
                            validationItem.get("value"),
                            validationItem.get("message").toString()
                    ));
                }
            }
        }

        validation = validationObject;
        super.validate();
    }
}
