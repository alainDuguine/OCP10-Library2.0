package org.alain.library.api.consumer.repository;

import org.alain.library.api.model.reservation.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationStatusRepository extends JpaRepository<ReservationStatus, Long> {
}
