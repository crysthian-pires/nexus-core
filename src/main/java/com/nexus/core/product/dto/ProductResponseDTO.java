package com.nexus.core.product.dto;

import com.nexus.core.product.ProductModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponseDTO(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer quantity,
        String category,
        boolean active,
        LocalDateTime createdAt
) {
    public ProductResponseDTO(ProductModel product) {
        this(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity(),
                product.getCategory(),
                product.isActive(),
                product.getCreatedAt()
        );
    }
}
