package com.backendsyndicate.smashclub.common.handler;

import com.backendsyndicate.smashclub.common.util.GlobalResponse;
import com.backendsyndicate.smashclub.common.util.Logging;
import com.backendsyndicate.smashclub.common.util.RequestCapture;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Global Error Code : X
 *  1. Request Data Format 01
 *  2. SQL
 *  3. Security
 *  4. File
 *  5. Other
 *  X01001
 *  X01002
 */
@RestControllerAdvice
@Configuration
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private List<Map<String,Object>> apiSubErrors = new ArrayList<>();

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String,Object> m = new HashMap<>();
        apiSubErrors.clear();
        for(FieldError error : ex.getBindingResult().getFieldErrors()) {
            m = new HashMap<>();
            m.put("field",error.getField());
            m.put("message",error.getDefaultMessage());
            m.put("rejected_value",error.getRejectedValue());
            apiSubErrors.add(m);
        }

        HttpServletRequest requestx = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return GlobalResponse.failed("Format tidak valid", "X01001", apiSubErrors, requestx);
    }

    @ExceptionHandler(UnexpectedRollbackException.class)
    public ResponseEntity<Object> unexpectedRollbackException(UnexpectedRollbackException ex, HttpServletRequest request){
        Logging.handleException("GlobalExceptionHandler","unexpectedRollbackException "+ RequestCapture.allRequest(request), 0, "X02001", ex.getMessage());
        return GlobalResponse.failed("Rollback untuk Transaksi Telah dilakukan", "X02001", null, request);
    }
    @ExceptionHandler(FileAlreadyExistsException.class)
    public ResponseEntity<Object> fileAlreadyExistsException(FileAlreadyExistsException ex, HttpServletRequest request){
        Logging.handleException("GlobalExceptionHandler","fileAlreadyExistsException "+ RequestCapture.allRequest(request), 0, "X04001", ex.getMessage());
        return GlobalResponse.failed("File telah dibuat sebelumnya!","X04001", null, request);
    }

    public ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, HttpServletRequest request) {
        Logging.handleException("GlobalExceptionHandler","handleExceptionInternal ", 0, "X05999", "Request: " + RequestCapture.allRequest(request) + "; Message: " + ex.getMessage());
        return GlobalResponse.internalServerError("TERJADI KESALAHAN!", "X05999", request);
    }
}