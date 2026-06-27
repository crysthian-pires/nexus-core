package com.nexus.core.customer;

import com.nexus.core.customer.dto.CustomerRequestDTO;
import com.nexus.core.customer.dto.CustomerResponseDTO;
import com.nexus.core.customer.dto.CustomerUpdateDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "Gerenciamento de clientes")
@SecurityRequirement(name = "Bearer Token")
public class CustomerController {

    private final CustomerService customerService;

    @Operation(summary = "Criar cliente")
    @PostMapping
    public ResponseEntity<CustomerResponseDTO> create(
            @Valid @RequestBody CustomerRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(customerService.create(dto));
    }

    @Operation(summary = "Listar clientes ativos")
    @GetMapping
    public ResponseEntity<List<CustomerResponseDTO>> listAll(
            @RequestParam(required = false) String search) {
        if (search != null && !search.isBlank()) {
            return ResponseEntity.ok(customerService.search(search));
        }
        return ResponseEntity.ok(customerService.listAll());
    }

    @Operation(summary = "Buscar cliente por ID")
    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.findById(id));
    }

    @Operation(summary = "Atualizar cliente")
    @PatchMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody CustomerUpdateDTO dto) {
        return ResponseEntity.ok(customerService.update(id, dto));
    }

    @Operation(summary = "Desativar cliente")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        customerService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
