package org.alain.library.webapp.business.impl;

import io.swagger.client.model.ReservationDto;
import org.alain.library.webapp.business.contract.ReservationManagement;
import org.alain.library.webapp.model.ExtendedReservation;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationManagementImpl implements ReservationManagement {

    @Override
    public List<ExtendedReservation> getExtendedReservations(List<ReservationDto> reservations){
        return reservations.stream()
                .map(ExtendedReservation::new)
                .collect(Collectors.toList());
    }
}
