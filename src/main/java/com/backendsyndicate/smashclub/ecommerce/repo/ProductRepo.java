package com.backendsyndicate.smashclub.ecommerce.repo;

import com.backendsyndicate.smashclub.ecommerce.model.Product;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepo extends JpaRepository<Product,Long> {
    @Query("""
        SELECT p FROM Product p
        WHERE p.status = 1""")
    Page<Product> findAllActiveProducts(Pageable pageable);

    Page<Product> findAllByProductNameContainsIgnoreCaseOrCategoryContainsIgnoreCase(Pageable pageable, String productName, String category, HttpServletRequest request);
    @Query("""
        SELECT p FROM Product p
        WHERE p.status = 1
        AND (
            LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(p.category) LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
    """)
    Page<Product> searchActiveProductsByCategoryOrName(@Param("keyword") String keyword, Pageable pageable);
}