package org.alain.library.api.model.book;

import org.alain.library.api.model.exceptions.FullReservationListException;
import org.alain.library.api.model.reservation.Reservation;
import org.alain.library.api.model.reservation.ReservationStatus;
import org.alain.library.api.model.reservation.StatusEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class BookTest {

    Book book;

    @BeforeEach
    void setUp() {
        book = Book.builder().id(1L).title("Titre Test").nbCopiesAvailable(1L).build();
    }

    @Test
    void addReservation() {
        Reservation reservation = Reservation.builder().id(1L).build();

        int nbReservations = book.getReservations().size();
        book.addReservation(reservation);

        assertThat(book.getReservations().size()).isEqualTo(nbReservations+1);
        assertThat(reservation.getBook()).isEqualTo(book);
    }

    @Test
    void addReservationWhenReservationListFull() {
        book.addReservation(Reservation.builder().id(1L).build());
        book.addReservation(Reservation.builder().id(2L).build());

        int nbReservations = book.getReservations().size();
        assertThrows(FullReservationListException.class,
                () -> book.addReservation(Reservation.builder().id(3L).build()));

        assertThat(book.getReservations().size()).isEqualTo(nbReservations);
    }

    @Test
    void removeReservation() {
        book.addReservation(Reservation.builder().id(1L).build());
        book.addReservation(Reservation.builder().id(2L).build());

        Reservation reservation = book.getReservations().get(0);

        book.removeReservation(reservation);

        assertThat(book.getReservations().size()).isEqualTo(1);
        assertThat(reservation.getBook()).isNull();

    }
}
