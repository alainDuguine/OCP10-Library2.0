package org.alain.library.api.service.api;

import org.alain.library.api.model.book.Book;
import org.alain.library.api.model.reservation.Reservation;
import org.alain.library.api.model.reservation.ReservationStatus;
import org.alain.library.api.model.reservation.StatusEnum;
import org.alain.library.api.model.user.User;
import org.alain.library.api.service.dto.BookDto;
import org.alain.library.api.service.dto.ReservationDto;
import org.alain.library.api.service.dto.ReservationStatusDto;
import org.alain.library.api.service.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ConvertersTest {

    User user;
    Book book;
    Reservation reservation;
    List<Reservation> reservations = new ArrayList<>();

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).email("testuser@email.com").build();
        book = Book.builder().id(1L).title("Test Book").nbCopiesAvailable(2L).build();

        List<ReservationStatus> statuses = new ArrayList<>(Arrays.asList(
                ReservationStatus.builder().id(1L).date(LocalDateTime.now().minusDays(2L)).status(StatusEnum.RESERVED).build(),
                ReservationStatus.builder().id(2L).date(LocalDateTime.now()).status(StatusEnum.RESERVED).build()
        ));

        reservation = Reservation.builder().id(1L).build();
        reservation.setStatuses(statuses);

        user.addReservation(reservation);
        book.addReservation(reservation);

    }

    @Test
    void convertBookModelToBookDto() {
        BookDto bookDto = Converters.convertBookModelToBookDto(book);

        assertThat(bookDto.getId()).isEqualTo(book.getId());
        assertThat(bookDto.getTitle()).isEqualTo(book.getTitle());
        assertThat(bookDto.getCopiesAvailable()).isEqualTo(book.getNbCopiesAvailable());
        assertThat(bookDto.getReservations().size()).isEqualTo(1);
    }

    @Test
    void convertUserModelToUserDto() {
        UserDto userDto = Converters.convertUserModelToUserDto(user);

        assertThat(userDto.getReservations().size()).isEqualTo(1);
        assertThat(userDto.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void convertReservationModelToReservationDto() {
        ReservationDto reservationDto = Converters.convertReservationModelToReservationDto(reservation);

        assertThat(reservationDto.getId()).isEqualTo(reservation.getId());
        assertThat(reservationDto.getStatuses().size()).isEqualTo(2);
        assertThat(reservationDto.getUserId()).isEqualTo(reservation.getUser().getId());
        assertThat(reservationDto.getUserEmail()).isEqualTo(reservation.getUser().getEmail());
        assertThat(reservationDto.getBookTitle()).isEqualTo(reservation.getBook().getTitle());
    }

    @Test
    void convertListReservationModelToListReservationDto() {
        Reservation reservation2 = Reservation.builder().id(1L).build();
        reservation2.addStatus(ReservationStatus.builder().id(3L).date(LocalDateTime.now()).status(StatusEnum.RESERVED).build());

        user.addReservation(reservation2);
        book.addReservation(reservation2);

        reservations.add(reservation);
        reservations.add(reservation2);

        List<ReservationDto> reservationDtoList = Converters.convertListReservationModelToListReservationDto(reservations);

        assertThat(reservationDtoList.size()).isEqualTo(2);
        assertThat(reservationDtoList.get(0).getStatuses().size()).isEqualTo(2);
        assertThat(reservationDtoList.contains(Converters.convertReservationModelToReservationDto(reservation2))).isTrue();
        assertThat(reservationDtoList.contains(Converters.convertReservationModelToReservationDto(reservation))).isTrue();
    }

    @Test
    void convertReservationStatusModelToReservationStatusDto() {
        ReservationStatus reservationStatus = ReservationStatus.builder().id(1L).status(StatusEnum.RESERVED).date(LocalDateTime.now()).build();

        ReservationStatusDto reservationStatusDto = Converters.convertReservationStatusModelToReservationStatusDto(reservationStatus);

        assertThat(reservationStatusDto.getDate()).isNotNull();
        assertThat(reservationStatusDto.getStatus()).isEqualTo(reservationStatus.getStatus().name());
    }

    @Test
    void convertListReservationStatusModelToListReservationStatusDto() {
        ReservationStatus reservationStatus = ReservationStatus.builder().id(1L).status(StatusEnum.RESERVED).date(LocalDateTime.now()).build();
        ReservationStatus reservationStatus2 = ReservationStatus.builder().id(2L).status(StatusEnum.RESERVED).date(LocalDateTime.now()).build();

        List<ReservationStatus> reservationStatusList = new ArrayList<>();
        reservationStatusList.add(reservationStatus);
        reservationStatusList.add(reservationStatus2);

        List<ReservationStatusDto> reservationStatusDtoList = Converters.convertListReservationStatusModelToListReservationStatusDto(reservationStatusList);

        assertThat(reservationStatusDtoList.size()).isEqualTo(2);
        assertThat(reservationStatusDtoList.contains(Converters.convertReservationStatusModelToReservationStatusDto(reservationStatus))).isTrue();
        assertThat(reservationStatusDtoList.contains(Converters.convertReservationStatusModelToReservationStatusDto(reservationStatus2))).isTrue();
    }
}
