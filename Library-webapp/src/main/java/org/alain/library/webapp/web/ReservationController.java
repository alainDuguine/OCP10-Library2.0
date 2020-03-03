package org.alain.library.webapp.web;

import io.swagger.client.api.ReservationApi;
import io.swagger.client.api.UserApi;
import io.swagger.client.model.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.alain.library.webapp.business.ReservationManagement;
import org.alain.library.webapp.model.ExtendedReservation;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.util.List;

import static org.alain.library.webapp.WebAppUtilities.*;

@Controller
@Slf4j
public class ReservationController {

    private final ReservationApi reservationApi;
    private final UserApi userApi;
    private final ReservationManagement reservationManagement;

    public ReservationController(ReservationApi reservationApi, UserApi userApi, ReservationManagement reservationManagement) {
        this.reservationApi = reservationApi;
        this.userApi = userApi;
        this.reservationManagement = reservationManagement;
    }

    @GetMapping("/reservations")
    public String reservations(Model model, HttpSession session){
        try {
            String email = (String) session.getAttribute(EMAIL_FIELD);
            if(email != null){
                log.info("Reservations recuperation : {}", email);
                UserDto user = userApi.getUserByEmail(email, getEncodedAuthorization(session)).execute().body();
                List<ExtendedReservation> extendedReservationList = reservationManagement.getExtendedReservations(session, user.getReservations());
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
}
