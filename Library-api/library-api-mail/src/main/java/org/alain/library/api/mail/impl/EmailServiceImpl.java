package org.alain.library.api.mail.impl;


import lombok.extern.slf4j.Slf4j;
import org.alain.library.api.mail.contract.EmailService;
import org.alain.library.api.model.reservation.Reservation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    private EmailBuilder emailBuilder;
    private JavaMailSender mailSender;

    @Value("${email.username}")
    private String EMAIL_USERNAME;
    @Value("${email.password}")
    private String EMAIL_PASSWORD;

    public EmailServiceImpl(EmailBuilder emailBuilder, JavaMailSender mailSender) {
        this.emailBuilder = emailBuilder;
        this.mailSender = mailSender;
    }

    @Override
    public void sendEmailForReservationAvailable(Reservation reservation){
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom(EMAIL_USERNAME);
            messageHelper.setTo(reservation.getUser().getEmail());
            messageHelper.setSubject("OpenLibrary.fr : Votre réservation est disponible !");
            String content = emailBuilder.buildForReservation(reservation);
            messageHelper.setText(content, true);
        };
        try{
            log.info("Sending email to {}, for available reservation {}", reservation.getUser().getEmail(), reservation.getId());
            mailSender.send(messagePreparator);
        }catch (MailException e){
            log.error("Error while sending email to {}, about reservation {} ", reservation.getUser().getEmail(), reservation.getId());
            log.error(e.getMessage());
        }
    }

}
