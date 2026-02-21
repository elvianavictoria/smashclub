package com.backendsyndicate.smashclub.admin.service.master;

import com.backendsyndicate.smashclub.admin.service.log.LogService;
import com.backendsyndicate.smashclub.common.constant.AdminConstant;
import com.backendsyndicate.smashclub.common.util.Logging;
import com.backendsyndicate.smashclub.ecommerce.model.Product;
import com.backendsyndicate.smashclub.ecommerce.model.ProductVariant;
import com.backendsyndicate.smashclub.ecommerce.repo.ProductRepo;
import com.backendsyndicate.smashclub.ecommerce.repo.ProductVariantRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class AdminProductVariantService {
    @Autowired
    private ProductRepo productRepo;
    @Autowired
    private ProductVariantRepo productVariantRepo;
    @Autowired
    private LogService logService;

    public boolean save(long id, List<ProductVariant> variants) {
        try {
            Optional<Product> opt = productRepo.findById(id);
            if( opt.isEmpty() ) {
                Logging.handleException("AdminProductVariantService", "save(long id, List<ProductVariant> variants)", 34, AdminConstant.ADMIN_PRODUCT_VARIANT_SERVICE_SAVE_PRODUCT_NOT_FOUND, "Product not found!");
                return false;
            }

            Product product = opt.get();
            List<ProductVariant> variantDBs = product.getProductVariants();
            Map<Long, ProductVariant> variantMappedById = new HashMap<>();
            for( ProductVariant variant: variants ) {
                Logging.printConsole("Variant ID: " + variant.getId());
                Logging.printConsole("Is new: " + (variant.getId() == 0));
                if( variant.getId() == 0 ) {
                    variant.setId(null);
                    variant.setProduct(product);
                    productVariantRepo.save(variant);
                }

                variantMappedById.put(variant.getId(), variant);
            }

            for( ProductVariant existingVariant: variantDBs ) {
                if( variantMappedById.containsKey(existingVariant.getId()) ) {
                    Logging.printConsole("Updating variant with ID: " + existingVariant.getId());
                    existingVariant.setSku(variantMappedById.get(existingVariant.getId()).getSku());
                    existingVariant.setVariantName(variantMappedById.get(existingVariant.getId()).getVariantName());
                    existingVariant.setPrice(variantMappedById.get(existingVariant.getId()).getPrice());
                    existingVariant.setStock(variantMappedById.get(existingVariant.getId()).getStock());
                    if(variantMappedById.get(existingVariant.getId()).getVariantImgLink() != null) existingVariant.setVariantImgLink(variantMappedById.get(existingVariant.getId()).getVariantImgLink());
                } else {
                    Logging.printConsole("Deleting variant with ID: " + existingVariant.getId());
                    productVariantRepo.deleteById(existingVariant.getId());
                }
            }
        } catch(Exception e) {
            Logging.handleException("AdminProductVariantService", "save(long id, List<ProductVariant> variants)", 32, AdminConstant.ADMIN_PRODUCT_VARIANT_SERVICE_SAVE_EXCEPTION, e.getMessage());
            logService.writeErrorLog(AdminConstant.ADMIN_PRODUCT_VARIANT_SERVICE_SAVE_EXCEPTION, "AdminProductVariantService@save()", e.getMessage());
            return false;
        }

        return true;
    }

    public boolean deleteByProductId(Long id) {
        try {
            productVariantRepo.deleteByProduct_Id(id);
        } catch(Exception e) {
            Logging.handleException("AdminProductVariantService", "update(Long id, Product product, HttpServletRequest request)", 120, AdminConstant.ADMIN_PRODUCT_VARIANT_SERVICE_DELETE_EXCEPTION, e.getMessage());
            logService.writeErrorLog(AdminConstant.ADMIN_PRODUCT_VARIANT_SERVICE_DELETE_EXCEPTION, "AdminProductVariantService@save()", e.getMessage());
            return false;
        }

        return true;
    }
}
