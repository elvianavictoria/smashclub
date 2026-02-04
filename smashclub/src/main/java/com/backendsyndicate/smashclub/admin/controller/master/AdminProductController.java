package com.backendsyndicate.smashclub.admin.controller.master;

import com.backendsyndicate.smashclub.admin.dto.request.ReqAdminProductSaveDTO;
import com.backendsyndicate.smashclub.admin.service.master.AdminProductService;
import com.backendsyndicate.smashclub.ecommerce.model.Product;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/admin/product")
public class AdminProductController {
    @Autowired
    private AdminProductService adminProductService;
    private ModelMapper modelMapper = new ModelMapper();

    @GetMapping
    public ResponseEntity<Object> productList(@RequestParam String keyword, @RequestParam int page, @RequestParam int size, HttpServletRequest request) {
        Pageable pageable = PageRequest.of(page, size);
        return adminProductService.findAll(keyword, pageable, request);
    }

    @GetMapping("{productId}")
    public ResponseEntity<Object> productDetail(@PathVariable Long productId, HttpServletRequest request) {
        return adminProductService.findById(productId, request);
    }

    @PostMapping("save")
    public ResponseEntity<Object> productSave(@RequestBody ReqAdminProductSaveDTO dto, HttpServletRequest request) {
        Product product = modelMapper.map(dto, Product.class);
        return adminProductService.save(product, request);
    }

    @PutMapping("update/{productId}")
    public ResponseEntity<Object> productUpdate(@PathVariable Long productId, @RequestBody ReqAdminProductSaveDTO dto, HttpServletRequest request) {
        Product product = modelMapper.map(dto, Product.class);
        return adminProductService.update(productId, product, request);
    }

    @DeleteMapping("delete/{productId}")
    public ResponseEntity<Object> productDelete(@PathVariable Long productId, HttpServletRequest request) {
        return adminProductService.delete(productId, request);
    }
}
