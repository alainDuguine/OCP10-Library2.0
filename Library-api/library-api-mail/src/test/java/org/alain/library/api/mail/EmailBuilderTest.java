package org.alain.library.api.mail;

import org.alain.library.api.model.reservation.Reservation;
import org.alain.library.api.model.reservation.ReservationStatus;
import org.alain.library.api.model.reservation.StatusEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(MockitoExtension.class)
class EmailBuilderTest {

    @Mock
    TemplateEngine templateEngine;
    @Spy
    Context context;
    @InjectMocks
    EmailBuilder emailBuilder;

    @Test
    void buildForReservation() {
        ReservationStatus status1 = ReservationStatus.builder().id(1L).status(StatusEnum.PENDING).date(LocalDateTime.now().minusDays(1)).build();
        ReservationStatus status2 = ReservationStatus.builder().id(2L).status(StatusEnum.RESERVED).date(LocalDateTime.now()).build();
        Reservation reservation = Reservation.builder().id(1L).statuses(Arrays.asList(status1,status2)).build();

        emailBuilder.buildForReservation(reservation);

//        assertThat(context.getVariable("webapp")).isEqualTo("http://localhost:8081/");
        assertThat(context.getVariable("dateCreation")).isEqualTo(status1.getDate());
    }
}
