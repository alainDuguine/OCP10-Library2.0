package org.alain.library.webapp.web;

import io.swagger.client.api.BookApi;
import io.swagger.client.api.LoanApi;
import io.swagger.client.model.BookDto;
import io.swagger.client.model.LoanDto;
import lombok.extern.slf4j.Slf4j;
import org.alain.library.webapp.model.ExtendedBook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.alain.library.webapp.web.ControllersUtilities.*;

@Slf4j
@Controller
public class BookController {

    private final BookApi bookApi;
    private final LoanApi loanApi;

    public BookController(BookApi bookApi, LoanApi loanApi) {
        this.bookApi = bookApi;
        this.loanApi = loanApi;
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
                List<ExtendedBook> extendedBookList = this.getExtendedBookDtoList(bookDtoList);
                for (ExtendedBook book: extendedBookList){
                    if (book.getBookDto().getCopiesAvailable() == 0) {
                        book.setEarliestReturn(getDateNextReturn(session, book.getBookDto()));
                    }
                }
                model.addAttribute("title", title);
                model.addAttribute("author", author);
                model.addAttribute("books", extendedBookList);
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

    private String getDateNextReturn(HttpSession session, BookDto bookDto) throws IOException {
        LocalDate returnDate = null;
        String dateResult = null;
        List<LoanDto> listLoans = loanApi.getLoansByBookId(getEncodedAuthorization(session), bookDto.getId()).execute().body();
        if (listLoans != null) {
            for (LoanDto loan:listLoans){
                LocalDate dateInLoan = LocalDate.parse(loan.getEndDate(), DATE_FORMATTER);
                if (returnDate == null || dateInLoan.isBefore(returnDate)) {
                    returnDate = dateInLoan;
                }
            }
            assert returnDate != null;
            dateResult = DATE_FORMATTER.format(returnDate);
        }
        return dateResult;
    }

    private List<ExtendedBook> getExtendedBookDtoList(List<BookDto> bookDtoList) {
        return bookDtoList.stream()
                .map(ExtendedBook::new)
                .collect(Collectors.toList());
    }

}
