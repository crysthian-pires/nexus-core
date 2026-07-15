package com.nexus.core.product;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<ProductModel, Long> {
    List<ProductModel> findByActiveTrue();
    List<ProductModel> findByCategoryAndActiveTrue(String category);
    boolean existsByNameIgnoreCaseAndActiveTrue(String name);
    boolean existsByNameIgnoreCaseAndActiveTrueAndIdNot(String name, Long id);
    Optional<ProductModel> findByIdAndActiveTrue(Long id);
}
