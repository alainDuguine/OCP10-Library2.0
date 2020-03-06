package org.alain.library.api.mail;

import org.alain.library.api.model.reservation.Reservation;
import org.alain.library.api.model.reservation.ReservationStatus;
import org.alain.library.api.model.reservation.StatusEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailBuilder {

    private TemplateEngine templateEngine;

    @Value("${webapp.url}")
    private String webAppUrl;

    public EmailBuilder(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String buildForReservation(Reservation reservation){
        Context context = new Context();
        context.setVariable("reservation", reservation);
        String dateCreation = reservation.getStatuses()
                .stream()
                .filter(reservationStatus -> reservationStatus.getStatus() == StatusEnum.PENDING)
                .map(ReservationStatus::getDate)
                .toString();
        context.setVariable("dateCreation", dateCreation);
        context.setVariable("webapp", webAppUrl);
        return templateEngine.process("reservationMailTemplate", context);
    }
}
