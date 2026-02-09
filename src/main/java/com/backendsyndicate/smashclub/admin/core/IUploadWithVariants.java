package com.backendsyndicate.smashclub.admin.core;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface IUploadWithVariants<T, TID> {
    public ResponseEntity<Object> save(T t, MultipartFile file, Map<String, MultipartFile> variants, HttpServletRequest request);
    public ResponseEntity<Object> update(TID id, T t, MultipartFile file, Map<String, MultipartFile> variants, HttpServletRequest request);
}
