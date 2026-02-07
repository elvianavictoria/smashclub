package com.backendsyndicate.smashclub.admin.dto.request;

import com.backendsyndicate.smashclub.admin.core.IValidation;
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

    @Override
    public void validate() {
        List<Object> validationObject = new ArrayList<>();

        if( productName == null || productName.isEmpty() ) {
            validationObject.add(Map.of(
                    "field", "productName",
                    "rejected_value", "",
                    "message", "Product name is required!"
            ));
        }
        // Need regex validation

        if( category == null || category.isEmpty() ) {
            validationObject.add(Map.of(
                    "field", "category",
                    "rejected_value", "",
                    "message", "Category is required!"
            ));
        }
        // Need regex validation

        if( !List.of(CommonConstant.STATUS_ACTIVE, CommonConstant.STATUS_INACTIVE).contains((int) status) ) {
            validationObject.add(Map.of(
                    "field", "status",
                    "rejected_value", status,
                    "message", "Status is invalid!"
            ));
        }

        validation = validationObject;
        super.validate();
    }
}
