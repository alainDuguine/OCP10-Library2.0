package org.alain.library.webapp.web;

import io.swagger.client.api.LoanApi;
import io.swagger.client.api.UserApi;
import io.swagger.client.model.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

import static org.alain.library.webapp.WebAppUtilities.*;

@Controller
@Slf4j
public class LoanController {

    private final UserApi userApi;
    private final LoanApi loanApi;

    public LoanController(UserApi userApi, LoanApi loanApi) {
        this.userApi = userApi;
        this.loanApi = loanApi;
    }

    @GetMapping("/loans")
    public String loans(Model model, HttpSession session){
        try {
            String email = (String) session.getAttribute(EMAIL_FIELD);
            if(email != null){
                log.info("Loans recuperation : " + email);
                UserDto user = userApi.getUserByEmail(email, getEncodedAuthorization(session)).execute().body();
                assert user != null;
                model.addAttribute("loans", user.getLoans());
                return "loans";
            }else{
                return REDIRECT_LOGIN;
            }
        }catch (Exception ex){
            log.error(CONNEXION_FAILED);
            return CONNEXION_FAILED;
        }
    }

    @GetMapping("/extend")
    public String extend(@RequestParam(name = "loanId") long loanId, HttpSession session){
        try{
            if(session.getAttribute(EMAIL_FIELD) != null){
                log.info("Loan extension  : " + loanId + ", user :" + session.getAttribute(EMAIL_FIELD));
                if(loanApi.extendLoan(loanId, getEncodedAuthorization(session)).execute().code() == 200){
                    return "redirect:/loans?success&loanId="+loanId;
                }else{
                    log.info("Extension refused  : " + loanId + ", user :" + session.getAttribute(EMAIL_FIELD));
                    return "redirect:/loans?error&loanId="+loanId;
                }
            }else{
                return "redirect:/login";
            }
        } catch (Exception e) {
            log.error(CONNEXION_FAILED);
            return CONNEXION_FAILED;
        }
    }

}
