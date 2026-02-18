package com.backendsyndicate.smashclub.ecommerce.repo;

import com.backendsyndicate.smashclub.ecommerce.model.Product;
import com.backendsyndicate.smashclub.ecommerce.model.ProductVariant;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductVariantRepo extends JpaRepository<ProductVariant, Long> {
    Optional<ProductVariant> findBySku(String sku);

    Optional<ProductVariant> findById(Long variantId);

    void deleteByProduct_Id(Long productId);

    @Query("SELECT pv FROM ProductVariant pv WHERE pv.id = :variantId " +
            "AND pv.stock >= :quantity")
    Optional<ProductVariant> findByIdAndSufficientStock(Long variantId, Integer quantity);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.id = :variantId")
    Optional<Product> findByIdForUpdate(@Param("variantId") Long variantId);
}
