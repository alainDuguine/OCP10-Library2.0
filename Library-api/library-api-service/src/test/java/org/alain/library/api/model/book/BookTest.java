package org.alain.library.api.model.book;

import org.alain.library.api.model.reservation.Reservation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class BookTest {

    Book book;

    @BeforeEach
    void setUp() {
        book = Book.builder().id(1L).title("Titre Test").nbCopiesAvailable(0L).build();
        book.setCopyList(Collections.singletonList(BookCopy.builder().id(1L).build()));
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
    void removeReservation() {
        book.addReservation(Reservation.builder().id(1L).build());
        book.addReservation(Reservation.builder().id(2L).build());

        Reservation reservation = book.getReservations().get(0);

        book.removeReservation(reservation);

        assertThat(book.getReservations().size()).isEqualTo(1);
        assertThat(reservation.getBook()).isNull();

    }
}
