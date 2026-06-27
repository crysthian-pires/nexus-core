package com.nexus.core.serviceorder;

import com.nexus.core.customer.CustomerModel;
import com.nexus.core.customer.CustomerRepository;
import com.nexus.core.exception.CustomerNotFoundException;
import com.nexus.core.exception.ServiceOrderNotFoundException;
import com.nexus.core.serviceorder.dto.ServiceOrderRequestDTO;
import com.nexus.core.serviceorder.dto.ServiceOrderResponseDTO;
import com.nexus.core.serviceorder.dto.ServiceOrderUpdateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceOrderService {

    private final ServiceOrderRepository serviceOrderRepository;
    private final CustomerRepository customerRepository;

    public ServiceOrderResponseDTO create(ServiceOrderRequestDTO dto) {
        CustomerModel customer = customerRepository.findById(dto.customerId())
                .orElseThrow(() -> new CustomerNotFoundException(dto.customerId()));

        ServiceOrderModel order = new ServiceOrderModel();
        order.setCustomer(customer);
        order.setDescription(dto.description());
        order.setTotalValue(dto.totalValue());
        order.setNotes(dto.notes());

        return new ServiceOrderResponseDTO(serviceOrderRepository.save(order));
    }

    public List<ServiceOrderResponseDTO> listAll() {
        return serviceOrderRepository.findByOrderByCreatedAtDesc()
                .stream()
                .map(ServiceOrderResponseDTO::new)
                .toList();
    }

    public List<ServiceOrderResponseDTO> listByCustomer(Long customerId) {
        return serviceOrderRepository.findByCustomerIdOrderByCreatedAtDesc(customerId)
                .stream()
                .map(ServiceOrderResponseDTO::new)
                .toList();
    }

    public List<ServiceOrderResponseDTO> listByStatus(ServiceOrderStatus status) {
        return serviceOrderRepository.findByStatusOrderByCreatedAtDesc(status)
                .stream()
                .map(ServiceOrderResponseDTO::new)
                .toList();
    }

    public ServiceOrderResponseDTO findById(Long id) {
        return new ServiceOrderResponseDTO(findOrderById(id));
    }

    public ServiceOrderResponseDTO update(Long id, ServiceOrderUpdateDTO dto) {
        ServiceOrderModel order = findOrderById(id);

        if (dto.description() != null) order.setDescription(dto.description());
        if (dto.totalValue() != null) order.setTotalValue(dto.totalValue());
        if (dto.notes() != null) order.setNotes(dto.notes());

        if (dto.status() != null) {
            order.setStatus(dto.status());
            if (dto.status() == ServiceOrderStatus.FINALIZADO) {
                order.setCompletedAt(LocalDateTime.now());
            }
        }

        return new ServiceOrderResponseDTO(serviceOrderRepository.save(order));
    }

    private ServiceOrderModel findOrderById(Long id) {
        return serviceOrderRepository.findById(id)
                .orElseThrow(() -> new ServiceOrderNotFoundException(id));
    }
}
