package com.backendsyndicate.smashclub.admin.service.master;

import com.backendsyndicate.smashclub.common.util.GlobalResponse;
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

    private String generateErrorCode(String methodNo, String errorNo) {
        return "ADM-PRDVAR" + "-" + methodNo + "-" + errorNo;
    }

    public boolean save(long id, List<ProductVariant> variants) {
        try {
            Optional<Product> opt = productRepo.findById(id);
            if( opt.isEmpty() ) {
                Logging.handleException("AdminProductVariantService", "save(long id, List<ProductVariant> variants)", 34, generateErrorCode("01", "001"), "Product not found!");
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
                    existingVariant.setName(variantMappedById.get(existingVariant.getId()).getName());
                    existingVariant.setPrice(variantMappedById.get(existingVariant.getId()).getPrice());
                    existingVariant.setStock(variantMappedById.get(existingVariant.getId()).getStock());
                    if(variantMappedById.get(existingVariant.getId()).getVariantImgLink() != null) existingVariant.setVariantImgLink(variantMappedById.get(existingVariant.getId()).getVariantImgLink());
                } else {
                    Logging.printConsole("Deleting variant with ID: " + existingVariant.getId());
                    productVariantRepo.deleteById(existingVariant.getId());
                }
            }

//            for( ProductVariant variant: variants ) {
//                if( variant.getId() != 0 ) {
//                    Optional<ProductVariant> opt = productVariantRepo.findById(variant.getId());
//                    if( opt.isEmpty() ) {
//                        return false;
//                    }
//
//                    ProductVariant variantDB = opt.get();
//                    variantDB.setName(variant.getName());
//                    variantDB.setSku(variant.getSku());
//                    variantDB.setPrice(variant.getPrice());
//                    variantDB.setStock(variant.getStock());
//                    if( variant.getVariantImgLink() != null ) variantDB.setVariantImgLink(variant.getVariantImgLink());
//                } else {
//                    productVariantRepo.save(variant);
//                }
//            }
        } catch(Exception e) {
            Logging.handleException("AdminProductVariantService", "save(long id, List<ProductVariant> variants)", 32, generateErrorCode("01", "010"), e.getMessage());
            return false;
        }

        return true;
    }

    public boolean deleteByProductId(Long id) {
        try {
            productVariantRepo.deleteByProduct_Id(id);
        } catch(Exception e) {
            Logging.handleException("AdminProductVariantService", "update(Long id, Product product, HttpServletRequest request)", 120, generateErrorCode("02", "010"), "Failed to save variants!");
            return false;
        }

        return true;
    }
}
