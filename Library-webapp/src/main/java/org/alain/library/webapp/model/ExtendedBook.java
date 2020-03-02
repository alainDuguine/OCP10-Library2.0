package org.alain.library.webapp.model;

import io.swagger.client.model.BookDto;
import io.swagger.client.model.ReservationDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class ExtendedBook {

    private static List<String> Active_Statuses = Arrays.asList("PENDING","RESERVED");
    private final BookDto bookDto;
    private List<ReservationDto> activeReservations;
    private String earliestReturn;

    public ExtendedBook(BookDto bookDto) {
        this.bookDto = bookDto;
        activeReservations = this.setActiveReservations();
    }

    private List<ReservationDto> setActiveReservations() {
        return bookDto.getReservations()
                .stream()
                .filter(reservation -> Active_Statuses.contains(reservation.getCurrentStatus()))
                .collect(Collectors.toList());
    }
}
