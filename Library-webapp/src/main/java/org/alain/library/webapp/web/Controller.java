package org.alain.library.webapp.web;

import io.swagger.client.api.BookApi;
import io.swagger.client.api.LoanApi;
import io.swagger.client.api.UserApi;
import io.swagger.client.model.BookDto;
import io.swagger.client.model.UserCredentials;
import io.swagger.client.model.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Base64;
import java.util.List;


@org.springframework.stereotype.Controller
@RequestMapping("/")
@Slf4j
public class Controller {

    private final BookApi bookApi;
    private final UserApi userApi;
    private final LoanApi loanApi;

    private static String EMAIL_FIELD = "email";
    private static String PASSWORD_FIELD = "password";
    private static  String REDIRECT_LOGIN = "redirect:/login";
    private static  String CONNEXION_FAILED = "Connexion failed";

    public Controller(BookApi bookApi, UserApi userApi, LoanApi loanApi) {
        this.bookApi = bookApi;
        this.userApi = userApi;
        this.loanApi = loanApi;
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
//                if (rememberMe != null){
//                    Cookie cookie = new Cookie("username", username);
//                    cookie.setMaxAge(30 * 24 * 60 * 60);
//                    cookie.setHttpOnly(true);
//                }
                return "redirect:/loans";
            }
        }catch (Exception ex){
            log.error(CONNEXION_FAILED);
            return CONNEXION_FAILED;
        }
        return "redirect:/login?error";
    }

    @GetMapping("/loans")
    public String loans(Model model, HttpSession session){
        try {
            String email = (String) session.getAttribute(EMAIL_FIELD);
            if(email != null){
                log.info("Loans recuperation : " + email);
                UserDto user = userApi.getUserByEmail(email, getEncodedAuthorization(session)).execute().body();
                assert user != null;
                model.addAttribute("loanList", user.getLoans());
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

    @GetMapping({"/search", "/books"})
    public String books(Model model,
                        HttpSession session,
                        @RequestParam(name = "title", defaultValue = "") String title,
                        @RequestParam(name = "author", defaultValue = "") String author){
        try {
            log.info("Displaying books : Title - " + title + ", author - " + author);
            if(session.getAttribute(EMAIL_FIELD) != null) {
                List<BookDto> bookDtoList = bookApi.getBooks(title, author).execute().body();
                assert bookDtoList != null;
                log.info("Book list :" + bookDtoList.size());
                model.addAttribute("title", title);
                model.addAttribute("author", author);
                model.addAttribute("books", bookDtoList);
            }else{
                return REDIRECT_LOGIN;
            }
        }catch (Exception ex){
            log.error(CONNEXION_FAILED);
            return CONNEXION_FAILED;
        }
        return "search";
    }

    private String getEncodedAuthorization(HttpSession session){
        String email = (String) session.getAttribute(EMAIL_FIELD);
        String password = (String) session.getAttribute(PASSWORD_FIELD);
        return "Basic " + Base64.getEncoder().encodeToString((email + ":" + password).getBytes());
    }
}
