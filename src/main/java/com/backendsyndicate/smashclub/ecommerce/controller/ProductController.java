package com.backendsyndicate.smashclub.ecommerce.controller;

import com.backendsyndicate.smashclub.ecommerce.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping("/search")
    public ResponseEntity<Object> searchProducts(@RequestParam(value = "keyword", required = false) String keyword, HttpServletRequest request) {
        Pageable pageable = null;
        pageable = PageRequest.of(0, 25, Sort.by("id").descending());
        return productService.findAll(keyword, pageable, request);
    }

    @GetMapping("/{productID}")
    public ResponseEntity<Object> productDetail(@PathVariable("productID") Long productID, HttpServletRequest request) {
        return productService.findById(productID, request);
    }
}
