package com.backendsyndicate.smashclub.common.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseWrapper {
    private String message;
    private List<ValidationError> errors;

    private String errorCode;
}