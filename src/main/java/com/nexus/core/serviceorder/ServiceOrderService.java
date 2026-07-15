package com.nexus.core.serviceorder;

import com.nexus.core.appointment.AppointmentStatus;
import com.nexus.core.customer.CustomerModel;
import com.nexus.core.customer.CustomerRepository;
import com.nexus.core.exception.*;
import com.nexus.core.serviceorder.dto.ServiceOrderRequestDTO;
import com.nexus.core.serviceorder.dto.ServiceOrderResponseDTO;
import com.nexus.core.serviceorder.dto.ServiceOrderUpdateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
        return serviceOrderRepository.findByActiveTrueOrderByCreatedAtDesc()
                .stream()
                .map(ServiceOrderResponseDTO::new)
                .toList();
    }

    public List<ServiceOrderResponseDTO> listByCustomer(Long customerId) {
        return serviceOrderRepository.findByCustomerIdAndActiveTrueOrderByCreatedAtDesc(customerId)
                .stream()
                .map(ServiceOrderResponseDTO::new)
                .toList();
    }

    public List<ServiceOrderResponseDTO> listByStatus(ServiceOrderStatus status) {
        return serviceOrderRepository.findByStatusAndActiveTrueOrderByCreatedAtDesc(status)
                .stream()
                .map(ServiceOrderResponseDTO::new)
                .toList();
    }

    public ServiceOrderResponseDTO findById(Long id) {
        return new ServiceOrderResponseDTO(findOrderById(id));
    }

    public ServiceOrderResponseDTO update(Long id, ServiceOrderUpdateDTO dto, boolean isAdmin) {
        ServiceOrderModel order = findOrderById(id);

        if (dto.description() != null) order.setDescription(dto.description());
        if (dto.totalValue() != null) order.setTotalValue(dto.totalValue());
        if (dto.notes() != null) order.setNotes(dto.notes());

        if (dto.status() != null) {

            ServiceOrderStatus currentStatus = order.getStatus();
            ServiceOrderStatus newStatus = dto.status();

            if(newStatus != currentStatus){
                boolean leavingTerminalState = currentStatus == ServiceOrderStatus.FINALIZADO
                        || currentStatus == ServiceOrderStatus.CANCELADO;
                if (leavingTerminalState && !isAdmin){
                    throw new ForbiddenStatusTransitionException(currentStatus, newStatus);
                }
            }

            if(newStatus == ServiceOrderStatus.FINALIZADO){
                BigDecimal effectiveValue = order.getTotalValue();
                if (effectiveValue == null || effectiveValue.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new InvalidFinalizationException();
                }
                order.setCompletedAt(LocalDateTime.now());
            }else if (currentStatus == ServiceOrderStatus.FINALIZADO){
                order.setCompletedAt(null);
            }
            order.setStatus(newStatus);
        }
        return new ServiceOrderResponseDTO(serviceOrderRepository.save(order));
    }

    public void deactivate(Long id, boolean isAdmin) {
        ServiceOrderModel order = findServiceOrder(id);
        if (!isAdmin) {
            throw new ForbiddenStatusTransitionException(order.getStatus(), AppointmentStatus.CANCELADO);
        }

        boolean isTerminal = order.getStatus() == ServiceOrderStatus.FINALIZADO
                || order.getStatus() == ServiceOrderStatus.CANCELADO;

        if (!isTerminal) {
            throw new NonTerminalOrderDeletionException(order.getStatus());
        }

        if (Boolean.FALSE.equals(order.getActive())) {
            return;
        }

        order.setActive(false);
        serviceOrderRepository.save(order);
    }

    private ServiceOrderModel findOrderById(Long id) {
        return serviceOrderRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ServiceOrderNotFoundException(id));
    }

    private ServiceOrderModel findServiceOrder(Long id) {
        return serviceOrderRepository.findById(id)
                .orElseThrow(() -> new ServiceOrderNotFoundException(id));
    }

}
