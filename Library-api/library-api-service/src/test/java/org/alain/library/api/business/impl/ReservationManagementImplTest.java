package org.alain.library.api.business.impl;

import org.alain.library.api.business.contract.BookManagement;
import org.alain.library.api.business.exceptions.ReservationException;
import org.alain.library.api.business.exceptions.UnauthorizedException;
import org.alain.library.api.consumer.repository.BookRepository;
import org.alain.library.api.consumer.repository.ReservationRepository;
import org.alain.library.api.consumer.repository.UserRepository;
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
import java.util.*;

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
    BookRepository bookRepository;
    @Mock
    UserRepository userRepository;
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
        Book book = Book.builder().id(1L).nbCopiesAvailable(0L).nbActiveReservations(0L).title("test title").build();
        BookCopy copy = BookCopy.builder().id(1L).build();
        book.addCopy(copy);

        given(bookRepository.findById(any())).willReturn(Optional.of(book));

        User user = User.builder().id(1L).roles("USER").email("test@emmail.com").build();
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

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
        given(bookRepository.findById(any())).willReturn(Optional.of(book));

        User user = User.builder().id(1L).roles("USER").email("test@emmail.com").build();
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

        UserPrincipal userPrincipal = new UserPrincipal(user);

        Exception exception = assertThrows(ReservationException.class, () -> service.createNewReservation(book.getId(), user.getId(), userPrincipal));

        String expectedMessage = "Impossible to add reservation, book id " + book.getId() + " has " + book.getNbCopiesAvailable() + " copies available";
        String actualMessage = exception.getMessage();
        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @Test
    void RG1API_createNewReservationWithListReservationFull_throwsReservationException() {
        Book book = Book.builder().id(1L).nbCopiesAvailable(0L).nbActiveReservations(2L).title("test title").build();
        given(bookRepository.findById(any())).willReturn(Optional.of(book));

        User user = User.builder().id(1L).roles("USER").email("test@emmail.com").build();
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

        UserPrincipal userPrincipal = new UserPrincipal(user);

        Exception exception = assertThrows(ReservationException.class, () -> service.createNewReservation(book.getId(), user.getId(), userPrincipal));

        String expectedMessage = "Reservation List full : size:" + book.getReservations().size() + ", book copies:" + book.getNbCopiesAvailable();
        String actualMessage = exception.getMessage();
        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @Test
    void RG2API_createNewReservation_WithUserHavingLoanOfTheBook_throwsReservationException() {
        Book book = Book.builder().id(1L).nbCopiesAvailable(0L).nbActiveReservations(0L).title("test title").build();
        BookCopy bookCopy = BookCopy.builder().id(1L).build();
        book.addCopy(bookCopy);

        given(bookRepository.findById(any())).willReturn(Optional.of(book));

        User user = User.builder().id(1L).roles("USER").email("test@emmail.com").build();
        BookCopy copy = BookCopy.builder().id(1L).build();
        copy.setBook(book);
        Loan loan = new Loan();
        loan.setBookCopy(copy);
        loan.setUser(user);
        loan.setCurrentStatus("LOANED");
        user.addLoan(loan);
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

        UserPrincipal userPrincipal = new UserPrincipal(user);

        Exception exception = assertThrows(ReservationException.class, () -> service.createNewReservation(book.getId(), user.getId(), userPrincipal));

        String expectedMessage = "User has already a copy of the book " + book.getId();
        String actualMessage = exception.getMessage();
        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @Test
    void RG2API_createNewReservation_WithUserHavingReservationOfTheBook_throwsReservationException() {
        Book book = Book.builder().id(1L).nbCopiesAvailable(0L).nbActiveReservations(0L).title("test title").build();
        BookCopy bookCopy = BookCopy.builder().id(1L).build();
        book.addCopy(bookCopy);
        given(bookRepository.findById(any())).willReturn(Optional.of(book));

        User user = User.builder().id(1L).roles("USER").email("test@email.com").build();
        BookCopy copy = BookCopy.builder().id(1L).build();
        copy.setBook(book);

        reservation.setBook(book);
        reservation.setCurrentStatus(StatusEnum.PENDING.name());
        user.addReservation(reservation);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

        UserPrincipal userPrincipal = new UserPrincipal(user);

        Exception exception = assertThrows(ReservationException.class, () -> service.createNewReservation(book.getId(), user.getId(), userPrincipal));

        String expectedMessage = "User has already a current reservation for the book " + reservation.getBook().getId();
        String actualMessage = exception.getMessage();
        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @Test
    void RG2API_createNewReservation_WithUserHavingReservationOfTheBookTerminated_shouldBeOk() {
        Book book = Book.builder().id(1L).nbCopiesAvailable(0L).nbActiveReservations(0L).title("test title").build();
        BookCopy bookCopy = BookCopy.builder().id(1L).build();
        book.addCopy(bookCopy);
        given(bookRepository.findById(any())).willReturn(Optional.of(book));

        User user = User.builder().id(1L).roles("USER").email("test@email.com").build();
        BookCopy copy = BookCopy.builder().id(1L).build();
        copy.setBook(book);

        reservation.setBook(book);
        reservation.setCurrentStatus(StatusEnum.TERMINATED.name());
        user.addReservation(reservation);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

        UserPrincipal userPrincipal = new UserPrincipal(user);

        Reservation reservation = service.createNewReservation(book.getId(), user.getId(), userPrincipal);

        assertThat(reservation).isNotNull();
        assertThat(reservation.getCurrentStatus()).isEqualTo(StatusEnum.PENDING.name());
        assertThat(reservation.getUser()).isEqualTo(user);
        assertThat(reservation.getBook()).isEqualTo(book);
    }

    @Test
    void updateReservation_withAdminRole() {
        User user = User.builder().id(1L).roles("USER").email("test@email.com").roles("ADMIN").build();
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
    void updateReservation_withStatusNotEqualsToCanceled_withSimpleUser_returnEmpty() {
        User user = User.builder().id(1L).roles("USER").email("test@email.com").build();
        UserPrincipal userPrincipal = new UserPrincipal(user);

        user.addReservation(reservation);

        given(reservationRepository.findById(anyLong())).willReturn(Optional.of(reservation));

        assertThrows(UnauthorizedException.class, () ->
                service.updateReservation(1L, "reserved", userPrincipal));
    }

    @Test
    void updateAndGetExpiredReservation() {
        Book book = Book.builder().id(1L).build();

        ReservationStatus reservationStatus1 = ReservationStatus.builder().status(StatusEnum.RESERVED).date(LocalDateTime.now().minusDays(4)).build();
        ReservationStatus reservationStatus2 = ReservationStatus.builder().status(StatusEnum.RESERVED).date(LocalDateTime.now().minusDays(3)).build();

        Reservation reservation1 = Reservation.builder().id(1L).book(book).build();
        reservation1.addStatus(reservationStatus1);

        Reservation reservation2 = Reservation.builder().id(2L).book(book).build();
        reservation2.addStatus(reservationStatus2);

        List<Reservation> reservationList = new ArrayList<>();
        reservationList.add(reservation1);
        reservationList.add(reservation2);

        given(reservationRepository.findExpired(any(),any())).willReturn(reservationList);

        List<Reservation> reservationListResult = service.updateAndGetExpiredReservation();

        assertThat(reservationListResult.size()).isEqualTo(2);
        assertThat(reservationListResult.get(0).getCurrentStatus()).isEqualTo(StatusEnum.CANCELED.name());
        assertThat(reservationListResult.get(0).getCurrentStatusDate().isAfter(reservationStatus1.getDate())).isTrue();
    }

    @Test
    void getReservationsByUser() {
        ReservationStatus status1 = ReservationStatus.builder().id(1L).status(StatusEnum.PENDING).date(LocalDateTime.now().minusDays(1)).build();
        ReservationStatus status2 = ReservationStatus.builder().id(1L).status(StatusEnum.PENDING).date(LocalDateTime.now().minusDays(2)).build();
        ReservationStatus status3 = ReservationStatus.builder().id(1L).status(StatusEnum.PENDING).date(LocalDateTime.now().minusDays(3)).build();
        ReservationStatus inactiveStatus = ReservationStatus.builder().id(1L).status(StatusEnum.TERMINATED).date(LocalDateTime.now().minusDays(3)).build();


        Book book = Book.builder().id(1L).build();

        User user = User.builder().id(1L).build();
        User user2 = User.builder().id(2L).build();
        User user3 = User.builder().id(3L).build();
        // Reservation we are looking for
        Reservation reservation1 = Reservation.builder().id(1L).book(book).user(user).build();
        // user should be first in list
        reservation1.addStatus(status1);

        // Other reservations in list
        Reservation reservation2 = Reservation.builder().id(2L).book(book).user(user2).build();
        reservation2.addStatus(status3);
        Reservation reservation3 = Reservation.builder().id(3L).book(book).user(user3).build();
        reservation3.addStatus(status2);
        Reservation reservation4 = Reservation.builder().id(4L).book(book).user(user3).build();
        reservation4.addStatus(inactiveStatus);
        List<Reservation> reservationList = new ArrayList<>(Arrays.asList(reservation1, reservation2, reservation3, reservation4));

        user.addReservation(reservation1);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(reservationRepository.findActiveReservationForBookOrderByDate(any())).willReturn(reservationList);

        List<Reservation> reservationsResultList = service.getReservationsByUser(1L);
        assertThat(reservationsResultList.get(0).getUserPositionInList()).isEqualTo(1);
    }

    @Test
    void getReservationsByUser_WithEmptyList() {
        User user = User.builder().id(1L).build();

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

        List<Reservation> reservationsResultList = service.getReservationsByUser(1L);
        assertThat(reservationsResultList.size()).isEqualTo(0);
    }
}

