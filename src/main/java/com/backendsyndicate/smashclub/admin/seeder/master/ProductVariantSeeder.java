package com.backendsyndicate.smashclub.admin.seeder.master;

import com.backendsyndicate.smashclub.admin.core.DataSeeder;
import com.backendsyndicate.smashclub.common.constant.AdminConstant;
import com.backendsyndicate.smashclub.common.util.Logging;
import com.backendsyndicate.smashclub.ecommerce.model.Product;
import com.backendsyndicate.smashclub.ecommerce.model.ProductVariant;
import com.backendsyndicate.smashclub.ecommerce.repo.ProductVariantRepo;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
@Transactional
@Profile({"dev", "staging"})
public class ProductVariantSeeder implements DataSeeder {
    private ProductVariantRepo productVariantRepo;
    private ProductSeeder productSeeder;

    public ProductVariantSeeder(ProductVariantRepo productVariantRepo, ProductSeeder productSeeder) {
        this.productVariantRepo = productVariantRepo;
        this.productSeeder = productSeeder;
    }

    @Override
    public void seed() {
        Logging.printConsole("Seeding product variant data...");
        initProductVariant();
    }

    public void initProductVariant() {
        generateProductVariantItem("INDM-001", "Indomie Goreng", 25, BigDecimal.valueOf(15000), AdminConstant.PRODUCT_IMG_INDOMIE, productSeeder.getProducts().get(AdminConstant.PRODUCT_INDOMIE));
        generateProductVariantItem("INDM-002", "Indomie Rasa Kari Ayam", 36, BigDecimal.valueOf(13000), AdminConstant.PRODUCT_IMG_INDOMIE_VAR_1, productSeeder.getProducts().get(AdminConstant.PRODUCT_INDOMIE));
        generateProductVariantItem("INDM-003", "Indomie Rasa Ayam Bawang", 40, BigDecimal.valueOf(13000), AdminConstant.PRODUCT_IMG_INDOMIE, productSeeder.getProducts().get(AdminConstant.PRODUCT_INDOMIE));
        generateProductVariantItem("INDM-004", "Indomie Rasa Soto", 21, BigDecimal.valueOf(13000), AdminConstant.PRODUCT_IMG_INDOMIE_VAR_1, productSeeder.getProducts().get(AdminConstant.PRODUCT_INDOMIE));

        generateProductVariantItem("AQFN-001", "Aquafina 330ML", 100, BigDecimal.valueOf(3000), AdminConstant.PRODUCT_IMG_AQUAFINA, productSeeder.getProducts().get(AdminConstant.PRODUCT_AQUAFINA));
        generateProductVariantItem("AQFN-002", "Aquafina 600ML", 120, BigDecimal.valueOf(6000), AdminConstant.PRODUCT_IMG_AQUAFINA_VAR_1, productSeeder.getProducts().get(AdminConstant.PRODUCT_AQUAFINA));

        generateProductVariantItem("RDBL-001", "Red Bull Original", 54, BigDecimal.valueOf(10000), AdminConstant.PRODUCT_IMG_REDBULL, productSeeder.getProducts().get(AdminConstant.PRODUCT_REDBULL));
        generateProductVariantItem("RDBL-002", "Red Bull Original", 54, BigDecimal.valueOf(10000), AdminConstant.PRODUCT_IMG_REDBULL_VAR_1, productSeeder.getProducts().get(AdminConstant.PRODUCT_REDBULL));

        generateProductVariantItem("KCRI-001", "Original", 55, BigDecimal.valueOf(25000), AdminConstant.PRODUCT_IMG_KATSURICE, productSeeder.getProducts().get(AdminConstant.PRODUCT_KATSURICE));
        generateProductVariantItem("KCRI-002", "Completed Package", 55, BigDecimal.valueOf(30000), AdminConstant.PRODUCT_IMG_KATSURICE_VAR_1, productSeeder.getProducts().get(AdminConstant.PRODUCT_KATSURICE));
    }

    private void generateProductVariantItem(String sku, String name, int stock, BigDecimal price, String variantImgLink, Product product) {
        ProductVariant productVariant = productVariantRepo.findBySku(sku).orElseGet( () -> {
            ProductVariant x = new ProductVariant();
            x.setSku(sku);
            x.setVariantName(name);
            x.setStock(stock);
            x.setPrice(price);
            x.setVariantImgLink(variantImgLink);
            x.setProduct(product);

            return productVariantRepo.save(x);
        } );
    }
}
