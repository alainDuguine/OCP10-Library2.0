package org.alain.library.api.service.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.alain.library.api.business.contract.ReservationManagement;
import org.alain.library.api.business.impl.UserPrincipal;
import org.alain.library.api.model.book.Author;
import org.alain.library.api.model.book.Book;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ReservationsApiControllerTest {

    @Mock
    ReservationManagement service;
    @Mock
    ObjectMapper objectMapper;
    @Mock
    HttpServletRequest request;
    @Mock
    Converters converter;
    @InjectMocks
    ReservationsApiController controller;

    MockMvc mockMvc;

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

    @Test
    void getReservation() throws Exception {
        given(service.findOne(anyLong())).willReturn(Optional.of(reservation));

        ResultActions result = mockMvc.perform(get("/reservations/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.bookId").value(1))
                .andExpect(jsonPath("$.statuses[0].status", equalTo("RESERVED")));
        String response = result.andReturn().getResponse().getContentAsString();
        System.out.println(response);
        verify(service).findOne(anyLong());
    }

    @Test
    void getReservationStatusNotFoundOnEmptyOptional() throws Exception {
        given(service.findOne(anyLong())).willReturn(Optional.empty());

        mockMvc.perform(get("/reservations/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getReservations() throws Exception {
        given(service.getReservationsByStatusAndUserIdAndBookId(any(), any(), any())).willReturn(reservationList);

        ResultActions result = mockMvc.perform(get("/reservations?status=pending&user=1&book=1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

}
