package org.alain.library.api.business.impl;

import org.alain.library.api.business.contract.ReservationManagement;
import org.alain.library.api.consumer.repository.BookRepository;
import org.alain.library.api.consumer.repository.ReservationRepository;
import org.alain.library.api.consumer.repository.UserRepository;
import org.alain.library.api.mail.contract.EmailService;
import org.alain.library.api.model.book.Book;
import org.alain.library.api.model.reservation.Reservation;
import org.alain.library.api.model.reservation.StatusEnum;
import org.alain.library.api.model.user.User;
import org.alain.library.api.service.LibraryApiServiceApplication;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = LibraryApiServiceApplication.class)
@TestPropertySource(locations = {"classpath:/application-test.properties"})
class ReservationManagementIT {

    @Mock
    private EmailService emailService;
    @Autowired
    private ReservationManagement reservationManagement;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookRepository bookRepository;


    List<Reservation> reservationList;

    private static int RESERVATION_EXPIRATION_DAYS = 2;


    @BeforeEach
    void setUp() {
        User user = userRepository.getOne(1L);
        Book book = bookRepository.getOne(1L);
        Reservation reservation1 = Reservation.builder().currentStatus(StatusEnum.RESERVED.name()).currentStatusDate(LocalDateTime.now()).user(user).book(book).build();
        Reservation reservation2 = Reservation.builder().currentStatus(StatusEnum.RESERVED.name()).currentStatusDate(LocalDateTime.now().minusDays(1)).user(user).book(book).build();
        Reservation reservation3 = Reservation.builder().currentStatus(StatusEnum.RESERVED.name()).currentStatusDate(LocalDateTime.now().minusDays(2)).user(user).book(book).build();
        Reservation reservation4 = Reservation.builder().currentStatus(StatusEnum.RESERVED.name()).currentStatusDate(LocalDateTime.now().minusDays(3)).user(user).book(book).build();
        Reservation reservation5 = Reservation.builder().currentStatus(StatusEnum.PENDING.name()).currentStatusDate(LocalDateTime.now().minusDays(4)).user(user).book(book).build();
        reservationList = Arrays.asList(reservation1,reservation2,reservation3,reservation4,reservation5);
    }


    @AfterEach
    void tearDown() {
        reservationRepository.deleteAll(reservationList);
    }

    @Test
    void findExpiredReservations() {
        reservationRepository.saveAll(reservationList);
        LocalDateTime expirationDate = LocalDateTime.now().minusDays(RESERVATION_EXPIRATION_DAYS);
        List<Reservation> expired = reservationRepository.findExpired(StatusEnum.RESERVED.name(), expirationDate);

        assertThat(expired.size()).isEqualTo(2);
        assertThat(expired).extracting(Reservation::getCurrentStatus).contains("RESERVED");
        assertThat(expired).allMatch(el -> el.getCurrentStatusDate().isBefore(expirationDate));

    }

    @Test
    @Transactional
    void getUserPositionInReservationList(){
        List<Reservation> reservationListUser4 = reservationManagement.getReservationsByUser(4L);
        List<Reservation> reservationListUser3 = reservationManagement.getReservationsByUser(3L);
        List<Reservation> reservationListUser1 = reservationManagement.getReservationsByUser(1L);

        assertThat(reservationListUser4.size()).isEqualTo(1);
        assertThat(reservationListUser3.size()).isEqualTo(4);
        assertThat(reservationListUser1.size()).isEqualTo(1);

        assertThat(reservationListUser1.get(0).getUserPositionInList()).isEqualTo(1);
        assertThat(reservationListUser4.get(0).getUserPositionInList()).isEqualTo(2);
    }

    @Test
    @Transactional
    void checkPendingListAndNotify() {
        reservationManagement.checkPendingListAndNotify(1L);
    }
}
