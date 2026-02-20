package com.backendsyndicate.smashclub.admin.service.master;

import com.backendsyndicate.smashclub.admin.core.ICRUD;
import com.backendsyndicate.smashclub.admin.core.IUploadWithVariants;
import com.backendsyndicate.smashclub.admin.dto.request.CustomRequestValidation;
import com.backendsyndicate.smashclub.admin.dto.response.RespAdminProductDetailDTO;
import com.backendsyndicate.smashclub.admin.dto.response.RespAdminProductListDTO;
import com.backendsyndicate.smashclub.admin.service.log.LogService;
import com.backendsyndicate.smashclub.common.constant.AdminConstant;
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
    @Autowired
    private LogService logService;

    private ModelMapper modelMapper = new ModelMapper();

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
                return GlobalResponse.failed("Product list is empty!", AdminConstant.ADMIN_PRODUCT_SERVICE_LIST_EMPTY, null, request);
            }

            page = page.map(new Function<Product, RespAdminProductListDTO>() {
                @Override
                public RespAdminProductListDTO apply(Product product) {
                    return mapListToDTO(product);
                }
            });
        } catch(Exception e) {
            Logging.handleException("AdminProductService", "findAll(Pageable pageable, HttpServletRequest request)", 33, AdminConstant.ADMIN_PRODUCT_SERVICE_LIST_EXCEPTION, e.getMessage());
            logService.writeErrorLog(AdminConstant.ADMIN_PRODUCT_SERVICE_LIST_EXCEPTION, "AdminProductService@findAll()", e.getMessage());
            return GlobalResponse.failed("Failed to get product list!", AdminConstant.ADMIN_PRODUCT_SERVICE_LIST_EXCEPTION, null, request);
        }

        return GlobalResponse.success("Successfully get product list!", page, request);
    }

    @Override
    public ResponseEntity<Object> findById(Long id, HttpServletRequest request) {
        RespAdminProductDetailDTO response = null;

        if( id == null ) {
            return GlobalResponse.failed("Product ID is required!", AdminConstant.ADMIN_PRODUCT_SERVICE_DETAIL_ID_REQUIRED, null, request);
        }

        try {
            Optional<Product> optionalProduct = productRepo.findById(id);
            if( optionalProduct.isEmpty() ) {
                return GlobalResponse.failed("Product not found!", AdminConstant.ADMIN_PRODUCT_SERVICE_DETAIL_NOT_FOUND, null, request);
            }

            Product product = optionalProduct.get();
            response = modelMapper.map(product, RespAdminProductDetailDTO.class);
        } catch(Exception e) {
            Logging.handleException("AdminProductService", "findById(Long id, HttpServletRequest request)", 86, AdminConstant.ADMIN_PRODUCT_SERVICE_DETAIL_EXCEPTION, e.getMessage());
            logService.writeErrorLog(AdminConstant.ADMIN_PRODUCT_SERVICE_DETAIL_EXCEPTION, "AdminProductService@findById()", e.getMessage());
            return GlobalResponse.failed("Failed to get product data!", AdminConstant.ADMIN_PRODUCT_SERVICE_DETAIL_EXCEPTION, null, request);
        }

        return GlobalResponse.success("Product data found!", response, request);
    }

    @Override
    public ResponseEntity<Object> save(Product product, HttpServletRequest request) {
        if( product == null ) {
            return GlobalResponse.failed("Product data is required!", AdminConstant.ADMIN_PRODUCT_SERVICE_SAVE_REQUEST_INVALID, null, request);
        }

        try {
            productRepo.save(product);
        } catch(Exception e) {
            Logging.handleException("AdminProductService", "save(Product product, HttpServletRequest request)", 73, AdminConstant.ADMIN_PRODUCT_SERVICE_SAVE_EXCEPTION, e.getMessage());
            logService.writeErrorLog(AdminConstant.ADMIN_PRODUCT_SERVICE_SAVE_EXCEPTION, "AdminProductService@save()", e.getMessage());

            return GlobalResponse.failed("Failed to save product data!", AdminConstant.ADMIN_PRODUCT_SERVICE_SAVE_EXCEPTION, null, request);
        }

        return GlobalResponse.success("Successfully save product data!", null, request);
    }

    @Override
    public ResponseEntity<Object> update(Long id, Product product, HttpServletRequest request) {
        if( id == null ) {
            return GlobalResponse.failed("Product ID is required!", AdminConstant.ADMIN_PRODUCT_SERVICE_UPDATE_ID_REQUIRED, null, request);
        }

        if( product == null ) {
            return GlobalResponse.failed("Product data is required!", AdminConstant.ADMIN_PRODUCT_SERVICE_UPDATE_REQUEST_INVALID, null, request);
        }

        try {
            Optional<Product> optionalProduct = productRepo.findById(id);
            if( optionalProduct.isEmpty() ) {
                return GlobalResponse.failed("Product data not found!", AdminConstant.ADMIN_PRODUCT_SERVICE_UPDATE_NOT_FOUND, null, request);
            }

            Product productDB = optionalProduct.get();
            productDB.setProductName(product.getProductName());
            productDB.setProductDesc(product.getProductDesc());
            productDB.setCategory(product.getCategory());
            if( product.getDefaultImgLink() != null ) productDB.setDefaultImgLink(product.getDefaultImgLink());
            productDB.setStatus(product.getStatus());

            if( !adminProductVariantService.save(id, product.getProductVariants()) ) {
                return GlobalResponse.failed("Failed to save variants!", AdminConstant.ADMIN_PRODUCT_SERVICE_UPDATE_VARIANT_FAILED, null, request);
            }
        } catch(Exception e) {
            Logging.handleException("AdminProductService", "update(Long id, Product product, HttpServletRequest request)", 120, AdminConstant.ADMIN_PRODUCT_SERVICE_UPDATE_EXCEPTION, e.getMessage());
            logService.writeErrorLog(AdminConstant.ADMIN_PRODUCT_SERVICE_UPDATE_EXCEPTION, "AdminProductService@update()", e.getMessage());
            return GlobalResponse.failed("Failed to update product data!", AdminConstant.ADMIN_PRODUCT_SERVICE_UPDATE_EXCEPTION, null, request);
        }

        return GlobalResponse.success("Successfully updated product data!", null, request);
    }

    @Override
    public ResponseEntity<Object> delete(Long id, HttpServletRequest request) {
        if( id == null ) {
            return GlobalResponse.failed("Product ID is required!", AdminConstant.ADMIN_PRODUCT_SERVICE_DELETE_ID_REQUIRED, null, request);
        }

        try {
            Optional<Product> optionalProduct = productRepo.findById(id);
            if( optionalProduct.isEmpty() ) {
                return GlobalResponse.failed("Product data not found!", AdminConstant.ADMIN_PRODUCT_SERVICE_DELETE_NOT_FOUND, null, request);
            }

            if(adminProductVariantService.deleteByProductId(id)) {
                productRepo.deleteById(id);
            }
        } catch(Exception e) {
            Logging.handleException("AdminProductService", "delete(Long id, HttpServletRequest request)", 150, AdminConstant.ADMIN_PRODUCT_SERVICE_DELETE_EXCEPTION, e.getMessage());
            logService.writeErrorLog(AdminConstant.ADMIN_PRODUCT_SERVICE_DELETE_EXCEPTION, "AdminProductService@delete()", e.getMessage());
            return GlobalResponse.failed("Failed to delete product data!", AdminConstant.ADMIN_PRODUCT_SERVICE_DELETE_EXCEPTION, null, request);
        }

        return GlobalResponse.success("Successfully deleted product data!", null, request);
    }

    @Override
    public ResponseEntity<Object> save(Product product, MultipartFile file, Map<String, MultipartFile> variants, HttpServletRequest request) {
        if( product == null ) {
            return GlobalResponse.failed("Product data is required!", AdminConstant.ADMIN_PRODUCT_SERVICE_SAVE_FILE_REQUEST_INVALID, null, request);
        }

        String defaultImgUrl = cloudinaryService.uploadImageGetUrl("product", file);
        if( defaultImgUrl == null || defaultImgUrl.isEmpty() ) {
            return GlobalResponse.failed("Failed to upload product image!", AdminConstant.ADMIN_PRODUCT_SERVICE_SAVE_FILE_IMAGE_ERROR, null, request);
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
            return GlobalResponse.failed("Failed to upload variant image!", AdminConstant.ADMIN_PRODUCT_SERVICE_SAVE_FILE_VARIANT_FAILED, variantValidationItems, request);
        }

        return save(product, request);
    }

    @Override
    public ResponseEntity<Object> update(Long id, Product product, MultipartFile file, Map<String, MultipartFile> variants, HttpServletRequest request) {
        if( id == null ) {
            return GlobalResponse.failed("Product ID is required!", AdminConstant.ADMIN_PRODUCT_SERVICE_UPDATE_FILE_ID_REQUIRED, null, request);
        }

        if( product == null ) {
            return GlobalResponse.failed("Product data is required!", AdminConstant.ADMIN_PRODUCT_SERVICE_UPDATE_FILE_REQUEST_INVALID, null, request);
        }

        if( file != null ) {
            String defaultImgUrl = cloudinaryService.uploadImageGetUrl("product", file);
            if( defaultImgUrl == null || defaultImgUrl.isEmpty() ) {
                return GlobalResponse.failed("Failed to upload product image!", AdminConstant.ADMIN_PRODUCT_SERVICE_UPDATE_FILE_IMAGE_ERROR, null, request);
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
            return GlobalResponse.failed("Failed to upload variant image!", AdminConstant.ADMIN_PRODUCT_SERVICE_UPDATE_FILE_VARIANT_FAILED, variantValidationItems, request);
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
