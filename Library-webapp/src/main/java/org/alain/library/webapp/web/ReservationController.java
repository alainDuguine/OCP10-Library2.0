package org.alain.library.webapp.web;

import io.swagger.client.api.ReservationApi;
import io.swagger.client.api.UserApi;
import io.swagger.client.model.ReservationDto;
import io.swagger.client.model.ReservationForm;
import io.swagger.client.model.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.alain.library.webapp.business.impl.ReservationManagementImpl;
import org.alain.library.webapp.model.ExtendedReservation;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import retrofit2.Response;

import javax.servlet.http.HttpSession;
import java.util.List;

import static org.alain.library.webapp.WebAppUtilities.*;

@Controller
@Slf4j
public class ReservationController {

    private final ReservationApi reservationApi;
    private final ReservationManagementImpl reservationManagementImpl;
    private final UserApi userApi;

    public ReservationController(ReservationApi reservationApi, ReservationManagementImpl reservationManagementImpl, UserApi userApi) {
        this.reservationApi = reservationApi;
        this.reservationManagementImpl = reservationManagementImpl;
        this.userApi = userApi;
    }


    @GetMapping("/reservations")
    public String reservations(Model model, HttpSession session){
        try {
            String email = (String) session.getAttribute(EMAIL_FIELD);
            if(email != null){
                log.info("Reservations recuperation : {}", email);
                List<ReservationDto> reservationDtoList = reservationApi.getReservationByUser(getEncodedAuthorization(session)).execute().body();
                if(reservationDtoList != null) {
                    List<ExtendedReservation> extendedReservationList = reservationManagementImpl.getExtendedReservations(reservationDtoList);
                    model.addAttribute("reservations", extendedReservationList);
                }
            }else{
                return REDIRECT_LOGIN;
            }
        }catch (Exception ex){
            log.error(CONNEXION_FAILED);
            log.error(ex.getMessage());
            return CONNEXION_FAILED;
        }
        return "reservations";
    }

    @GetMapping("/reservations/cancel")
    public String cancelReservation(@RequestParam Long id, HttpSession session) {
        try {
            log.info("Request for canceling reservation {}", id);
            if (session.getAttribute(EMAIL_FIELD) != null) {
                boolean result = reservationApi.updateReservation(getEncodedAuthorization(session), id, "CANCELED").execute().isSuccessful();
                return "redirect:/reservations?id=" + id + "&canceled=" + result;
            } else {
                return REDIRECT_LOGIN;
            }
        } catch (Exception ex) {
            log.error(CONNEXION_FAILED);
            return CONNEXION_FAILED;
        }
    }

    @GetMapping("/reservations/add")
    public ModelAndView addReservation(@RequestParam(name = "bookId") long bookId, HttpSession session, ModelMap model) {
        try {
            String email = session.getAttribute(EMAIL_FIELD).toString();
            log.info("Request for adding reservation on book {}, user {}", bookId, email);
            UserDto user = userApi.getUserByEmail(email, getEncodedAuthorization(session)).execute().body();
                if (user != null) {
                    log.info("Requesting user {}", user.getId());
                    ReservationForm reservationForm = new ReservationForm();
                    reservationForm.setBookId(bookId);
                    reservationForm.setUserId(user.getId());
                    log.info("Creating reservationForm {}", reservationForm.toString());
                    Response<ReservationDto> response = reservationApi.addReservation(getEncodedAuthorization(session), reservationForm).execute();
                    if (response.isSuccessful()) {
                        log.info("Reservation {} created for book {} and user {}", response.body().getId(), bookId, email);
                        return new ModelAndView("redirect:/reservations?id=" + response.body().getId() + "&created=true");
                    } else {
                        log.warn("Reservation not created for book {} and user {}", bookId, email);
                        model.addAttribute("error", response.errorBody().string());
                        return new ModelAndView("redirect:/search", model);
                    }
                } else {
                return new ModelAndView("redirect:/login");
            }
        } catch (Exception e) {
            log.error(CONNEXION_FAILED);
            return new ModelAndView(CONNEXION_FAILED);
        }
    }
}
