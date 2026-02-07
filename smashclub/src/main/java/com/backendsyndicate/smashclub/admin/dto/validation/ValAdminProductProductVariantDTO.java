package com.backendsyndicate.smashclub.admin.dto.validation;

import com.backendsyndicate.smashclub.admin.dto.request.CustomRequestValidation;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Getter
@Setter
public class ValAdminProductProductVariantDTO extends CustomRequestValidation {
    @NotNull
    private Long id;
    @NotNull
    @NotBlank
    private String name;
    @NotNull
    @NotBlank
    private String sku;
    @NotNull
    private BigDecimal price;
    @NotNull
    private int stock;

    @Override
    public void validate() {
        List<Map<String, Object>> validationObject = new ArrayList<>();

        if( id == null ) {
            validationObject.add(constructValidationItem("id", "", "ID is required!"));
        }

        if( name == null || name.isEmpty() ) {
            validationObject.add(constructValidationItem("name", "", "Name is required!"));
        }

        if( sku == null || name.isEmpty() ) {
            validationObject.add(constructValidationItem("sku", "", "SKU is required!"));
        }

        if( price == null ) {
            validationObject.add(constructValidationItem("price", "", "Price is required!"));
        }

        if( stock < 0 ) {
            validationObject.add(constructValidationItem("stock", stock, "Stock is invalid!"));
        }

        validation = validationObject;
        super.validate();
    }
}
