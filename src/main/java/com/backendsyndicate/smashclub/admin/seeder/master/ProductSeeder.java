package com.backendsyndicate.smashclub.admin.seeder.master;

import com.backendsyndicate.smashclub.admin.core.DataSeeder;
import com.backendsyndicate.smashclub.common.util.Logging;
import com.backendsyndicate.smashclub.ecommerce.model.Product;
import com.backendsyndicate.smashclub.ecommerce.repo.ProductRepo;
import com.backendsyndicate.smashclub.common.constant.AdminConstant;
import com.backendsyndicate.smashclub.common.constant.CommonConstant;
import jakarta.persistence.Column;
import lombok.Getter;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Profile("dev")
@Component
@Transactional
public class ProductSeeder implements DataSeeder {
    private ProductRepo productRepo;
    @Getter
    private final Map<Long, Product> products = new HashMap<Long, Product>();

    public ProductSeeder(ProductRepo productRepo) {
        this.productRepo = productRepo;
    }

    @Override
    public void seed() {
        Logging.printConsole("Seeding product data...");
        initProduct();
    }

    public void initProduct() {
        generateProductItem(AdminConstant.PRODUCT_INDOMIE, "Indomie", "Mie instan dengan beberapa varian rasa.", "Makanan", (byte) CommonConstant.STATUS_ACTIVE, AdminConstant.PRODUCT_IMG_INDOMIE);
        generateProductItem(AdminConstant.PRODUCT_AQUAFINA, "Aquafina", "Aquafina Air Mineral. Tersedia ukuran 330ml dan 600ml.", "Minuman", (byte) CommonConstant.STATUS_ACTIVE, AdminConstant.PRODUCT_IMG_AQUAFINA);
        generateProductItem(AdminConstant.PRODUCT_REDBULL, "Red Bull", "Untuk meningkatkan performa anda di lapangan.", "Minuman", (byte) CommonConstant.STATUS_ACTIVE, AdminConstant.PRODUCT_IMG_REDBULL);
        generateProductItem(AdminConstant.PRODUCT_KATSURICE, "Katsu Chicken Rice", "Nasi dengan ayam katsu. Extra charge jika tambah telur.", "Makanan", (byte) CommonConstant.STATUS_ACTIVE, AdminConstant.PRODUCT_IMG_KATSURICE);
    }

    private void generateProductItem(Long id, String productName, String productDesc, String category, byte status, String defaultImgLink) {
        Product product = productRepo.findById(id).orElseGet( () -> {
            Product x = new Product();
//            x.setId(id);
            x.setProductName(productName);
            x.setProductDesc(productDesc);
            x.setCategory(category);
            x.setStatus(status);
            x.setDefaultImgLink(defaultImgLink);

            return productRepo.save(x);
        } );

        products.put(id, product);
    }
}
