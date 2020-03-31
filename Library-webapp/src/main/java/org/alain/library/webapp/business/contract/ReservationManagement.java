package org.alain.library.webapp.business.contract;

import io.swagger.client.model.ReservationDto;
import org.alain.library.webapp.model.ExtendedReservation;

import java.util.List;

public interface ReservationManagement {
    List<ExtendedReservation> getExtendedReservations(List<ReservationDto> reservations);
}
