package com.nexus.core.appointment;

import com.nexus.core.customer.CustomerModel;
import com.nexus.core.customer.CustomerRepository;
import com.nexus.core.exception.AppointmentNotFoundException;
import com.nexus.core.exception.CustomerNotFoundException;
import com.nexus.core.appointment.dto.AppointmentRequestDTO;
import com.nexus.core.appointment.dto.AppointmentResponseDTO;
import com.nexus.core.appointment.dto.AppointmentUpdateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final CustomerRepository customerRepository;

    public AppointmentResponseDTO create(AppointmentRequestDTO dto) {
        CustomerModel customer = customerRepository.findById(dto.customerId())
                .orElseThrow(() -> new CustomerNotFoundException(dto.customerId()));

        AppointmentModel appointment = new AppointmentModel();
        appointment.setCustomer(customer);
        appointment.setDescription(dto.description());
        appointment.setScheduledAt(dto.scheduledAt());
        appointment.setEstimatedValue(dto.estimatedValue());
        appointment.setNotes(dto.notes());

        return new AppointmentResponseDTO(appointmentRepository.save(appointment));
    }

    public List<AppointmentResponseDTO> listAll() {
        return appointmentRepository.findByOrderByScheduledAtAsc()
                .stream()
                .map(AppointmentResponseDTO::new)
                .toList();
    }

    public List<AppointmentResponseDTO> listByCustomer(Long customerId) {
        return appointmentRepository.findByCustomerIdOrderByScheduledAtAsc(customerId)
                .stream()
                .map(AppointmentResponseDTO::new)
                .toList();
    }

    public List<AppointmentResponseDTO> listByStatus(AppointmentStatus status) {
        return appointmentRepository.findByStatusOrderByScheduledAtAsc(status)
                .stream()
                .map(AppointmentResponseDTO::new)
                .toList();
    }

    public AppointmentResponseDTO findById(Long id) {
        return new AppointmentResponseDTO(findAppointmentById(id));
    }

    public AppointmentResponseDTO update(Long id, AppointmentUpdateDTO dto) {
        AppointmentModel appointment = findAppointmentById(id);

        if (dto.description() != null) appointment.setDescription(dto.description());
        if (dto.scheduledAt() != null) appointment.setScheduledAt(dto.scheduledAt());
        if (dto.status() != null) appointment.setStatus(dto.status());
        if (dto.estimatedValue() != null) appointment.setEstimatedValue(dto.estimatedValue());
        if (dto.notes() != null) appointment.setNotes(dto.notes());

        return new AppointmentResponseDTO(appointmentRepository.save(appointment));
    }

    public void cancel(Long id) {
        AppointmentModel appointment = findAppointmentById(id);
        appointment.setStatus(AppointmentStatus.CANCELADO);
        appointmentRepository.save(appointment);
    }

    private AppointmentModel findAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException(id));
    }
}
