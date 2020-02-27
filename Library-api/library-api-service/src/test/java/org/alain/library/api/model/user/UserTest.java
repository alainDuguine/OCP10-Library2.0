package org.alain.library.api.model.user;

import org.alain.library.api.model.reservation.Reservation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    User user;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).build();
    }

    @Test
    void addReservation() {
        Reservation reservation = Reservation.builder().id(1L).build();

        int nbReservations = user.getReservations().size();
        user.addReservation(reservation);

        assertThat(user.getReservations().size()).isEqualTo(nbReservations+1);
        assertThat(reservation.getUser()).isEqualTo(user);
    }

    @Test
    void removeReservation() {
        List<Reservation> reservationList = new ArrayList<>();
        reservationList.add(Reservation.builder().id(1L).build());
        reservationList.add(Reservation.builder().id(2L).build());

        user.setReservations(reservationList);

        Reservation reservation = user.getReservations().get(0);
        user.removeReservation(reservation);

        assertThat(user.getReservations().size()).isEqualTo(1);
        assertThat(reservation.getUser()).isNull();
    }
}
