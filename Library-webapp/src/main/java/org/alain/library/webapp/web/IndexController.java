package org.alain.library.webapp.web;

import io.swagger.client.api.UserApi;
import io.swagger.client.model.UserCredentials;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

import static org.alain.library.webapp.WebAppUtilities.*;


@Controller
@RequestMapping("/")
@Slf4j
public class IndexController {

    private final UserApi userApi;

    public IndexController(UserApi userApi) {
        this.userApi = userApi;
    }

    @GetMapping("/")
    public String def(){return REDIRECT_LOGIN;}

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session){
        log.info("Logout : " + session.getAttribute(EMAIL_FIELD));
        session.invalidate();
        return "redirect:/login?logout";
    }

    @PostMapping("/signin")
    public String signin(HttpSession session,
                         @RequestParam(name = "username")String username,
                         @RequestParam(name = "password")String password,
                         @RequestParam(name="rememberMe", required = false)String rememberMe){
        try{
            UserCredentials userCredentials = new UserCredentials();
            userCredentials.setEmail(username);
            userCredentials.setPassword(password);
            log.info("Login : " + username);
            if(userApi.login(userCredentials).execute().code() == 200){
                session.setAttribute(EMAIL_FIELD, username);
                session.setAttribute(PASSWORD_FIELD, password);
                return "redirect:/loans";
            }
        }catch (Exception ex){
            log.error(CONNEXION_FAILED);
            return CONNEXION_FAILED;
        }
        return "redirect:/login?error";
    }
}
