package com.nexus.core.serviceorder;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ServiceOrderRepository extends JpaRepository<ServiceOrderModel, Long> {
    List<ServiceOrderModel> findByActiveTrueOrderByCreatedAtDesc();
    List<ServiceOrderModel> findByCustomerIdAndActiveTrueOrderByCreatedAtDesc(Long customerId);
    List<ServiceOrderModel> findByStatusAndActiveTrueOrderByCreatedAtDesc(ServiceOrderStatus status);
    Optional<ServiceOrderModel> findByIdAndActiveTrue(Long id);
}
