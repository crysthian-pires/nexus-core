package com.nexus.core.product;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<ProductModel, Long> {
    List<ProductModel> findByActiveTrue();
    List<ProductModel> findByCategoryAndActiveTrue(String category);
}
