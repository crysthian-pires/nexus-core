package com.nexus.core.appointment;

import com.nexus.core.customer.CustomerModel;
import com.nexus.core.customer.CustomerRepository;
import com.nexus.core.exception.AppointmentNotFoundException;
import com.nexus.core.exception.CustomerNotFoundException;
import com.nexus.core.appointment.dto.AppointmentRequestDTO;
import com.nexus.core.appointment.dto.AppointmentResponseDTO;
import com.nexus.core.appointment.dto.AppointmentUpdateDTO;
import com.nexus.core.exception.ForbiddenStatusTransitionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    private CustomerModel customer;
    private AppointmentModel appointment;

    @BeforeEach
    void setUp() {
        customer = new CustomerModel();
        customer.setId(1L);
        customer.setName("João Silva");
        customer.setActive(true);

        appointment = new AppointmentModel();
        appointment.setId(1L);
        appointment.setCustomer(customer);
        appointment.setDescription("Troca de para-brisa");
        appointment.setScheduledAt(LocalDateTime.now().plusDays(1));
        appointment.setStatus(AppointmentStatus.AGENDADO);
        appointment.setEstimatedValue(new BigDecimal("850.00"));
    }

    @Test
    @DisplayName("Deve criar agendamento com sucesso")
    void create_success() {
        AppointmentRequestDTO dto = new AppointmentRequestDTO(
                1L,
                "Troca de para-brisa",
                LocalDateTime.now().plusDays(1),
                new BigDecimal("850.00"),
                null
        );

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(appointmentRepository.save(any(AppointmentModel.class))).thenReturn(appointment);

        AppointmentResponseDTO response = appointmentService.create(dto);

        assertThat(response).isNotNull();
        assertThat(response.description()).isEqualTo("Troca de para-brisa");
        assertThat(response.customerName()).isEqualTo("João Silva");
        verify(appointmentRepository).save(any(AppointmentModel.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar agendamento com cliente inexistente")
    void create_customerNotFound() {
        AppointmentRequestDTO dto = new AppointmentRequestDTO(
                999L, "Troca de para-brisa", LocalDateTime.now().plusDays(1), null, null
        );

        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.create(dto))
                .isInstanceOf(CustomerNotFoundException.class);
    }

    @Test
    @DisplayName("Deve listar todos os agendamentos")
    void listAll_success() {
        when(appointmentRepository.findByOrderByScheduledAtAsc()).thenReturn(List.of(appointment));

        var response = appointmentService.listAll();

        assertThat(response).hasSize(1);
        assertThat(response.get(0).description()).isEqualTo("Troca de para-brisa");
    }

    @Test
    @DisplayName("Deve listar agendamentos por cliente")
    void listByCustomer_success() {
        when(appointmentRepository.findByCustomerIdOrderByScheduledAtAsc(1L))
                .thenReturn(List.of(appointment));

        var response = appointmentService.listByCustomer(1L);

        assertThat(response).hasSize(1);
        assertThat(response.get(0).customerId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Deve listar agendamentos por status")
    void listByStatus_success() {
        when(appointmentRepository.findByStatusOrderByScheduledAtAsc(AppointmentStatus.AGENDADO))
                .thenReturn(List.of(appointment));

        var response = appointmentService.listByStatus(AppointmentStatus.AGENDADO);

        assertThat(response).hasSize(1);
        assertThat(response.get(0).status()).isEqualTo(AppointmentStatus.AGENDADO);
    }

    @Test
    @DisplayName("Deve buscar agendamento por ID com sucesso")
    void findById_success() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        AppointmentResponseDTO response = appointmentService.findById(1L);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar agendamento inexistente")
    void findById_notFound() {
        when(appointmentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.findById(999L))
                .isInstanceOf(AppointmentNotFoundException.class);
    }

    @Test
    @DisplayName("Deve atualizar agendamento com sucesso")
    void update_success() {
        AppointmentUpdateDTO dto = new AppointmentUpdateDTO(
                null, null, AppointmentStatus.CONFIRMADO, null, null
        );

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(AppointmentModel.class))).thenReturn(appointment);

        AppointmentResponseDTO response = appointmentService.update(1L, dto, true);

        assertThat(response).isNotNull();
        verify(appointmentRepository).save(any(AppointmentModel.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar agendamento inexistente")
    void update_notFound() {
        AppointmentUpdateDTO dto = new AppointmentUpdateDTO(null, null, null, null, null);

        when(appointmentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.update(999L, dto, true))
                .isInstanceOf(AppointmentNotFoundException.class);
    }

    @Test
    @DisplayName("Deve cancelar agendamento com sucesso")
    void cancel_success() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        appointmentService.cancel(1L, true);

        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.CANCELADO);
        verify(appointmentRepository).save(appointment);
    }

    @Test
    @DisplayName("Deve lançar exceção ao cancelar agendamento inexistente")
    void cancel_notFound() {
        when(appointmentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.cancel(999L, true))
                .isInstanceOf(AppointmentNotFoundException.class);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar sair de estado terminal sem ser ADMIN")
    void update_leavingTerminalStateWithoutAdmin_throwsException() {
        appointment.setStatus(AppointmentStatus.CONCLUIDO);

        AppointmentUpdateDTO dto = new AppointmentUpdateDTO(
                null, null, AppointmentStatus.AGENDADO, null, null
        );

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> appointmentService.update(1L, dto, false))
                .isInstanceOf(ForbiddenStatusTransitionException.class);

        verify(appointmentRepository, never()).save(any());
    }

    @Test
    @DisplayName("ADMIN deve conseguir sair de estado terminal normalmente")
    void update_leavingTerminalStateAsAdmin_succeeds() {
        appointment.setStatus(AppointmentStatus.CONCLUIDO);

        AppointmentUpdateDTO dto = new AppointmentUpdateDTO(
                null, null, AppointmentStatus.AGENDADO, null, null
        );

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(AppointmentModel.class))).thenReturn(appointment);

        AppointmentResponseDTO response = appointmentService.update(1L, dto, true);

        assertThat(response).isNotNull();
        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.AGENDADO);
        verify(appointmentRepository).save(any(AppointmentModel.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao cancelar agendamento já CONCLUIDO sem ser ADMIN")
    void cancel_alreadyConcluded_withoutAdmin_throwsException() {
        appointment.setStatus(AppointmentStatus.CONCLUIDO);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> appointmentService.cancel(1L, false))
                .isInstanceOf(ForbiddenStatusTransitionException.class);

        verify(appointmentRepository, never()).save(any());
    }
}
