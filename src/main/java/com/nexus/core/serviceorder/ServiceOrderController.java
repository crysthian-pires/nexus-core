package com.nexus.core.serviceorder;

import com.nexus.core.serviceorder.dto.ServiceOrderRequestDTO;
import com.nexus.core.serviceorder.dto.ServiceOrderResponseDTO;
import com.nexus.core.serviceorder.dto.ServiceOrderUpdateDTO;
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
@RequestMapping("/service-orders")
@RequiredArgsConstructor
@Tag(name = "Ordens de Serviço", description = "Gerenciamento de ordens de serviço")
@SecurityRequirement(name = "Bearer Token")
public class ServiceOrderController {

    private final ServiceOrderService serviceOrderService;

    @Operation(summary = "Criar ordem de serviço")
    @PostMapping
    public ResponseEntity<ServiceOrderResponseDTO> create(
            @Valid @RequestBody ServiceOrderRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(serviceOrderService.create(dto));
    }

    @Operation(summary = "Listar ordens de serviço")
    @GetMapping
    public ResponseEntity<List<ServiceOrderResponseDTO>> listAll(
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) ServiceOrderStatus status) {
        if (customerId != null) {
            return ResponseEntity.ok(serviceOrderService.listByCustomer(customerId));
        }
        if (status != null) {
            return ResponseEntity.ok(serviceOrderService.listByStatus(status));
        }
        return ResponseEntity.ok(serviceOrderService.listAll());
    }

    @Operation(summary = "Buscar OS por ID")
    @GetMapping("/{id}")
    public ResponseEntity<ServiceOrderResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(serviceOrderService.findById(id));
    }

    @Operation(summary = "Atualizar OS")
    @PatchMapping("/{id}")
    public ResponseEntity<ServiceOrderResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody ServiceOrderUpdateDTO dto) {
        return ResponseEntity.ok(serviceOrderService.update(id, dto));
    }
}
