package com.backendsyndicate.smashclub.ecommerce.repo;

import com.backendsyndicate.smashclub.ecommerce.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepo extends JpaRepository<Product, Long> {
    Page<Product> findAllByProductNameContainsOrCategoryContains(String productName, String category, Pageable pageable);
}
