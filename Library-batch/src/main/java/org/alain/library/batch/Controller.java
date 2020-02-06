package org.alain.library.batch;

import io.swagger.client.api.LoanApi;
import io.swagger.client.model.LoanDto;
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
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class Controller {

    private final LoanApi loanApi;
    private EmailBuilder emailBuilder;
    private JavaMailSender mailSender;

    @Value("${batch.username}")
    private String BATCH_USERNAME;
    @Value("${batch.password}")
    private String BATCH_PASSWORD;

    public Controller(LoanApi loanApi, EmailBuilder emailBuilder, JavaMailSender mailSender) {
        this.loanApi = loanApi;
        this.emailBuilder = emailBuilder;
        this.mailSender = mailSender;
    }

    @Scheduled(cron = "${mailScheduling.delay}" )
    public void getLateLoan() throws IOException, InterruptedException {
        log.info("Launching of batch email");
        String authorisation = "Basic " + Base64.getEncoder().encodeToString((BATCH_USERNAME+":"+BATCH_PASSWORD).getBytes());
        List<LoanDto> loanDtoList = loanApi.checkAndGetLateLoans(authorisation).execute().body();
        if (loanDtoList == null || loanDtoList.isEmpty()){
            log.info("No late loans to send email to");
        }else {
            log.info("Number of email to be sent :" + loanDtoList.size());
            for (LoanDto loan : loanDtoList){
                this.prepareAndSend(loan);
                TimeUnit.SECONDS.sleep(5);
            }
        }
    }

    private void prepareAndSend(LoanDto loanDto){
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom(BATCH_USERNAME);
            messageHelper.setTo(loanDto.getUserEmail());
            messageHelper.setSubject("OpenLibrary.fr : Votre emprunt est en retard !");
            String content = emailBuilder.build(loanDto);
            messageHelper.setText(content, true);
        };
        try{
            log.info("Sending email to :" + loanDto.getUserEmail() + ", loan : " + loanDto.toString());
            mailSender.send(messagePreparator);
        }catch (MailException e){
            log.error("Error while sending email about loan " + loanDto.getId() + "\n"+ e.getMessage());
        }
    }
}
