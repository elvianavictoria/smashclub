package com.backendsyndicate.smashclub.admin.controller.master;

import com.backendsyndicate.smashclub.admin.dto.request.ReqAdminProductSaveDTO;
import com.backendsyndicate.smashclub.admin.service.master.AdminProductService;
import com.backendsyndicate.smashclub.common.constant.PermissionConstant;
import com.backendsyndicate.smashclub.common.util.GlobalResponse;
import com.backendsyndicate.smashclub.common.util.Logging;
import com.backendsyndicate.smashclub.ecommerce.model.Product;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/v1/admin/product")
public class AdminProductController {
    @Autowired
    private AdminProductService adminProductService;
    private ModelMapper modelMapper = new ModelMapper();

    @PreAuthorize("hasAuthority('" + PermissionConstant.PRODUCT_READ_CODE + "')")
    @GetMapping
    public ResponseEntity<Object> productList(@RequestParam String keyword, @RequestParam int page, @RequestParam int size, HttpServletRequest request) {
        Pageable pageable = PageRequest.of(page, size);
        return adminProductService.findAll(keyword, pageable, request);
    }

    @PreAuthorize("hasAuthority('" + PermissionConstant.PRODUCT_EDIT_CODE + "')")
    @GetMapping("{productId}")
    public ResponseEntity<Object> productDetail(@PathVariable Long productId, HttpServletRequest request) {
        return adminProductService.findById(productId, request);
    }

    @PreAuthorize("hasAuthority('" + PermissionConstant.PRODUCT_CREATE_CODE + "')")
    @PostMapping("save")
    public ResponseEntity<Object> productSave(
//        @RequestBody ReqAdminProductSaveDTO dto,
        @RequestParam MultipartFile defaultImgLink,
        @RequestParam String productName,
        @RequestParam(required = false) String productDesc,
        @RequestParam String category,
        @RequestParam byte status,
        HttpServletRequest request
    ) {
        ReqAdminProductSaveDTO dto = new ReqAdminProductSaveDTO();
        dto.setProductName(productName);
        dto.setProductDesc(productDesc);
        dto.setCategory(category);
        dto.setStatus(status);
        dto.validate();

        if( !dto.isValidated() ) {
            return GlobalResponse.failed("Format tidak valid!", "X01001", dto.getValidation(), request);
        }

        Product product = modelMapper.map(dto, Product.class);
        return adminProductService.save(product, request);
    }

    @PreAuthorize("hasAuthority('" + PermissionConstant.PRODUCT_EDIT_CODE + "')")
    @PutMapping("update/{productId}")
    public ResponseEntity<Object> productUpdate(
            @PathVariable Long productId,
            @RequestParam(required = false) MultipartFile defaultImgLink,
//            @RequestPart("data") ReqAdminProductSaveDTO dto,
            @RequestParam String productName,
            @RequestParam(required = false) String productDesc,
            @RequestParam String category,
            @RequestParam byte status,
            HttpServletRequest request
    ) {
        ReqAdminProductSaveDTO dto = new ReqAdminProductSaveDTO();
        dto.setProductName(productName);
        dto.setProductDesc(productDesc);
        dto.setCategory(category);
        dto.setStatus(status);
        dto.validate();

        if( !dto.isValidated() ) {
            return GlobalResponse.failed("Format tidak valid!", "X01001", dto.getValidation(), request);
        }

        Product product = modelMapper.map(dto, Product.class);
        return adminProductService.update(productId, product, defaultImgLink, request);
    }

    @PreAuthorize("hasAuthority('" + PermissionConstant.PRODUCT_DELETE_CODE + "')")
    @DeleteMapping("delete/{productId}")
    public ResponseEntity<Object> productDelete(@PathVariable Long productId, HttpServletRequest request) {
        return adminProductService.delete(productId, request);
    }
}
