package org.alain.library.api.mail.contract;

import org.alain.library.api.model.reservation.Reservation;

public interface EmailService {
    void sendEmailForReservationAvailable(Reservation reservation);
}
