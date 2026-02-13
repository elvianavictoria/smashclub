package com.backendsyndicate.smashclub.admin.dto.request;

import com.backendsyndicate.smashclub.admin.core.IValidation;
import lombok.Getter;

import java.util.List;
import java.util.Map;

public class CustomRequestValidation implements IValidation {
    protected boolean isSuccess;
    @Getter
    protected List<Map<String, Object>> validation;

    @Override
    public boolean isValidated() {
        return isSuccess;
    }

    @Override
    public void validate() {
        isSuccess = validation.isEmpty();
    }

    public static Map<String, Object> constructValidationItem(String field, Object rejectedValue, String message) {
        return Map.of(
            "field", field,
            "rejected_value", rejectedValue != null ? rejectedValue : "",
            "message", message
        );
    }
}
