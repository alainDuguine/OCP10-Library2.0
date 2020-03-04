package org.alain.library.api.business.contract;

import org.alain.library.api.business.impl.UserPrincipal;
import org.alain.library.api.model.reservation.Reservation;

import java.util.List;
import java.util.Optional;

public interface ReservationManagement extends CrudManagement<Reservation> {
    List<Reservation> getReservationsByStatusAndUserIdAndBookId(String status, Long userId, Long bookId);
    Reservation createNewReservation(Long bookId, Long userId, UserPrincipal userPrincipal);
    Optional<Reservation> updateReservation(Long reservationId, String status, UserPrincipal userPrincipal);
    List<Reservation> updateAndGetExpiredReservation();
    List<Reservation> getReservationsByUser(Long id);
}
