package com.backendsyndicate.smashclub.admin.service.master;

import com.backendsyndicate.smashclub.admin.core.ICRUD;
import com.backendsyndicate.smashclub.admin.dto.response.RespAdminProductListDTO;
import com.backendsyndicate.smashclub.ecommerce.model.Product;
import com.backendsyndicate.smashclub.ecommerce.repo.ProductRepo;
import com.backendsyndicate.smashclub.common.util.DatetimeFormatting;
import com.backendsyndicate.smashclub.common.util.GlobalResponse;
import com.backendsyndicate.smashclub.common.util.Logging;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.function.Function;

@Service
@Transactional
public class AdminProductService implements ICRUD<Product, Long> {
    @Autowired
    private ProductRepo productRepo;
    private ModelMapper modelMapper = new ModelMapper();

    private String generateErrorCode(String methodNo, String errorNo) {
        return "ADM-PRD" + "-" + methodNo + "-" + errorNo;
    }

    @Override
    public ResponseEntity<Object> findAll(String keyword, Pageable pageable, HttpServletRequest request) {
        Page page = null;

        try {
            if( !keyword.isEmpty() ) {
                page = productRepo.findAllByProductNameContains(keyword, pageable);
            } else {
                page = productRepo.findAll(pageable);
            }

            if( page.isEmpty() ) {
                return GlobalResponse.failed("Product list is empty!", generateErrorCode("01", "001"), null, request);
            }

            page = page.map(new Function<Product, RespAdminProductListDTO>() {
                @Override
                public RespAdminProductListDTO apply(Product product) {
                    return mapListToDTO(product);
                }
            });
        } catch(Exception e) {
            Logging.handleException("ProductService", "findAll(Pageable pageable, HttpServletRequest request)", 33, generateErrorCode("01", "010"), e.getMessage());
            return GlobalResponse.failed("Failed to get product list!", generateErrorCode("01", "010"), null, request);
        }

        return GlobalResponse.success("Successfully get product list!", page, request);
    }

    @Override
    public ResponseEntity<Object> findById(Long id, HttpServletRequest request) {
        Product product = null;

        if( id == null ) {
            return GlobalResponse.failed("Product ID is required!", generateErrorCode("02", "001"), null, request);
        }

        try {
            Optional<Product> optionalProduct = productRepo.findById(id);
            if( optionalProduct.isEmpty() ) {
                return GlobalResponse.failed("Product not found!", generateErrorCode("02", "002"), null, request);
            }

            product = optionalProduct.get();
        } catch(Exception e) {
            return GlobalResponse.failed("Failed to get product data!", generateErrorCode("02", "010"), null, request);
        }

        return GlobalResponse.success("Product data found!", product, request);
    }

    @Override
    public ResponseEntity<Object> save(Product product, HttpServletRequest request) {
        if( product == null ) {
            return GlobalResponse.failed("Product data is required!", generateErrorCode("03", "001"), null, request);
        }

        try {
            productRepo.save(product);
        } catch(Exception e) {
            Logging.handleException("ProductService", "save(Product product, HttpServletRequest request)", 73, generateErrorCode("03", "010"), e.getMessage());
            return GlobalResponse.failed("Failed to save product data!", generateErrorCode("03", "010"), null, request);
        }

        return GlobalResponse.success("Successfully save product data!", null, request);
    }

    @Override
    public ResponseEntity<Object> update(Long id, Product product, HttpServletRequest request) {
        if( id == null ) {
            return GlobalResponse.failed("Product ID is required!", generateErrorCode("04", "001"), null, request);
        }

        if( product == null ) {
            return GlobalResponse.failed("Product data is required!", generateErrorCode("04", "002"), null, request);
        }

        try {
            Optional<Product> optionalProduct = productRepo.findById(id);
            if( optionalProduct.isEmpty() ) {
                return GlobalResponse.failed("Product data not found!", generateErrorCode("04", "003"), null, request);
            }

            Product productDB = optionalProduct.get();
            productDB.setProductName(product.getProductName());
            productDB.setStatus(product.getStatus());
        } catch(Exception e) {
            Logging.handleException("ProductService", "update(Long id, Product product, HttpServletRequest request)", 94, generateErrorCode("04", "010"), e.getMessage());
            return GlobalResponse.failed("Failed to update product data!", generateErrorCode("04", "010"), null, request);
        }

        return GlobalResponse.success("Successfully updated product data!", null, request);
    }

    @Override
    public ResponseEntity<Object> delete(Long id, HttpServletRequest request) {
        if( id == null ) {
            return GlobalResponse.failed("Product ID is required!", generateErrorCode("05", "001"), null, request);
        }

        try {
            Optional<Product> optionalProduct = productRepo.findById(id);
            if( optionalProduct.isEmpty() ) {
                return GlobalResponse.failed("Product data not found!", generateErrorCode("05", "002"), null, request);
            }

            productRepo.deleteById(id);
        } catch(Exception e) {
            Logging.handleException("ProductService", "delete(Long id, HttpServletRequest request)", 120, generateErrorCode("05", "010"), e.getMessage());
            return GlobalResponse.failed("Failed to delete product data!", generateErrorCode("05", "010"), null, request);
        }

        return GlobalResponse.success("Successfully deleted product data!", null, request);
    }

    private RespAdminProductListDTO mapListToDTO(Product product) {
        RespAdminProductListDTO result = modelMapper.map(product, RespAdminProductListDTO.class);

        return result;
    }
}
