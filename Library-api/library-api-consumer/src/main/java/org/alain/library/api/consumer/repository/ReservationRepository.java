package org.alain.library.api.consumer.repository;

import org.alain.library.api.model.reservation.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByCurrentStatusAndUserIdAndBookId(String status, Long bookId, Long userId);
}
