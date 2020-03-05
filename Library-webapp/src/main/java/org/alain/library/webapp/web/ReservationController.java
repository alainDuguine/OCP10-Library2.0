package org.alain.library.webapp.web;

import io.swagger.client.api.ReservationApi;
import io.swagger.client.model.ReservationDto;
import lombok.extern.slf4j.Slf4j;
import org.alain.library.webapp.business.ReservationManagement;
import org.alain.library.webapp.model.ExtendedReservation;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;

import static org.alain.library.webapp.WebAppUtilities.*;

@Controller
@Slf4j
public class ReservationController {

    private final ReservationApi reservationApi;
    private final ReservationManagement reservationManagement;

    public ReservationController(ReservationApi reservationApi, ReservationManagement reservationManagement) {
        this.reservationApi = reservationApi;
        this.reservationManagement = reservationManagement;
    }


    @GetMapping("/reservations")
    public String reservations(Model model, HttpSession session,
                               @RequestParam (required = false) Long id,
                               @RequestParam (required = false) boolean success){
        try {
            String email = (String) session.getAttribute(EMAIL_FIELD);
            if(email != null){
                log.info("Reservations recuperation : {}", email);
                List<ReservationDto> reservationDtoList = reservationApi.getReservationByUser(getEncodedAuthorization(session)).execute().body();
                List<ExtendedReservation> extendedReservationList = reservationManagement.getExtendedReservations(reservationDtoList);
                model.addAttribute("reservations", extendedReservationList);
                return "reservations";
            }else{
                return REDIRECT_LOGIN;
            }
        }catch (Exception ex){
            log.error(CONNEXION_FAILED);
            return CONNEXION_FAILED;
        }
    }

    @GetMapping("/reservations/cancel")
    public String cancelReservation(@RequestParam Long id, HttpSession session){
        try{
            log.info("Request for canceling reservation {}", id);
            if(session.getAttribute(EMAIL_FIELD) != null){
                boolean result = reservationApi.updateReservation(getEncodedAuthorization(session), id, "CANCELED").execute().isSuccessful();
                return "redirect:/reservations?id="+id+"&success="+result;
            }else{
                return REDIRECT_LOGIN;
            }
        }catch (Exception ex){
            log.error(CONNEXION_FAILED);
            return CONNEXION_FAILED;
            }
        }
}
