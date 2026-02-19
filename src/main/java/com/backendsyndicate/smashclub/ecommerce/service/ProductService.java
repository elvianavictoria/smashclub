package com.backendsyndicate.smashclub.ecommerce.service;

import com.backendsyndicate.smashclub.common.util.GlobalResponse;
import com.backendsyndicate.smashclub.ecommerce.core.IProduct;
import com.backendsyndicate.smashclub.ecommerce.dto.response.RespProductDTO;
import com.backendsyndicate.smashclub.ecommerce.dto.response.RespProductVariantDTO;
import com.backendsyndicate.smashclub.ecommerce.model.Product;
import com.backendsyndicate.smashclub.ecommerce.repo.ProductRepo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

///**
// * Module Code: PROD
// */

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService implements IProduct<Object> {
    @Autowired
    private ProductRepo productRepo;

    private ModelMapper modelMapper = new ModelMapper();

    private String generateErrorCode(String methodNo, String errorNo) {
        return "PROD-" + methodNo + "E" + errorNo;
    }

    /**
     * Code: 01
     *
     * @param pageable
     * @param request
     * @return
     */

    @Override
    public ResponseEntity<Object> findAll(String keyword, Pageable pageable, HttpServletRequest request) {
        Page<Product> page;
        try {
            if( keyword!= null && !keyword.isBlank() ) {
                page = productRepo.searchActiveProductsByCategoryOrName(keyword, pageable);
            } else {
                page = productRepo.findAllActiveProducts(pageable);
            }

            if( page.isEmpty() ) {
                return GlobalResponse.failed("Product list is empty!", generateErrorCode("01", "001"), page, request);
            }

            Page<RespProductDTO> response = page.map(this::mapListToDTO);
            return GlobalResponse.success("Successfully get product list!", response, request);

        } catch(Exception e) {
            return GlobalResponse.failed("Failed to get product list!", generateErrorCode("01", "010"), null, request);
        }

    }

    @Override
    public ResponseEntity<Object> findById(Long id, HttpServletRequest request) {
        RespProductDTO response = null;

        if( id == null ) {
            return GlobalResponse.failed("Product ID is required!", generateErrorCode("02", "001"), null, request);
        }

        try {
            Optional<Product> optionalProduct = productRepo.findById(id);
            if( optionalProduct.isEmpty() ) {
                return GlobalResponse.failed("Product not found!", generateErrorCode("02", "002"), null, request);
            }

            Product product = optionalProduct.get();
            response = mapListToDTO(product);
        } catch(Exception e) {
            return GlobalResponse.failed("Failed to get product data!", generateErrorCode("02", "010"), null, request);
        }

        return GlobalResponse.success("Product data found!", response, request);
    }

    private RespProductDTO mapListToDTO(Product product) {
        RespProductDTO dto = new RespProductDTO();
        dto.setId(product.getId());
        dto.setProductName(product.getProductName());
        dto.setProductDesc(product.getProductDesc());
        dto.setCategory(product.getCategory());
        dto.setDefaultImgLink(product.getDefaultImgLink());
        dto.setStatus(product.getStatus());

        List<RespProductVariantDTO> variantDTOs = product.getProductVariants()
                .stream()
                .map(v -> {
                    RespProductVariantDTO vd = new RespProductVariantDTO();
                    vd.setId(v.getId());
                    vd.setVariantName(v.getVariantName());
                    vd.setSku(v.getSku());
                    vd.setPrice(v.getPrice());
                    vd.setStock(v.getStock());
                    vd.setVariantImgLink(v.getVariantImgLink());
                    return vd;
                })
                .toList();

        dto.setProductVariants(variantDTOs);
        return dto;
    }
}
