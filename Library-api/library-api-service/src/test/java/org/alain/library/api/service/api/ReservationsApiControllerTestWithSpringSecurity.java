package org.alain.library.api.service.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.alain.library.api.business.contract.ReservationManagement;
import org.alain.library.api.model.book.Author;
import org.alain.library.api.model.book.Book;
import org.alain.library.api.model.reservation.Reservation;
import org.alain.library.api.model.reservation.ReservationStatus;
import org.alain.library.api.model.reservation.StatusEnum;
import org.alain.library.api.model.user.User;
import org.alain.library.api.service.dto.ReservationForm;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@ExtendWith(SpringExtension.class)
//@WebMvcTest(ReservationsApiController.class)
public class ReservationsApiControllerTestWithSpringSecurity {

    @Autowired
    private MockMvc mockMvc;
    @Mock
    ReservationManagement service;
    @Autowired
    ReservationsApiController controller;


    Reservation reservation;
    List<Reservation> reservationList = new ArrayList<>();

    @BeforeEach
    void setUp() {
        ReservationStatus status = ReservationStatus.builder().id(1L).date(LocalDateTime.now()).status(StatusEnum.RESERVED).build();
        Author author = Author.builder().id(1L).firstName("Test firstname").build();
        Book book = Book.builder().id(1L).title("Test Book").build();
        book.addAuthor(author);
        User user = User.builder().id(1L).email("usertest@email.com").firstName("Test firstname").lastName("Test lastname").build();

        reservation = Reservation.builder().id(1L).book(book).user(user).build();
        reservation.addStatus(status);

        ReservationStatus status1 = ReservationStatus.builder().id(2L).date(LocalDateTime.now()).status(StatusEnum.TERMINATED).build();

        Reservation reservation1 = Reservation.builder().id(2L).book(book).user(user).build();
        reservation1.addStatus(status1);

        reservationList.add(reservation);
        reservationList.add(reservation1);

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

//    @WithMockUser(value = "spring")
//    @Test
    void addReservation() throws Exception {
        ReservationForm form = new ReservationForm();
        form.setBookId(1L);
        form.setUserId(1L);
//        given(service.createNewReservation(anyLong(),anyLong(), any())).willReturn(Optional.of(reservation));

        mockMvc.perform(post("/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(form))
                .header("Authorization", "authorization"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.bookId").value(1));
    }

}
