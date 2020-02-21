package org.alain.library.api.business.impl;

import org.alain.library.api.business.contract.ReservationManagement;
import org.alain.library.api.model.reservation.Reservation;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReservationManagementImpl implements ReservationManagement {
    @Override
    public Optional<Reservation> getReservation(Long id) {
        return Optional.empty();
    }

    @Override
    public List<Reservation> getReservationsByStatusAndUserIdAndBookId(String status, Long userId, Long bookId) {
        return null;
    }
}
