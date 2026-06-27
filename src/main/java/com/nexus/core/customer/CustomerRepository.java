package com.nexus.core.customer;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerRepository extends JpaRepository<CustomerModel, Long> {
    List<CustomerModel> findByActiveTrue();
    List<CustomerModel> findByNameContainingIgnoreCaseAndActiveTrue(String name);
}
