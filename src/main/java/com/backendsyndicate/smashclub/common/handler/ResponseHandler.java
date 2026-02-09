package com.backendsyndicate.smashclub.common.handler;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;  // TAMBAHKAN INI!
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component  // TAMBAHKAN INI! PASTI
public class ResponseHandler {
    /**
     * Here is where responseFormat is configured
     *
     * @param message
     * @param status
     * @param errorCode
     * @param data
     * @param request
     * @return
     */
    public Map<String, Object> constructResponseFormat(
            String message,
            HttpStatus status,
            Object errorCode,
            Object data,
            HttpServletRequest request
    ) {
        Map<String,Object> m = new HashMap<>();
        m.put("message",message);
        m.put("status",status.value());
        m.put("data",data==null?"":data);
        m.put("timestamp", Instant.now().toString());
        m.put("success",!status.isError());
        if(errorCode!=null){
            m.put("errorCode",errorCode);
            m.put("path",request.getRequestURI());
        }

        return m;
    }

    public ResponseEntity<Object> handleResponse(
            String message,
            HttpStatus status,
            Object errorCode,
            Object data,
            HttpServletRequest request
    ) {
        Map<String, Object> m = constructResponseFormat(message, status, errorCode, data, request);
        return new ResponseEntity<>(m,status);
    }

    public ResponseEntity<Object> handleResponse(
            String message,
            HttpStatus status,
            Object errorCode,
            Object data,
            WebRequest request
    ){

        Map<String,Object> m = new HashMap<>();
        m.put("message",message);
        m.put("status",status.value());
        m.put("data",data==null?"":data);
        m.put("timestamp", Instant.now().toString());
        m.put("success",!status.isError());
        if(errorCode!=null){
            m.put("errorCode",errorCode);
            m.put("path",request.getContextPath());
        }
        return new ResponseEntity<>(m,status);
    }
}