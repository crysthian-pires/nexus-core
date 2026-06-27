package com.nexus.core.customer;

import com.nexus.core.exception.CustomerNotFoundException;
import com.nexus.core.customer.dto.CustomerRequestDTO;
import com.nexus.core.customer.dto.CustomerResponseDTO;
import com.nexus.core.customer.dto.CustomerUpdateDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private CustomerModel customer;

    @BeforeEach
    void setUp() {
        customer = new CustomerModel();
        customer.setId(1L);
        customer.setName("João Silva");
        customer.setEmail("joao@email.com");
        customer.setPhone("+5511999998888");
        customer.setDocument("123.456.789-00");
        customer.setActive(true);
    }

    @Test
    @DisplayName("Deve criar cliente com sucesso")
    void create_success() {
        CustomerRequestDTO dto = new CustomerRequestDTO(
                "João Silva", "joao@email.com", "+5511999998888", "123.456.789-00", null
        );

        when(customerRepository.save(any(CustomerModel.class))).thenReturn(customer);

        CustomerResponseDTO response = customerService.create(dto);

        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo("João Silva");
        verify(customerRepository).save(any(CustomerModel.class));
    }

    @Test
    @DisplayName("Deve listar todos os clientes ativos")
    void listAll_success() {
        when(customerRepository.findByActiveTrue()).thenReturn(List.of(customer));

        var response = customerService.listAll();

        assertThat(response).hasSize(1);
        assertThat(response.get(0).name()).isEqualTo("João Silva");
    }

    @Test
    @DisplayName("Deve buscar cliente por nome")
    void search_success() {
        when(customerRepository.findByNameContainingIgnoreCaseAndActiveTrue("João"))
                .thenReturn(List.of(customer));

        var response = customerService.search("João");

        assertThat(response).hasSize(1);
        assertThat(response.get(0).name()).isEqualTo("João Silva");
    }

    @Test
    @DisplayName("Deve buscar cliente por ID com sucesso")
    void findById_success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        CustomerResponseDTO response = customerService.findById(1L);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar cliente inexistente")
    void findById_notFound() {
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.findById(999L))
                .isInstanceOf(CustomerNotFoundException.class);
    }

    @Test
    @DisplayName("Deve atualizar cliente com sucesso")
    void update_success() {
        CustomerUpdateDTO dto = new CustomerUpdateDTO("João Atualizado", null, null, null, null);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(CustomerModel.class))).thenReturn(customer);

        CustomerResponseDTO response = customerService.update(1L, dto);

        assertThat(response).isNotNull();
        verify(customerRepository).save(any(CustomerModel.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar cliente inexistente")
    void update_notFound() {
        CustomerUpdateDTO dto = new CustomerUpdateDTO("João", null, null, null, null);

        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.update(999L, dto))
                .isInstanceOf(CustomerNotFoundException.class);
    }

    @Test
    @DisplayName("Deve desativar cliente com sucesso")
    void deactivate_success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        customerService.deactivate(1L);

        assertThat(customer.isActive()).isFalse();
        verify(customerRepository).save(customer);
    }

    @Test
    @DisplayName("Deve lançar exceção ao desativar cliente inexistente")
    void deactivate_notFound() {
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.deactivate(999L))
                .isInstanceOf(CustomerNotFoundException.class);
    }
}
