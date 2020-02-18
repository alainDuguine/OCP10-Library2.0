package org.alain.library.api.model.reservation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ReservationTest {

    Reservation reservation;

    @BeforeEach
    void setUp() {
        reservation = Reservation.builder().id(1L).build();
    }

    @Test
    void addStatus() {
        ReservationStatus status = ReservationStatus.builder().status(StatusEnum.NOTIFIED).build();

        int nbStatuses = reservation.getStatuses().size();
        reservation.addStatus(status);

        assertThat(reservation.getStatuses().size()).isEqualTo(nbStatuses+1);
        assertThat(status.getReservation()).isEqualTo(reservation);
        assertThat(status.getDate()).isNotNull();
    }

    @Test
    void removeStatus() {
        reservation.addStatus(ReservationStatus.builder().id(1L).build());
        reservation.addStatus(ReservationStatus.builder().id(2L).build());

        ReservationStatus status = reservation.getStatuses().get(0);

        reservation.removeStatus(status);

        assertThat(reservation.getStatuses().size()).isEqualTo(1);
        assertThat(status.getReservation()).isNull();
    }
}
