package org.alain.library.webapp.model;

import io.swagger.client.model.ReservationDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

import static org.alain.library.webapp.WebAppUtilities.ACTIVE_STATUS;
import static org.alain.library.webapp.WebAppUtilities.DATE_FORMATTER;

@Data
@Builder
@AllArgsConstructor
public class ExtendedReservation {
    private final ReservationDto reservationDto;
    private LocalDate currentStatusDate;
    private LocalDate nextReturnDate;
    private boolean isActive;

    public ExtendedReservation(ReservationDto reservationDto) {
        this.reservationDto = reservationDto;
        this.currentStatusDate = LocalDate.parse(reservationDto.getCurrentStatusDate(), DATE_FORMATTER);
        this.isActive = ACTIVE_STATUS.contains(reservationDto.getCurrentStatus());
        if(reservationDto.getDateNextReturnBook()!=null)
            this.nextReturnDate = LocalDate.parse(reservationDto.getDateNextReturnBook(), DATE_FORMATTER);
    }
}
