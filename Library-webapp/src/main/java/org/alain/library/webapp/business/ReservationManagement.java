package org.alain.library.webapp.business;

import io.swagger.client.model.ReservationDto;
import org.alain.library.webapp.model.ExtendedReservation;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationManagement {

    public List<ExtendedReservation> getExtendedReservations(List<ReservationDto> reservations){
        return reservations.stream()
                .map(ExtendedReservation::new)
                .collect(Collectors.toList());
    }
}
