package org.alain.library.webapp.web;

import lombok.Getter;

import javax.servlet.http.HttpSession;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Getter
public class ControllersUtilities {

    protected static final String EMAIL_FIELD = "email";
    protected static final String PASSWORD_FIELD = "password";
    protected static final String REDIRECT_LOGIN = "redirect:/login";
    protected static final String CONNEXION_FAILED = "Connexion failed";
    protected static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private ControllersUtilities() {
    }

    protected static String getEncodedAuthorization(HttpSession session){
        String email = (String) session.getAttribute(EMAIL_FIELD);
        String password = (String) session.getAttribute(PASSWORD_FIELD);
        return "Basic " + Base64.getEncoder().encodeToString((email + ":" + password).getBytes());
    }
}
