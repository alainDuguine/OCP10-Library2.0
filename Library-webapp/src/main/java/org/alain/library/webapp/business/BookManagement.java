package org.alain.library.webapp.business;

import io.swagger.client.api.BookApi;
import io.swagger.client.api.LoanApi;
import io.swagger.client.model.BookDto;
import io.swagger.client.model.LoanDto;
import lombok.extern.slf4j.Slf4j;
import org.alain.library.webapp.model.ExtendedBook;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.alain.library.webapp.WebAppUtilities.DATE_FORMATTER;
import static org.alain.library.webapp.WebAppUtilities.getEncodedAuthorization;

@Service
@Slf4j
public class BookManagement {

    private final BookApi bookApi;
    private final LoanApi loanApi;

    public BookManagement(BookApi bookApi, LoanApi loanApi) {
        this.bookApi = bookApi;
        this.loanApi = loanApi;
    }

    public List<ExtendedBook> getExtendedBookDtoList(List<BookDto> bookDtoList) {
        log.info("Converting bookDto to ExtendedBook : {} books", bookDtoList.size());
        return bookDtoList.stream()
                .map(ExtendedBook::new)
                .collect(Collectors.toList());
    }

    public BookDto getBookDto(Long bookId) throws IOException {
        return bookApi.getBook(bookId).execute().body();
    }

    public String getDateNextReturn(HttpSession session, BookDto bookDto) throws IOException {
        LocalDate returnDate = null;
        String dateResult = null;
        log.info("Retrieving loans for bookId {}", bookDto.getId());
        List<LoanDto> listLoans = loanApi.getLoansByBookId(getEncodedAuthorization(session), bookDto.getId()).execute().body();
        if (listLoans != null) {
            for (LoanDto loan:listLoans){
                LocalDate dateInLoan = LocalDate.parse(loan.getEndDate(), DATE_FORMATTER);
                if (returnDate == null || dateInLoan.isBefore(returnDate)) {
                    returnDate = dateInLoan;
                }
            }
            log.info("Retrieving date earliest return : {}", returnDate);
            dateResult = DATE_FORMATTER.format(Objects.requireNonNull(returnDate));
        }
        return dateResult;
    }

}
