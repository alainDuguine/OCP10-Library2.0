package org.alain.library.webapp.model;

import io.swagger.client.model.ReservationDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

import static org.alain.library.webapp.WebAppUtilities.DATE_FORMATTER;

@Data
@Builder
@AllArgsConstructor
public class ExtendedReservation {
    private final ExtendedBook extendedBook;
    private final ReservationDto reservationDto;
    private LocalDate currentStatusDate;

    public ExtendedReservation(ExtendedBook extendedBook, ReservationDto reservationDto) {
        this.extendedBook = extendedBook;
        this.reservationDto = reservationDto;
        this.currentStatusDate = LocalDate.parse(reservationDto.getCurrentStatusData(), DATE_FORMATTER);
    }
}
