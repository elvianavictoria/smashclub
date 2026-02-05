package com.backendsyndicate.smashclub.common.handler;

import com.backendsyndicate.smashclub.common.util.GlobalResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/** Response Body untuk masalah JWT / Security */
@Component("customAuthenticationEntryPoint")
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        /** Response Header */
        response.setHeader("Content-Type","application/json");
        int status = response.getStatus();
        status = status == 200 ? HttpServletResponse.SC_UNAUTHORIZED : HttpServletResponse.SC_FORBIDDEN;

        /** Response Code */
        String message = status == 401 ? "Unauthenticated" : "Unauthorized Access";
        response.setStatus(status);

        /** Response Body */
        Map<String, Object> data = new ResponseHandler().constructResponseFormat(
                message,
                HttpStatus.UNAUTHORIZED,
                "X01001",
                null,
                request
        );

        response.getOutputStream().println(new JSONObject(data).toString());
    }
}
