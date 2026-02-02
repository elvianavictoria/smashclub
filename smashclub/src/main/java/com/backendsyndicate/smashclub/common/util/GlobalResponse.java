package com.backendsyndicate.smashclub.common.util;

import com.backendsyndicate.smashclub.common.handler.ResponseHandler;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GlobalResponse {
    public static ResponseEntity<Object> success(String message, Object data, HttpServletRequest request) {
        return new ResponseHandler().handleResponse(message, HttpStatus.OK, null, data, request);
    }

    public static ResponseEntity<Object> created(String message, Object data, HttpServletRequest request) {
        return new ResponseHandler().handleResponse(message, HttpStatus.CREATED, null, data, request);
    }

    public static ResponseEntity<Object> failed(String message, String errorCode, Object data, HttpServletRequest request) {
        return new ResponseHandler().handleResponse(message, HttpStatus.BAD_REQUEST, errorCode, data, request);
    }

    public static ResponseEntity<Object> notFound(String message, String errorCode, HttpServletRequest request) {
        return new ResponseHandler().handleResponse(message, HttpStatus.NOT_FOUND, errorCode, null, request);
    }

    public static ResponseEntity<Object> unauthorized(String message, String errorCode, HttpServletRequest request) {
        return new ResponseHandler().handleResponse(message, HttpStatus.UNAUTHORIZED, errorCode, null, request);
    }

    public static ResponseEntity<Object> mediaTypeNotSupported(String message, String errorCode, HttpServletRequest request) {
        return new ResponseHandler().handleResponse(message, HttpStatus.UNSUPPORTED_MEDIA_TYPE, errorCode, null, request);
    }

    public static ResponseEntity<Object> internalServerError(String message, String errorCode, HttpServletRequest request){
        return new ResponseHandler().handleResponse(message, HttpStatus.INTERNAL_SERVER_ERROR, errorCode, null, request);
    }
}
