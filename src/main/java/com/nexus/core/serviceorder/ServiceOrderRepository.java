package com.nexus.core.serviceorder;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceOrderRepository extends JpaRepository<ServiceOrderModel, Long> {
    List<ServiceOrderModel> findByOrderByCreatedAtDesc();
    List<ServiceOrderModel> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
    List<ServiceOrderModel> findByStatusOrderByCreatedAtDesc(ServiceOrderStatus status);
}
