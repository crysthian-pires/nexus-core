package com.nexus.core.product;

import com.nexus.core.exception.ProductNotFoundException;
import com.nexus.core.product.dto.ProductRequestDTO;
import com.nexus.core.product.dto.ProductResponseDTO;
import com.nexus.core.product.dto.ProductUpdateDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private ProductModel product;

    @BeforeEach
    void setUp() {
        product = new ProductModel();
        product.setId(1L);
        product.setName("Para-brisa Civic 2020");
        product.setDescription("Para-brisa dianteiro Honda Civic 2020");
        product.setPrice(new BigDecimal("850.00"));
        product.setQuantity(5);
        product.setCategory("Para-brisa");
        product.setActive(true);
    }

    @Test
    @DisplayName("Deve criar produto com sucesso")
    void create_success() {
        ProductRequestDTO dto = new ProductRequestDTO(
                "Para-brisa Civic 2020",
                "Para-brisa dianteiro Honda Civic 2020",
                new BigDecimal("850.00"),
                5,
                "Para-brisa"
        );

        when(productRepository.save(any(ProductModel.class))).thenReturn(product);

        ProductResponseDTO response = productService.create(dto);

        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo("Para-brisa Civic 2020");
        assertThat(response.price()).isEqualByComparingTo("850.00");
        verify(productRepository).save(any(ProductModel.class));
    }

    @Test
    @DisplayName("Deve listar todos os produtos ativos")
    void listAll_success() {
        when(productRepository.findByActiveTrue()).thenReturn(List.of(product));

        var response = productService.listAll();

        assertThat(response).hasSize(1);
        assertThat(response.get(0).name()).isEqualTo("Para-brisa Civic 2020");
    }

    @Test
    @DisplayName("Deve buscar produto por ID com sucesso")
    void findById_success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductResponseDTO response = productService.findById(1L);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar produto inexistente")
    void findById_notFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.findById(999L))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    @DisplayName("Deve atualizar produto com sucesso")
    void update_success() {
        ProductUpdateDTO dto = new ProductUpdateDTO(null, null, new BigDecimal("800.00"), 10, null);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(ProductModel.class))).thenReturn(product);

        ProductResponseDTO response = productService.update(1L, dto);

        assertThat(response).isNotNull();
        verify(productRepository).save(any(ProductModel.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar produto inexistente")
    void update_notFound() {
        ProductUpdateDTO dto = new ProductUpdateDTO(null, null, null, 10, null);

        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.update(999L, dto))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    @DisplayName("Deve desativar produto com sucesso")
    void deactivate_success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.deactivate(1L);

        assertThat(product.isActive()).isFalse();
        verify(productRepository).save(product);
    }

    @Test
    @DisplayName("Deve lançar exceção ao desativar produto inexistente")
    void deactivate_notFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.deactivate(999L))
                .isInstanceOf(ProductNotFoundException.class);
    }
}
