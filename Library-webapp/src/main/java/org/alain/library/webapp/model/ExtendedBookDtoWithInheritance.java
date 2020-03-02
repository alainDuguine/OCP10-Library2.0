package org.alain.library.webapp.model;

import io.swagger.client.model.BookDto;
import io.swagger.client.model.ReservationDto;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ExtendedBookDtoWithInheritance extends BookDto {

    private static List<String> Active_Statuses = Arrays.asList("PENDING","RESERVED");
    private List<ReservationDto> activeReservations;

    public ExtendedBookDtoWithInheritance(BookDto book) {
        this.setId(book.getId());
        this.setIsbn(book.getIsbn());
        this.setTitle(book.getTitle());
        this.setAuthors(book.getAuthors());
        this.setCopiesAvailable(book.getCopiesAvailable());
        this.setReservationListFull(book.isReservationListFull());
        this.setReservations(book.getReservations());
        this.activeReservations = this.setActiveReservations();
    }

    public List<ReservationDto> setActiveReservations() {
        return super.getReservations()
                .stream()
                .filter(reservation -> Active_Statuses.contains(reservation.getCurrentStatus()))
                .collect(Collectors.toList());
    }

    public static List<String> getActive_Statuses() {
        return Active_Statuses;
    }

    public static void setActive_Statuses(List<String> active_Statuses) {
        Active_Statuses = active_Statuses;
    }

    public List<ReservationDto> getActiveReservations() {
        return activeReservations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ExtendedBookDtoWithInheritance that = (ExtendedBookDtoWithInheritance) o;
        return Objects.equals(activeReservations, that.activeReservations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), activeReservations);
    }
}
