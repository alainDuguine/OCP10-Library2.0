package org.alain.library.batch;

import io.swagger.client.api.LoanApi;
import io.swagger.client.api.ReservationApi;
import io.swagger.client.model.LoanDto;
import io.swagger.client.model.ReservationDto;
import lombok.extern.slf4j.Slf4j;
import org.alain.library.batch.configuration.EmailBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class Controller {


    private final LoanApi loanApi;
    private final ReservationApi reservationApi;
    private EmailBuilder emailBuilder;
    private JavaMailSender mailSender;
    private String encodedAuthorization;

    @Value("${reservation.notification.days}")
    private int DAYS_NOTIFICATION_RESERVATION;
    @Value("${batch.username}")
    private String BATCH_USERNAME;
    @Value("${batch.password}")
    private String BATCH_PASSWORD;

    public Controller(LoanApi loanApi, ReservationApi reservationApi, EmailBuilder emailBuilder, JavaMailSender mailSender) {
        this.loanApi = loanApi;
        this.reservationApi = reservationApi;
        this.emailBuilder = emailBuilder;
        this.mailSender = mailSender;
    }

//    @Scheduled(cron = "${mailScheduling.delay}" )
    public void getLateLoan() throws IOException, InterruptedException {
        if (encodedAuthorization == null)
            this.encodedAuthorization = getEncodedAuthorization();
        log.info("Launching of batch email for late loans");
        List<LoanDto> loanDtoList = loanApi.checkAndGetLateLoans(encodedAuthorization).execute().body();
        if (loanDtoList == null || loanDtoList.isEmpty()){
            log.info("No late loans to send email to");
        } else {
            log.info("Number of email to be sent :" + loanDtoList.size());
            for (LoanDto loan : loanDtoList){
                this.prepareAndSendLateLoan(loan);
            }
        }
    }

    @Scheduled(cron = "${mailScheduling.delay}" )
    public void getFutureExpiredLoans() throws IOException, InterruptedException {
        if (encodedAuthorization == null)
            this.encodedAuthorization = getEncodedAuthorization();
        log.info("Launching of batch email for future late loans");
        List<LoanDto> loanDtoList = loanApi.checkAndGetFutureLateLoans(encodedAuthorization, DAYS_NOTIFICATION_RESERVATION).execute().body();
        if (loanDtoList == null || loanDtoList.isEmpty()){
            log.info("No future expired loans to send email to");
        } else {
            Map<String, List<LoanDto>> listMap = loanDtoList.stream()
                    .collect(Collectors.groupingBy(LoanDto::getUserEmail));
            log.info("Number of email to be sent :" + listMap.size());
            listMap.forEach(this::prepareAndSendFutureLateLoan);
        }
    }

    @Scheduled(cron = "${mailScheduling.delay}" )
    public void getExpiredReservations() throws IOException, InterruptedException {
        if (encodedAuthorization == null)
            this.encodedAuthorization = getEncodedAuthorization();
        log.info("Launching of batch email for late reservations");
        List<ReservationDto> reservationDtoList = reservationApi.checkAndGetExpiredReservation(encodedAuthorization).execute().body();
        if (reservationDtoList == null || reservationDtoList.isEmpty()){
            log.info("No expired reservations to send email to");
        } else {
            log.info("Number of email to be sent :" + reservationDtoList.size());
            for (ReservationDto reservationDto : reservationDtoList){
                this.prepareAndSendExpiredReservation(reservationDto);
            }
        }
    }

    private void prepareAndSendFutureLateLoan(String emailAddress, List<LoanDto> loanList) {
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom(BATCH_USERNAME);
            messageHelper.setTo(emailAddress);
            messageHelper.setSubject("OpenLibrary.fr : Vos emprunts vont bientôt expirer !");
            String content = emailBuilder.buildFutureLateLoan(loanList);
            messageHelper.setText(content, true);
        };
        try {
            log.info("Sending email to : {}, loans: {}", emailAddress, loanList.size());
            mailSender.send(messagePreparator);
        } catch (MailException e){
            log.error("Error while sending email about future latre loans " + emailAddress + "\n"+ e.getMessage());
        }
    }

    private void prepareAndSendExpiredReservation(ReservationDto reservationDto) {
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom(BATCH_USERNAME);
            messageHelper.setTo(reservationDto.getUserEmail());
            messageHelper.setSubject("OpenLibrary.fr : Votre réservation a expirée !");
            String content = emailBuilder.buildLateReservation(reservationDto);
            messageHelper.setText(content, true);
        };
        try{
            log.info("Sending email to :" + reservationDto.getUserEmail() + ", reservation : " + reservationDto.getId());
            mailSender.send(messagePreparator);
        }catch (MailException e){
            log.error("Error while sending email about reservation " + reservationDto.getId() + "\n"+ e.getMessage());
        }
    }

    private void prepareAndSendLateLoan(LoanDto loanDto){
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom(BATCH_USERNAME);
            messageHelper.setTo(loanDto.getUserEmail());
            messageHelper.setSubject("OpenLibrary.fr : Votre emprunt est en retard !");
            String content = emailBuilder.buildLateLoan(loanDto);
            messageHelper.setText(content, true);
        };
        try{
            log.info("Sending email to :" + loanDto.getUserEmail() + ", loan : " + loanDto.toString());
            mailSender.send(messagePreparator);
        }catch (MailException e){
            log.error("Error while sending email about loan " + loanDto.getId() + "\n"+ e.getMessage());
        }
    }

    private String getEncodedAuthorization(){
        return "Basic " + Base64.getEncoder().encodeToString((BATCH_USERNAME+":"+BATCH_PASSWORD).getBytes());
    }

}
