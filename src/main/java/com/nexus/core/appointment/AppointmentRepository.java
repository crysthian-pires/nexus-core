package com.nexus.core.appointment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<AppointmentModel, Long> {
    List<AppointmentModel> findByOrderByScheduledAtAsc();
    List<AppointmentModel> findByCustomerIdOrderByScheduledAtAsc(Long customerId);
    List<AppointmentModel> findByStatusOrderByScheduledAtAsc(AppointmentStatus status);
    List<AppointmentModel> findByScheduledAtBetweenOrderByScheduledAtAsc(
            LocalDateTime start, LocalDateTime end);
}
