package org.alain.library.api.service.api;

import org.alain.library.api.business.contract.BookManagement;
import org.alain.library.api.business.contract.UserManagement;
import org.alain.library.api.consumer.repository.ReservationRepository;
import org.alain.library.api.consumer.repository.ReservationStatusRepository;
import org.alain.library.api.model.book.Book;
import org.alain.library.api.model.reservation.Reservation;
import org.alain.library.api.model.reservation.StatusEnum;
import org.alain.library.api.model.user.User;
import org.alain.library.api.service.dto.ReservationDto;
import org.alain.library.api.service.dto.ReservationForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = {"classpath:/ReservationIT.properties","classpath:/application.properties"})
class ReservationsApiControllerIT {

    @Value("${test.username}")
    String username;
    @Value("${test.password}")
    String password;
    String apiURL;

    private final BookManagement bookManagement;
    private final UserManagement userManagement;
    private final ReservationRepository reservationRepository;
    private final ReservationStatusRepository reservationStatusRepository;

    private TestRestTemplate restTemplate;

    @LocalServerPort
    int randomServerPort;

    @Autowired
    ReservationsApiControllerIT(BookManagement bookManagement, UserManagement userManagement, ReservationRepository reservationRepository, ReservationStatusRepository reservationStatusRepository) {
        this.bookManagement = bookManagement;
        this.userManagement = userManagement;
        this.reservationRepository = reservationRepository;
        this.reservationStatusRepository = reservationStatusRepository;
    }


    @BeforeEach
    void setUp() {
        restTemplate = new TestRestTemplate(username,password);
        apiURL = "http://localhost:" + randomServerPort;
    }

    @Test
    void addReservation() {
        Book book = bookManagement.findOne(7L).get();
        User user = userManagement.findOne(2L).get();

        ReservationForm reservationForm = new ReservationForm();
        reservationForm.setUserId(user.getId());
        reservationForm.setBookId(book.getId());

        ResponseEntity<ReservationDto> result = restTemplate.postForEntity(apiURL+"/reservations", reservationForm, ReservationDto.class);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result).isNotNull();
        assertThat(result.getBody().getId()).isNotNull();
        assertThat(result.getBody().getUserId()).isEqualTo(user.getId());
        assertThat(result.getBody().getUserEmail()).isEqualTo(user.getEmail());
        assertThat(result.getBody().getBookId()).isEqualTo(book.getId());
        assertThat(result.getBody().getBookTitle()).isEqualTo(book.getTitle());
        assertThat(result.getBody().getStatuses()).isNotNull();
        assertThat(result.getBody().getStatuses().get(0).getStatus()).isEqualTo(StatusEnum.PENDING.name());
        assertThat(result.getBody().getStatuses().get(0).getDate()).isNotNull();

        reservationRepository.deleteById(result.getBody().getId());
    }

    @Test
    void checkRG1Failed_addReservation_shouldNotBePersisted() {
        // Book has no copy available, and only one copy in total in library

        Book book = bookManagement.findOne(7L).get();
        List<User> users = userManagement.findAll();

        // users(0) already has a loan for book id 7
        User user1 = users.stream().filter(user -> user.getId() == 2).findAny().orElse(null);
        User user2 = users.stream().filter(user -> user.getId() == 3).findAny().orElse(null);
        User user3 = users.stream().filter(user -> user.getId() == 4).findAny().orElse(null);

        ReservationForm reservationForm = new ReservationForm();
        reservationForm.setUserId(user1.getId());
        reservationForm.setBookId(book.getId());

        ResponseEntity<ReservationDto> responseEntity1 = restTemplate.postForEntity(apiURL + "/reservations", reservationForm, ReservationDto.class);

        ReservationForm reservationForm2 = new ReservationForm();
        reservationForm2.setUserId(user2.getId());
        reservationForm2.setBookId(book.getId());

        ResponseEntity<ReservationDto> responseEntity2 = restTemplate.postForEntity(apiURL+"/reservations", reservationForm2, ReservationDto.class);

        ReservationForm reservationForm3 = new ReservationForm();
        reservationForm3.setUserId(user3.getId());
        reservationForm3.setBookId(book.getId());

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(apiURL+"/reservations", reservationForm2, String.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody()).isEqualTo("Reservation List full : size:2, book copies:1");

        reservationRepository.deleteById(responseEntity1.getBody().getId());
        reservationRepository.deleteById(responseEntity2.getBody().getId());

    }

    @Test
    void checkRG2Failed_addReservation_shouldNotBePersisted() {

        Book book = bookManagement.findOne(7L).get();
        User user = userManagement.findOne(1L).get();

        ReservationForm reservationForm = new ReservationForm();
        reservationForm.setUserId(user.getId());
        reservationForm.setBookId(book.getId());

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(apiURL+"/reservations", reservationForm, String.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody()).isEqualTo("User has already a copy of the book 7");
    }

    @Test
    void getReservation() {
        Book book = bookManagement.findOne(7L).get();
        User user = userManagement.findAll().get(0);

        ReservationForm reservationForm = new ReservationForm();
        reservationForm.setUserId(user.getId());
        reservationForm.setBookId(book.getId());

        ResponseEntity<ReservationDto> result = restTemplate.postForEntity(apiURL+"/reservations", reservationForm, ReservationDto.class);
        Long id = result.getBody().getId();

        ResponseEntity<ReservationDto> reservation = restTemplate.getForEntity(apiURL+"/reservations/"+id, ReservationDto.class);

        assertThat(reservation).isNotNull();
        assertThat(reservation.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(reservation.getBody().getId()).isEqualTo(id);

        reservationRepository.deleteById(result.getBody().getId());
    }

    @Test
    void getReservations() {
        ResponseEntity<ReservationDto[]> reservations = restTemplate.getForEntity(apiURL+"/reservations", ReservationDto[].class);

        assertThat(reservations.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(reservations.getBody().length).isEqualTo(6);
    }

    @Test
    void getReservations_withParameters() {
        ResponseEntity<ReservationDto[]> reservations = restTemplate.getForEntity(apiURL+"/reservations?currentStatus=pending&user=3&book=17", ReservationDto[].class);

        assertThat(reservations.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(reservations.getBody().length).isEqualTo(1);
    }

    @Test
    void updateReservation() {
        Book book = bookManagement.findOne(7L).get();
        User user = userManagement.findOne(2L).get();

        ReservationForm reservationForm = new ReservationForm();
        reservationForm.setUserId(user.getId());
        reservationForm.setBookId(book.getId());

        ResponseEntity<ReservationDto> result = restTemplate.postForEntity(apiURL+"/reservations", reservationForm, ReservationDto.class);
        String status = StatusEnum.RESERVED.name();

        restTemplate.put(apiURL+"/reservations/"+result.getBody().getId()+"?status="+status, null);

        Optional<Reservation> reservation = reservationRepository.findById(result.getBody().getId());

        assertThat(reservation.get().getCurrentStatus()).isEqualTo(StatusEnum.RESERVED.name());

        reservationRepository.deleteById(result.getBody().getId());
    }

    @Test
    void checkAndGetExpiredReservationWithNoExpired() {
        ResponseEntity<ReservationDto[]> reservations = restTemplate.getForEntity(apiURL+"/reservations/expired", ReservationDto[].class);

        assertThat(reservations.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(reservations.getBody().length).isEqualTo(0);
    }

    @Test
    void checkAndGetExpiredReservationWithExpired() {

        Reservation reservation = reservationRepository.findById(89L).get();
        reservation.setCurrentStatus("RESERVED");
        reservation.setCurrentStatusDate(LocalDateTime.now().minusDays(3));

        reservationRepository.save(reservation);

        ResponseEntity<ReservationDto[]> reservations = restTemplate.getForEntity(apiURL+"/reservations/expired", ReservationDto[].class);

        Reservation reservationAfterUpdate = reservationRepository.findById(89L).get();

        assertThat(reservations.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(reservations.getBody().length).isEqualTo(1);
        assertThat(reservationAfterUpdate.getCurrentStatus()).isEqualTo(StatusEnum.CANCELED.name());

        // Putting back to original state
        reservationAfterUpdate.setCurrentStatus(StatusEnum.PENDING.name());
        reservationAfterUpdate.setCurrentStatusDate(LocalDateTime.now());
        reservationRepository.save(reservationAfterUpdate);
    }

}

