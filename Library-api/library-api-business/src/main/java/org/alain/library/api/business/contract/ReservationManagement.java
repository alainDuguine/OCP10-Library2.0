package org.alain.library.api.business.contract;

import org.alain.library.api.model.reservation.Reservation;

import java.util.List;
import java.util.Optional;

public interface ReservationManagement {
    Optional<Reservation> getReservation(Long id);
    List<Reservation> getReservationsByStatusAndUserIdAndBookId(String status, Long userId, Long bookId);
}
