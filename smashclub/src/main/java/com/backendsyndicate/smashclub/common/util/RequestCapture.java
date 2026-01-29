package com.backendsyndicate.smashclub.common.util;

import com.backendsyndicate.smashclub.common.config.MainConfig;
import jakarta.servlet.http.HttpServletRequest;
import org.json.simple.JSONObject;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class RequestCapture {
    public static String allRequest(WebRequest webRequest) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Map<String, Object> requestData = processingData(request);
        String strValue = new JSONObject(requestData).toString();
        return strValue;
    }

    public static String allRequest(HttpServletRequest requestx) {
        ContentCachingRequestWrapper request = new ContentCachingRequestWrapper(requestx, MainConfig.getCacheLimit());
        Map<String, Object> requestData = processingData(request);
        String strValue = new JSONObject(requestData).toString();
        return strValue;
    }

    public static Map<String, Object> processingData(HttpServletRequest request) {
        String headerName = "";
        String paramName = "";
        Map<String, Object> requestData = new HashMap<>();
        Map<String, Object> requestParam = new HashMap<>();
        Map<String, Object> requestHeader = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();

        while(headerNames.hasMoreElements()) {
            headerName = headerNames.nextElement();
            requestHeader.put(headerName, request.getHeader(headerName));
        }

        Enumeration<String> paramNames = request.getParameterNames();
        while(paramNames.hasMoreElements()) {
            paramName = paramNames.nextElement();
            requestHeader.put(paramName, request.getParameter(paramName));
        }

        requestData.put("authType", request.getAuthType());
        requestData.put("method", request.getMethod());
        requestData.put("serverName", request.getServerName());
        requestData.put("serverPort", request.getServerPort());
        requestData.put("session", request.getSession());
        requestData.put("queryString", request.getQueryString());
        requestData.put("remoteAddr", request.getRemoteAddr());
        requestData.put("remoteHost", request.getRemoteHost());
        requestData.put("pathInfo", request.getPathInfo());
        requestData.put("locale", request.getLocale());
        requestData.put("principal", request.getUserPrincipal());
        requestData.put("isSecure", request.isSecure());
        requestData.put("requestHeader", requestHeader);
        requestData.put("requestParam", requestParam);

        try {
            if( request.getMethod().equalsIgnoreCase("POST") || request.getMethod().equalsIgnoreCase("PUT") || request.getMethod().equalsIgnoreCase("PATCH") ) {
                requestData.put("requestBody", request.toString());
            }
        } catch(Exception e) {
            Logging.handleException("RequestCapture", "processingData", 25, "", e.getMessage());
        }

        return requestData;
    }
}
