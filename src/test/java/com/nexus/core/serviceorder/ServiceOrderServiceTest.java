package com.nexus.core.serviceorder;

import com.nexus.core.customer.CustomerModel;
import com.nexus.core.customer.CustomerRepository;
import com.nexus.core.exception.CustomerNotFoundException;
import com.nexus.core.exception.ServiceOrderNotFoundException;
import com.nexus.core.serviceorder.dto.ServiceOrderRequestDTO;
import com.nexus.core.serviceorder.dto.ServiceOrderResponseDTO;
import com.nexus.core.serviceorder.dto.ServiceOrderUpdateDTO;
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
class ServiceOrderServiceTest {

    @Mock
    private ServiceOrderRepository serviceOrderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private ServiceOrderService serviceOrderService;

    private CustomerModel customer;
    private ServiceOrderModel order;

    @BeforeEach
    void setUp() {
        customer = new CustomerModel();
        customer.setId(1L);
        customer.setName("João Silva");
        customer.setActive(true);

        order = new ServiceOrderModel();
        order.setId(1L);
        order.setCustomer(customer);
        order.setDescription("Troca de para-brisa");
        order.setStatus(ServiceOrderStatus.PENDENTE);
        order.setTotalValue(new BigDecimal("850.00"));
    }

    @Test
    @DisplayName("Deve criar OS com sucesso")
    void create_success() {
        ServiceOrderRequestDTO dto = new ServiceOrderRequestDTO(
                1L, "Troca de para-brisa", new BigDecimal("850.00"), null
        );

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(serviceOrderRepository.save(any(ServiceOrderModel.class))).thenReturn(order);

        ServiceOrderResponseDTO response = serviceOrderService.create(dto);

        assertThat(response).isNotNull();
        assertThat(response.description()).isEqualTo("Troca de para-brisa");
        assertThat(response.customerName()).isEqualTo("João Silva");
        verify(serviceOrderRepository).save(any(ServiceOrderModel.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar OS com cliente inexistente")
    void create_customerNotFound() {
        ServiceOrderRequestDTO dto = new ServiceOrderRequestDTO(
                999L, "Troca de para-brisa", null, null
        );

        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> serviceOrderService.create(dto))
                .isInstanceOf(CustomerNotFoundException.class);
    }

    @Test
    @DisplayName("Deve listar todas as OS")
    void listAll_success() {
        when(serviceOrderRepository.findByOrderByCreatedAtDesc()).thenReturn(List.of(order));

        var response = serviceOrderService.listAll();

        assertThat(response).hasSize(1);
        assertThat(response.get(0).description()).isEqualTo("Troca de para-brisa");
    }

    @Test
    @DisplayName("Deve listar OS por cliente")
    void listByCustomer_success() {
        when(serviceOrderRepository.findByCustomerIdOrderByCreatedAtDesc(1L))
                .thenReturn(List.of(order));

        var response = serviceOrderService.listByCustomer(1L);

        assertThat(response).hasSize(1);
        assertThat(response.get(0).customerId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Deve listar OS por status")
    void listByStatus_success() {
        when(serviceOrderRepository.findByStatusOrderByCreatedAtDesc(ServiceOrderStatus.PENDENTE))
                .thenReturn(List.of(order));

        var response = serviceOrderService.listByStatus(ServiceOrderStatus.PENDENTE);

        assertThat(response).hasSize(1);
        assertThat(response.get(0).status()).isEqualTo(ServiceOrderStatus.PENDENTE);
    }

    @Test
    @DisplayName("Deve buscar OS por ID com sucesso")
    void findById_success() {
        when(serviceOrderRepository.findById(1L)).thenReturn(Optional.of(order));

        ServiceOrderResponseDTO response = serviceOrderService.findById(1L);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar OS inexistente")
    void findById_notFound() {
        when(serviceOrderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> serviceOrderService.findById(999L))
                .isInstanceOf(ServiceOrderNotFoundException.class);
    }

    @Test
    @DisplayName("Deve atualizar status da OS com sucesso")
    void update_status_success() {
        ServiceOrderUpdateDTO dto = new ServiceOrderUpdateDTO(
                null, ServiceOrderStatus.EM_EXECUCAO, null, null
        );

        when(serviceOrderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(serviceOrderRepository.save(any(ServiceOrderModel.class))).thenReturn(order);

        ServiceOrderResponseDTO response = serviceOrderService.update(1L, dto);

        assertThat(response).isNotNull();
        verify(serviceOrderRepository).save(any(ServiceOrderModel.class));
    }

    @Test
    @DisplayName("Deve definir completedAt ao finalizar OS")
    void update_finalizado_setsCompletedAt() {
        ServiceOrderUpdateDTO dto = new ServiceOrderUpdateDTO(
                null, ServiceOrderStatus.FINALIZADO, null, null
        );

        when(serviceOrderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(serviceOrderRepository.save(any(ServiceOrderModel.class))).thenReturn(order);

        serviceOrderService.update(1L, dto);

        assertThat(order.getCompletedAt()).isNotNull();
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar OS inexistente")
    void update_notFound() {
        ServiceOrderUpdateDTO dto = new ServiceOrderUpdateDTO(null, null, null, null);

        when(serviceOrderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> serviceOrderService.update(999L, dto))
                .isInstanceOf(ServiceOrderNotFoundException.class);
    }
}
