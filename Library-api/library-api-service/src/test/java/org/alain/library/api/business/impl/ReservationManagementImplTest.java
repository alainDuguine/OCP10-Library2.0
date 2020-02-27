package org.alain.library.api.business.impl;

import org.alain.library.api.business.contract.BookManagement;
import org.alain.library.api.business.contract.UserManagement;
import org.alain.library.api.business.exceptions.ReservationException;
import org.alain.library.api.consumer.repository.ReservationRepository;
import org.alain.library.api.model.book.Book;
import org.alain.library.api.model.book.BookCopy;
import org.alain.library.api.model.loan.Loan;
import org.alain.library.api.model.reservation.Reservation;
import org.alain.library.api.model.reservation.ReservationStatus;
import org.alain.library.api.model.reservation.StatusEnum;
import org.alain.library.api.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ReservationManagementImplTest {

    @Mock
    ReservationRepository reservationRepository;
    @Mock
    BookManagement bookManagement;
    @Mock
    UserManagement userManagement;
    @InjectMocks
    ReservationManagementImpl service;

    Reservation reservation;

    @BeforeEach
    void setUp() {
        reservation = Reservation.builder().id(1L).build();
    }

    @Test
    void getReservation() {
        given(reservationRepository.findById(anyLong())).willReturn(Optional.of(reservation));

        Reservation reservation = service.findOne(1L).get();

        assertThat(reservation).isNotNull();
        assertThat(reservation.getId()).isEqualTo(1L);
    }

    @Test
    void getReservationsByStatusAndUserIdAndBookId() {
        given(reservationRepository.findByCurrentStatusAndUserIdAndBookId(anyString(), anyLong(), anyLong())).willReturn(Collections.singletonList(reservation));

        List<Reservation> reservationList = service.getReservationsByStatusAndUserIdAndBookId("PENDING", 1L, 1L);

        assertThat(reservationList.size()).isEqualTo(1);
        assertThat(reservationList.get(0).getId()).isEqualTo(1);
    }

    @Test
    void createNewReservationOk() {
        Book book = Book.builder().id(1L).nbCopiesAvailable(0L).title("test title").build();
        BookCopy copy = BookCopy.builder().id(1L).build();
        book.addCopy(copy);

        given(bookManagement.findOne(any())).willReturn(Optional.of(book));

        User user = User.builder().id(1L).roles("USER").email("test@emmail.com").build();
        given(userManagement.findOne(anyLong())).willReturn(Optional.of(user));

        UserPrincipal userPrincipal = new UserPrincipal(user);

        Reservation reservation = service.createNewReservation(book.getId(), userPrincipal.getId(), userPrincipal);

        assertThat(reservation).isNotNull();
        assertThat(reservation.getUser()).isEqualTo(user);
        assertThat(reservation.getBook()).isEqualTo(book);
        assertThat(reservation.getCurrentStatus()).isEqualTo(StatusEnum.PENDING.name());
        assertThat(reservation.getCurrentStatusDate()).isNotNull();
        assertThat(reservation.getStatuses().size()).isEqualTo(1);
        assertThat(user.getReservations().size()).isEqualTo(1);
    }

    @Test
    void RG1API_createNewReservationWithBookStillAvailable_throwsReservationException() {
        Book book = Book.builder().id(1L).nbCopiesAvailable(2L).title("test title").build();
        given(bookManagement.findOne(any())).willReturn(Optional.of(book));

        User user = User.builder().id(1L).roles("USER").email("test@emmail.com").build();
        given(userManagement.findOne(anyLong())).willReturn(Optional.of(user));

        UserPrincipal userPrincipal = new UserPrincipal(user);

        Exception exception = assertThrows(ReservationException.class, () -> service.createNewReservation(book.getId(), user.getId(), userPrincipal));

        String expectedMessage = "Impossible to add reservation, book id " + book.getId() + " has " + book.getNbCopiesAvailable() + " copies available";
        String actualMessage = exception.getMessage();
        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @Test
    void RG1API_createNewReservationWithListReservationFull_throwsReservationException() {
        Book book = Book.builder().id(1L).nbCopiesAvailable(0L).title("test title").build();
        given(bookManagement.findOne(any())).willReturn(Optional.of(book));

        User user = User.builder().id(1L).roles("USER").email("test@emmail.com").build();
        given(userManagement.findOne(anyLong())).willReturn(Optional.of(user));

        UserPrincipal userPrincipal = new UserPrincipal(user);

        Exception exception = assertThrows(ReservationException.class, () -> service.createNewReservation(book.getId(), user.getId(), userPrincipal));

        String expectedMessage = "Reservation List full : size:"+book.getReservations().size()+", book copies:"+ book.getNbCopiesAvailable();
        String actualMessage = exception.getMessage();
        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @Test
    void RG2API_createNewReservation_WithUserHavingLoanOfTheBook_throwsReservationException() {
        Book book = Book.builder().id(1L).nbCopiesAvailable(0L).title("test title").build();
        BookCopy bookCopy = BookCopy.builder().id(1L).build();
        book.addCopy(bookCopy);

        given(bookManagement.findOne(any())).willReturn(Optional.of(book));

        User user = User.builder().id(1L).roles("USER").email("test@emmail.com").build();
        BookCopy copy = BookCopy.builder().id(1L).build();
        copy.setBook(book);
        Loan loan = new Loan();
        loan.setBookCopy(copy);
        loan.setUser(user);
        loan.setCurrentStatus("LOANED");
        user.addLoan(loan);
        given(userManagement.findOne(anyLong())).willReturn(Optional.of(user));

        UserPrincipal userPrincipal = new UserPrincipal(user);

        Exception exception = assertThrows(ReservationException.class, () -> service.createNewReservation(book.getId(), user.getId(), userPrincipal));

        String expectedMessage = "User has already a copy of the book " + book.getId();
        String actualMessage = exception.getMessage();
        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @Test
    void RG2API_createNewReservation_WithUserHavingReservationOfTheBook_throwsReservationException() {
        Book book = Book.builder().id(1L).nbCopiesAvailable(0L).title("test title").build();
        BookCopy bookCopy = BookCopy.builder().id(1L).build();
        book.addCopy(bookCopy);
        given(bookManagement.findOne(any())).willReturn(Optional.of(book));

        User user = User.builder().id(1L).roles("USER").email("test@email.com").build();
        BookCopy copy = BookCopy.builder().id(1L).build();
        copy.setBook(book);

        reservation.setBook(book);
        reservation.setCurrentStatus(StatusEnum.PENDING.name());
        user.addReservation(reservation);

        given(userManagement.findOne(anyLong())).willReturn(Optional.of(user));

        UserPrincipal userPrincipal = new UserPrincipal(user);

        Exception exception = assertThrows(ReservationException.class, () -> service.createNewReservation(book.getId(), user.getId(), userPrincipal));

        String expectedMessage = "User has already a current reservation for the book " + reservation.getBook().getId();
        String actualMessage = exception.getMessage();
        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @Test
    void RG2API_createNewReservation_WithUserHavingReservationOfTheBookTerminated_shouldBeOk() {
        Book book = Book.builder().id(1L).nbCopiesAvailable(0L).title("test title").build();
        BookCopy bookCopy = BookCopy.builder().id(1L).build();
        book.addCopy(bookCopy);
        given(bookManagement.findOne(any())).willReturn(Optional.of(book));

        User user = User.builder().id(1L).roles("USER").email("test@email.com").build();
        BookCopy copy = BookCopy.builder().id(1L).build();
        copy.setBook(book);

        reservation.setBook(book);
        reservation.setCurrentStatus(StatusEnum.TERMINATED.name());
        user.addReservation(reservation);

        given(userManagement.findOne(anyLong())).willReturn(Optional.of(user));

        UserPrincipal userPrincipal = new UserPrincipal(user);

        Reservation reservation = service.createNewReservation(book.getId(), user.getId(), userPrincipal);

        assertThat(reservation).isNotNull();
        assertThat(reservation.getCurrentStatus()).isEqualTo(StatusEnum.PENDING.name());
        assertThat(reservation.getUser()).isEqualTo(user);
        assertThat(reservation.getBook()).isEqualTo(book);
    }

    @Test
    void updateReservation() {
        User user = User.builder().id(1L).roles("USER").email("test@email.com").build();
        UserPrincipal userPrincipal = new UserPrincipal(user);

        user.addReservation(reservation);
        given(reservationRepository.findById(anyLong())).willReturn(Optional.of(reservation));
        given(reservationRepository.save(any())).willReturn(reservation);

        Optional<Reservation> reservationResult = service.updateReservation(1L, "terminated", userPrincipal);

        assertThat(reservationResult.isPresent()).isTrue();
    }

    @Test
    void updateReservation_withWrongUser_returnEmpty() {
        User user = User.builder().id(1L).roles("USER").email("test@email.com").build();
        UserPrincipal userPrincipal = new UserPrincipal(user);

        User user2 = User.builder().id(2L).roles("USER").email("wronguser@email.com").build();
        user2.addReservation(reservation);

        given(reservationRepository.findById(anyLong())).willReturn(Optional.of(reservation));

        Optional<Reservation> reservationResult = service.updateReservation(1L, "terminated", userPrincipal);

        assertThat(reservationResult.isPresent()).isFalse();
    }

    @Test
    void updateAndGetExpiredReservation() {
        ReservationStatus reservationStatusOk = ReservationStatus.builder().status(StatusEnum.RESERVED).date(LocalDateTime.now()).build();
        ReservationStatus reservationStatusExpired = ReservationStatus.builder().status(StatusEnum.RESERVED).date(LocalDateTime.now().minusDays(2)).build();

        Reservation reservation1 = Reservation.builder().id(1L).build();
        reservation1.addStatus(reservationStatusOk);

        Reservation reservation2 = Reservation.builder().id(2L).build();
        reservation2.addStatus(reservationStatusExpired);

        List<Reservation> reservationList = new ArrayList<>();
        reservationList.add(reservation1);
        reservationList.add(reservation2);

        given(reservationRepository.findByCurrentStatusAndUserIdAndBookId(anyString(), any(), any())).willReturn(reservationList);

        List<Reservation> reservationListResult = service.updateAndGetExpiredReservation();

        assertThat(reservationListResult.size()).isEqualTo(1);
        assertThat(reservationListResult.get(0).getCurrentStatus()).isEqualTo(StatusEnum.CANCELED.name());
        assertThat(reservationListResult.get(0).getCurrentStatusDate().isAfter(reservationStatusExpired.getDate())).isTrue();
    }

    @Test
    void testGetReservationsByStatusAndUserIdAndBookId() {
    }

    @Test
    void createNewReservation() {
    }

    @Test
    void testUpdateReservation() {
    }

    @Test
    void testUpdateAndGetExpiredReservation() {
    }
}
