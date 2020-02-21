package org.alain.library.api.model.reservation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
        ReservationStatus status = ReservationStatus.builder().status(StatusEnum.RESERVED).build();

        int nbStatuses = reservation.getStatuses().size();
        reservation.addStatus(status);

        assertThat(reservation.getStatuses().size()).isEqualTo(nbStatuses+1);
        assertThat(reservation.getCurrentStatus()).isEqualTo(status.getStatus().name());
        assertThat(reservation.getCurrentStatusDate()).isEqualTo(status.getDate());
        assertThat(status.getReservation()).isEqualTo(reservation);
        assertThat(status.getDate()).isNotNull();
    }

    @Test
    void addStatusWithOldDate() {
        ReservationStatus status = ReservationStatus.builder().status(StatusEnum.RESERVED).build();

        reservation.addStatus(status);
        int nbStatuses = reservation.getStatuses().size();

        ReservationStatus oldStatus = ReservationStatus.builder().status(StatusEnum.RESERVED).date(LocalDateTime.of(2019, 01,01,00,00)).build();
        reservation.addStatus(oldStatus);

        assertThat(reservation.getStatuses().size()).isEqualTo(nbStatuses+1);
        assertThat(reservation.getCurrentStatus()).isEqualTo(status.getStatus().name());
        assertThat(reservation.getCurrentStatusDate()).isEqualTo(status.getDate());
    }

    @Test
    void removeStatus() {
        reservation.addStatus(ReservationStatus.builder().id(1L).status(StatusEnum.RESERVED).date(LocalDateTime.now()).build());
        reservation.addStatus(ReservationStatus.builder().id(2L).status(StatusEnum.RESERVED).date(LocalDateTime.now()).build());

        ReservationStatus status = reservation.getStatuses().get(0);

        reservation.removeStatus(status);

        assertThat(reservation.getStatuses().size()).isEqualTo(1);
        assertThat(status.getReservation()).isNull();
    }
}
