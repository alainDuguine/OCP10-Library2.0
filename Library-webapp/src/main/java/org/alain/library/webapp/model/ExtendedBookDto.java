package org.alain.library.webapp.model;

import io.swagger.client.model.BookDto;
import io.swagger.client.model.ReservationDto;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ExtendedBookDto{

    private static List<String> Active_Statuses = Arrays.asList("PENDING","RESERVED");
    private final BookDto bookDto;
    private List<ReservationDto> activeReservations;

    public ExtendedBookDto(BookDto bookDto) {
        this.bookDto = bookDto;
        activeReservations = this.getActiveReservations();
    }

    private List<ReservationDto> getActiveReservations() {
        return bookDto.getReservations()
                .stream()
                .filter(reservation -> Active_Statuses.contains(reservation.getCurrentStatus()))
                .collect(Collectors.toList());
    }
}
