package org.alain.library.batch.configuration;

import io.swagger.client.model.LoanDto;
import io.swagger.client.model.ReservationDto;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;

@Service
public class EmailBuilder {

    private TemplateEngine templateEngine;

    public EmailBuilder(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String buildLateLoan(LoanDto loanDto){
        Context context = new Context();
        context.setVariable("loan", loanDto);
        return templateEngine.process("mailTemplateLateLoan", context);
    }

    public String buildLateReservation(ReservationDto reservationDto) {
        Context context = new Context();
        reservationDto.getStatuses().stream()
                .filter(reservationStatus -> reservationStatus.getStatus().equals("RESERVED"))
                .findFirst()
                .ifPresent(status -> context.setVariable("dateStatus", status.getDate()));
        context.setVariable("reservation", reservationDto);
        return templateEngine.process("mailTemplateExpiredReservation", context);
    }

    public String buildFutureLateLoan(List<LoanDto> loanList) {
        Context context = new Context();
        context.setVariable("loans", loanList);
        return templateEngine.process("mailTemplateFutureLateLoan", context);
    }
}
