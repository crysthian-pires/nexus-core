package com.nexus.core.customer;

import com.nexus.core.exception.CustomerNotFoundException;
import com.nexus.core.customer.dto.CustomerRequestDTO;
import com.nexus.core.customer.dto.CustomerResponseDTO;
import com.nexus.core.customer.dto.CustomerUpdateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerResponseDTO create(CustomerRequestDTO dto) {
        CustomerModel customer = new CustomerModel();
        customer.setName(dto.name());
        customer.setEmail(dto.email());
        customer.setPhone(dto.phone());
        customer.setDocument(dto.document());
        customer.setNotes(dto.notes());
        return new CustomerResponseDTO(customerRepository.save(customer));
    }

    public List<CustomerResponseDTO> listAll() {
        return customerRepository.findByActiveTrue()
                .stream()
                .map(CustomerResponseDTO::new)
                .toList();
    }

    public List<CustomerResponseDTO> search(String name) {
        return customerRepository.findByNameContainingIgnoreCaseAndActiveTrue(name)
                .stream()
                .map(CustomerResponseDTO::new)
                .toList();
    }

    public CustomerResponseDTO findById(Long id) {
        return new CustomerResponseDTO(findCustomerById(id));
    }

    public CustomerResponseDTO update(Long id, CustomerUpdateDTO dto) {
        CustomerModel customer = findCustomerById(id);
        if (dto.name() != null) customer.setName(dto.name());
        if (dto.email() != null) customer.setEmail(dto.email());
        if (dto.phone() != null) customer.setPhone(dto.phone());
        if (dto.document() != null) customer.setDocument(dto.document());
        if (dto.notes() != null) customer.setNotes(dto.notes());
        return new CustomerResponseDTO(customerRepository.save(customer));
    }

    public void deactivate(Long id) {
        CustomerModel customer = findCustomerById(id);
        customer.setActive(false);
        customerRepository.save(customer);
    }

    private CustomerModel findCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
    }
}
