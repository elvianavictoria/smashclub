package com.backendsyndicate.smashclub.external.core;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface IWebhook<T> {
    public ResponseEntity<Object> callback(T t, HttpServletRequest request);
}
