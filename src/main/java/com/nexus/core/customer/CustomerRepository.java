package com.nexus.core.customer;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<CustomerModel, Long> {
    List<CustomerModel> findByActiveTrue();
    List<CustomerModel> findByNameContainingIgnoreCaseAndActiveTrue(String name);
    boolean existsByDocument(String document);
    boolean existsByDocumentAndIdNot(String document, Long id);
    Optional<CustomerModel> findByIdAndActiveTrue(Long id);
}
