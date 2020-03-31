package org.alain.library.webapp.model;

import io.swagger.client.model.BookDto;
import io.swagger.client.model.ReservationDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.alain.library.webapp.WebAppUtilities.ACTIVE_STATUS;
import static org.alain.library.webapp.WebAppUtilities.DATE_FORMATTER;

@Data
@AllArgsConstructor
@Builder
public class ExtendedBook {

    private final BookDto bookDto;
    private List<ReservationDto> activeReservations;
    private LocalDate earliestReturn;

    public ExtendedBook(BookDto bookDto) {
        this.bookDto = bookDto;
        activeReservations = this.setActiveReservations();
        if(bookDto.getDateNextReturnBook()!=null){
            this.earliestReturn = LocalDate.parse(bookDto.getDateNextReturnBook(), DATE_FORMATTER);
        }
    }

    private List<ReservationDto> setActiveReservations() {
        return bookDto.getReservations()
                .stream()
                .filter(reservation -> ACTIVE_STATUS.contains(reservation.getCurrentStatus()))
                .collect(Collectors.toList());
    }
}
