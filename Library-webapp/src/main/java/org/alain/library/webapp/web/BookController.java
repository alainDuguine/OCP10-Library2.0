package org.alain.library.webapp.web;

import io.swagger.client.api.BookApi;
import io.swagger.client.model.BookDto;
import lombok.extern.slf4j.Slf4j;
import org.alain.library.webapp.business.impl.BookManagementImpl;
import org.alain.library.webapp.model.ExtendedBook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;

import static org.alain.library.webapp.WebAppUtilities.*;

@Slf4j
@Controller
public class BookController {

    private final BookApi bookApi;
    private final BookManagementImpl bookManagementImpl;

    public BookController(BookApi bookApi, BookManagementImpl bookManagementImpl) {
        this.bookApi = bookApi;
        this.bookManagementImpl = bookManagementImpl;
    }

    @GetMapping({"/search", "/books"})
    public String books(Model model,
                        HttpSession session,
                        @RequestParam(name = "title", defaultValue = "") String title,
                        @RequestParam(name = "author", defaultValue = "") String author){
        try {
            log.info("Displaying books : Title - " + title + ", author - " + author);
            if(session.getAttribute(EMAIL_FIELD) != null) {
                log.info("Retrieving bookList :");
                List<BookDto> bookDtoList = bookApi.getBooks(title, author).execute().body();
                if(bookDtoList != null) {
                    log.info("Book list : {}", bookDtoList.size());
                    List<ExtendedBook> extendedBookList = bookManagementImpl.getExtendedBookDtoList(bookDtoList);
                    model.addAttribute("title", title);
                    model.addAttribute("author", author);
                    model.addAttribute("books", extendedBookList);
                }
            }else{
                return REDIRECT_LOGIN;
            }
        }catch (Exception ex){
            log.error(CONNEXION_FAILED);
            log.error(ex.getMessage());
            return CONNEXION_FAILED;
        }
        return "search";
    }
}
