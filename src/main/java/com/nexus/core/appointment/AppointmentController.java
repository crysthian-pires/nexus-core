package com.nexus.core.appointment;

import com.nexus.core.appointment.dto.AppointmentRequestDTO;
import com.nexus.core.appointment.dto.AppointmentResponseDTO;
import com.nexus.core.appointment.dto.AppointmentUpdateDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
@Tag(name = "Agendamentos", description = "Gerenciamento de agendamentos")
@SecurityRequirement(name = "Bearer Token")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @Operation(summary = "Criar agendamento")
    @PostMapping
    public ResponseEntity<AppointmentResponseDTO> create(
            @Valid @RequestBody AppointmentRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(appointmentService.create(dto));
    }

    @Operation(summary = "Listar agendamentos")
    @GetMapping
    public ResponseEntity<List<AppointmentResponseDTO>> listAll(
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) AppointmentStatus status) {
        if (customerId != null) {
            return ResponseEntity.ok(appointmentService.listByCustomer(customerId));
        }
        if (status != null) {
            return ResponseEntity.ok(appointmentService.listByStatus(status));
        }
        return ResponseEntity.ok(appointmentService.listAll());
    }

    @Operation(summary = "Buscar agendamento por ID")
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.findById(id));
    }

    @Operation(summary = "Atualizar agendamento")
    @PatchMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> update( @PathVariable Long id, @Valid @RequestBody AppointmentUpdateDTO dto, Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return ResponseEntity.ok(appointmentService.update(id, dto, isAdmin));
    }

    @Operation(summary = "Cancelar agendamento")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(@PathVariable Long id, Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        appointmentService.cancel(id, isAdmin);
        return ResponseEntity.noContent().build();
    }
}
