package com.backendsyndicate.smashclub.admin.dto.request;

import com.backendsyndicate.smashclub.admin.core.IValidation;
import lombok.Getter;

import java.util.List;

public class CustomRequestValidation implements IValidation {
    protected boolean isSuccess;
    @Getter
    protected List<Object> validation;

    @Override
    public boolean isValidated() {
        return isSuccess;
    }

    @Override
    public void validate() {
        isSuccess = validation.isEmpty();
    }
}
