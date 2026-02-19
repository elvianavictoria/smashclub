package com.backendsyndicate.smashclub.admin.service.master;

import com.backendsyndicate.smashclub.admin.core.ICRUD;
import com.backendsyndicate.smashclub.admin.core.IUploadWithVariants;
import com.backendsyndicate.smashclub.admin.dto.request.CustomRequestValidation;
import com.backendsyndicate.smashclub.admin.dto.response.RespAdminProductDetailDTO;
import com.backendsyndicate.smashclub.admin.dto.response.RespAdminProductListDTO;
import com.backendsyndicate.smashclub.ecommerce.model.Product;
import com.backendsyndicate.smashclub.ecommerce.model.ProductVariant;
import com.backendsyndicate.smashclub.ecommerce.repo.ProductRepo;
import com.backendsyndicate.smashclub.common.util.GlobalResponse;
import com.backendsyndicate.smashclub.common.util.Logging;
import com.backendsyndicate.smashclub.ecommerce.repo.ProductVariantRepo;
import com.backendsyndicate.smashclub.external.dto.CloudinaryResponseDTO;
import com.backendsyndicate.smashclub.external.service.storage.CloudinaryService;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Transactional
public class AdminProductService implements ICRUD<Product, Long>, IUploadWithVariants<Product, Long> {
    @Autowired
    private AdminProductVariantService adminProductVariantService;
    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private ProductRepo productRepo;
    private ModelMapper modelMapper = new ModelMapper();

    private String generateErrorCode(String methodNo, String errorNo) {
        return "ADM-PRD" + "-" + methodNo + "-" + errorNo;
    }

    @Override
    public ResponseEntity<Object> findAll(String keyword, Integer status, Pageable pageable, HttpServletRequest request) {
        Page page = null;

        try {
            if( !keyword.isEmpty() ) {
                page = productRepo. findAllByProductNameContainsIgnoreCaseOrCategoryContainsIgnoreCase(pageable, keyword, keyword, request);
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
            Logging.handleException("AdminProductService", "findAll(Pageable pageable, HttpServletRequest request)", 33, generateErrorCode("01", "010"), e.getMessage());
            return GlobalResponse.failed("Failed to get product list!", generateErrorCode("01", "010"), null, request);
        }

        return GlobalResponse.success("Successfully get product list!", page, request);
    }

    @Override
    public ResponseEntity<Object> findById(Long id, HttpServletRequest request) {
        RespAdminProductDetailDTO response = null;

        if( id == null ) {
            return GlobalResponse.failed("Product ID is required!", generateErrorCode("02", "001"), null, request);
        }

        try {
            Optional<Product> optionalProduct = productRepo.findById(id);
            if( optionalProduct.isEmpty() ) {
                return GlobalResponse.failed("Product not found!", generateErrorCode("02", "002"), null, request);
            }

            Product product = optionalProduct.get();
            response = modelMapper.map(product, RespAdminProductDetailDTO.class);
        } catch(Exception e) {
            return GlobalResponse.failed("Failed to get product data!", generateErrorCode("02", "010"), null, request);
        }

        return GlobalResponse.success("Product data found!", response, request);
    }

    @Override
    public ResponseEntity<Object> save(Product product, HttpServletRequest request) {
        if( product == null ) {
            return GlobalResponse.failed("Product data is required!", generateErrorCode("03", "001"), null, request);
        }

        try {
            productRepo.save(product);
        } catch(Exception e) {
            Logging.handleException("AdminProductService", "save(Product product, HttpServletRequest request)", 73, generateErrorCode("03", "010"), e.getMessage());
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
            productDB.setProductDesc(product.getProductDesc());
            productDB.setCategory(product.getCategory());
            if( product.getDefaultImgLink() != null ) productDB.setDefaultImgLink(product.getDefaultImgLink());
            productDB.setStatus(product.getStatus());

            if( !adminProductVariantService.save(id, product.getProductVariants()) ) {
                return GlobalResponse.failed("Failed to save variants!", generateErrorCode("04", "004"), null, request);
            }
        } catch(Exception e) {
            Logging.handleException("AdminProductService", "update(Long id, Product product, HttpServletRequest request)", 120, generateErrorCode("04", "010"), e.getMessage());
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

            if(adminProductVariantService.deleteByProductId(id)) {
                productRepo.deleteById(id);
            }
        } catch(Exception e) {
            Logging.handleException("AdminProductService", "delete(Long id, HttpServletRequest request)", 150, generateErrorCode("05", "010"), e.getMessage());
            return GlobalResponse.failed("Failed to delete product data!", generateErrorCode("05", "010"), null, request);
        }

        return GlobalResponse.success("Successfully deleted product data!", null, request);
    }

    @Override
    public ResponseEntity<Object> save(Product product, MultipartFile file, Map<String, MultipartFile> variants, HttpServletRequest request) {
        if( product == null ) {
            return GlobalResponse.failed("Product data is required!", generateErrorCode("13", "001"), null, request);
        }

        String defaultImgUrl = cloudinaryService.uploadImageGetUrl("product", file);
        if( defaultImgUrl == null || defaultImgUrl.isEmpty() ) {
            return GlobalResponse.failed("Failed to upload product image!", generateErrorCode("13", "002"), null, request);
        }

        product.setDefaultImgLink(defaultImgUrl);
        List<Map<String, Object>> variantValidationItems = new ArrayList<>();
        for( Map.Entry<String, MultipartFile> entry: variants.entrySet() ) {
            String key = entry.getKey();
            MultipartFile variantFile = entry.getValue();

            if( variantFile != null ) {
                String variantImgUrl = cloudinaryService.uploadImageGetUrl("product/variant", variantFile);
                if( variantImgUrl == null || variantImgUrl.isEmpty() ) {
                    variantValidationItems.add(CustomRequestValidation.constructValidationItem(String.format("productVariants[%s].variantImgLink", key), "", String.format("Variant image %s upload process failed!", key)));
                } else {
                    product.getProductVariants().get(Integer.parseInt(key)).setVariantImgLink(variantImgUrl);
                }
            }
        }

        if( !variantValidationItems.isEmpty() ) {
            return GlobalResponse.failed("Failed to upload variant image!", generateErrorCode("13", "003"), variantValidationItems, request);
        }

        return save(product, request);
    }

    @Override
    public ResponseEntity<Object> update(Long id, Product product, MultipartFile file, Map<String, MultipartFile> variants, HttpServletRequest request) {
        if( id == null ) {
            return GlobalResponse.failed("Product ID is required!", generateErrorCode("14", "001"), null, request);
        }

        if( product == null ) {
            return GlobalResponse.failed("Product data is required!", generateErrorCode("14", "002"), null, request);
        }

        if( file != null ) {
            String defaultImgUrl = cloudinaryService.uploadImageGetUrl("product", file);
            if( defaultImgUrl == null || defaultImgUrl.isEmpty() ) {
                return GlobalResponse.failed("Failed to upload product image!", generateErrorCode("14", "003"), null, request);
            }

            product.setDefaultImgLink(defaultImgUrl);
        }

        List<Map<String, Object>> variantValidationItems = new ArrayList<>();
        List<ProductVariant> listTmp = new ArrayList<ProductVariant>();
        variants.forEach( (key, variantFile) -> {
            ProductVariant productVariantTmp;

            if( !key.equals("defaultImgLink") ) {
                String keyIndex = extractVariantKey(key);

                if( variantFile != null ) {
                    String variantImgUrl = cloudinaryService.uploadImageGetUrl("product/variant", variantFile);
                    if( variantImgUrl == null || variantImgUrl.isEmpty() ) {
                        variantValidationItems.add(CustomRequestValidation.constructValidationItem(String.format("productVariants[%s].variantImgLink", keyIndex), "", String.format("Variant image %s upload process failed!", keyIndex)));
                    } else {
                        product.getProductVariants().get(Integer.parseInt(keyIndex)).setVariantImgLink(variantImgUrl);
                    }
                }
            }
        } );

        if( !listTmp.isEmpty() ) {
            product.setProductVariants(listTmp);
        }

        if( !variantValidationItems.isEmpty() ) {
            return GlobalResponse.failed("Failed to upload variant image!", generateErrorCode("14", "004"), variantValidationItems, request);
        }

        return update(id, product, request);
    }

    private RespAdminProductListDTO mapListToDTO(Product product) {
        RespAdminProductListDTO result = modelMapper.map(product, RespAdminProductListDTO.class);

        return result;
    }

    private String extractVariantKey(String key) {
        Pattern pattern = Pattern.compile("variantImages\\[(\\d+)]");
        Matcher matcher = pattern.matcher(key);
        if( !matcher.matches() ) {
            Logging.printConsole("Failed to get index from " + key);
            return "";
        }

        return matcher.group(1);
    }
}
