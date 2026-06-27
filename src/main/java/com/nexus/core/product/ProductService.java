package com.nexus.core.product;

import com.nexus.core.exception.ProductNotFoundException;
import com.nexus.core.product.dto.ProductRequestDTO;
import com.nexus.core.product.dto.ProductResponseDTO;
import com.nexus.core.product.dto.ProductUpdateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ProductResponseDTO create(ProductRequestDTO dto) {
        ProductModel product = new ProductModel();
        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setPrice(dto.price());
        product.setQuantity(dto.quantity());
        product.setCategory(dto.category());
        return new ProductResponseDTO(productRepository.save(product));
    }

    public List<ProductResponseDTO> listAll() {
        return productRepository.findByActiveTrue()
                .stream()
                .map(ProductResponseDTO::new)
                .toList();
    }

    public ProductResponseDTO findById(Long id) {
        return new ProductResponseDTO(findProductById(id));
    }

    public ProductResponseDTO update(Long id, ProductUpdateDTO dto) {
        ProductModel product = findProductById(id);
        if (dto.name() != null) product.setName(dto.name());
        if (dto.description() != null) product.setDescription(dto.description());
        if (dto.price() != null) product.setPrice(dto.price());
        if (dto.quantity() != null) product.setQuantity(dto.quantity());
        if (dto.category() != null) product.setCategory(dto.category());
        return new ProductResponseDTO(productRepository.save(product));
    }

    public void deactivate(Long id) {
        ProductModel product = findProductById(id);
        product.setActive(false);
        productRepository.save(product);
    }

    private ProductModel findProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }
}
