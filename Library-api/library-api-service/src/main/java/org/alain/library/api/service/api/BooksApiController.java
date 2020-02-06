package org.alain.library.api.service.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.alain.library.api.business.contract.BookManagement;
import org.alain.library.api.business.exceptions.UnknownAuthorException;
import org.alain.library.api.business.exceptions.UnknownBookException;
import org.alain.library.api.model.book.Book;
import org.alain.library.api.model.book.BookCopy;
import org.alain.library.api.service.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

import static org.alain.library.api.service.api.Converters.*;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2019-11-01T07:19:44.790+01:00")

@Controller
@Slf4j
public class BooksApiController implements BooksApi {

    private final ObjectMapper objectMapper;
    private final BookManagement bookManagement;
    private final HttpServletRequest request;

    @org.springframework.beans.factory.annotation.Autowired
    public BooksApiController(ObjectMapper objectMapper, BookManagement bookManagement, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.bookManagement = bookManagement;
        this.request = request;
    }

    public ResponseEntity<BookDto> getBook(@ApiParam(value = "Id of book to return",required=true) @PathVariable("id") Long id) {
        log.info("Getting book " + id);
        Optional<Book> book = bookManagement.findOne(id);
        if (book.isPresent()){
            return new ResponseEntity<BookDto>(convertBookModelToBookDto(book.get()),HttpStatus.OK);
        }
        log.info("Unknown book " + id);
        return new ResponseEntity<BookDto>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<List<BookDto>> getBooks(@ApiParam(value = "Title of book to return", defaultValue = "") @Valid @RequestParam(value = "title", required = false, defaultValue="") String title,
                                                  @ApiParam(value = "author of book to return", defaultValue = "") @Valid @RequestParam(value = "author", required = false, defaultValue="") String author) {
        List<Book> bookList = bookManagement.findByTitleAndAuthor(title,author);
        log.info("Getting list books : " + bookList.size());
        return new ResponseEntity<List<BookDto>>(convertListBookModelToListBookDto(bookList), HttpStatus.OK);
    }

    public ResponseEntity<BookCopyDto> getCopy(@ApiParam(value = "Book id concerned by the copy",required=true) @PathVariable("bookId") Long bookId,
                                               @ApiParam(value = "BookCopy id to get",required=true) @PathVariable("copyId") Long copyId) {
        log.info("Getting book copy : " + copyId);
        Optional<BookCopy> bookCopy = bookManagement.findCopyInBook(bookId, copyId);
        if (bookCopy.isPresent()){
            return new ResponseEntity<BookCopyDto>(convertBookCopyModelToBookCopyDto(bookCopy.get()),HttpStatus.OK);
        }
        log.warn("Unknown book copy : " + copyId);
        return new ResponseEntity<BookCopyDto>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<List<BookCopyDto>> getCopies(@ApiParam(value = "Id of book to find copies from",required=true) @PathVariable("id") Long id) {
        try {
            List<BookCopy> bookCopyList = bookManagement.findCopiesInBook(id);
            log.info("Getting list books copies : " + bookCopyList.size() +" book n° " + id);
            return new ResponseEntity<List<BookCopyDto>>(convertListBookCopyModelToListBookCopyDto(bookCopyList), HttpStatus.OK);
        }catch (UnknownBookException ex){
            log.warn("Unknown book " + id);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    public ResponseEntity<Void> deleteBook(@ApiParam(value = "Book id to delete",required=true) @PathVariable("id") Long id) {
        log.info("Deleting book : " + id);
        bookManagement.delete(id);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    public ResponseEntity<Void> deleteBookCopy(@ApiParam(value = "Book id to delete a copy from",required=true) @PathVariable("bookId") Long bookId,
                                               @ApiParam(value = "BookCopy id to delete",required=true) @PathVariable("copyId") Long copyId) {
        log.info("Deleting book copy : " + copyId + " book : " + bookId);
        bookManagement.deleteCopyInBook(bookId, copyId);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    public ResponseEntity<BookDto> addBook(@ApiParam(value = "Book object that needs to be added to the database" ,required=true ) @Valid @RequestBody BookForm bookForm) {
        try {
            log.info("Creating new book");
            Optional<Book> bookModel = bookManagement.saveBook(convertBookFormToBookModel(bookForm));
            if(bookModel.isPresent()){
                log.info("New book created " + bookModel.get().getId());
                return new ResponseEntity<BookDto>(convertBookModelToBookDto(bookModel.get()), HttpStatus.OK);
            }
            log.warn("Book already exists " + bookForm.getTitle());
            return new ResponseEntity<BookDto>(HttpStatus.CONFLICT);
        }catch(UnknownAuthorException ex){
            log.warn("Book author doesn't exists " + bookForm.getAuthors().toString());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    public ResponseEntity<BookCopyDto> addBookCopy(@ApiParam(value = "Id of book to find copies from",required=true) @PathVariable("id") Long id,
                                                   @ApiParam(value = "Book copy object that needs to be added to the database" ,required=true )  @Valid @RequestBody CopyForm copyForm) {
        log.info("Creating new book copy for book : " + id);
        Optional<BookCopy> bookCopyModel = bookManagement.saveBookCopy(id, convertBookCopyFormToBookCopyModel(copyForm));
        if(bookCopyModel.isPresent()){
            log.info("New book copy created " + bookCopyModel.get().getId());
            return new ResponseEntity<BookCopyDto>(convertBookCopyModelToBookCopyDto(bookCopyModel.get()), HttpStatus.OK);
        }
        log.warn("Book doesn't exists " + id);
        return new ResponseEntity<BookCopyDto>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<BookDto> updateBook(@ApiParam(value = "Book id to update",required=true) @PathVariable("id") Long id,
                                              @ApiParam(value = "Book object updated" ,required=true )  @Valid @RequestBody BookDto book) {
        try {
            log.info("Update book : " + id);
            Optional<Book> bookModel = bookManagement.updateBook(id, convertBookDtoToBookModel(book));
            if (bookModel.isPresent()) {
                log.info("Updated book : " + bookModel.get().getId());
                return new ResponseEntity<BookDto>(convertBookModelToBookDto(bookModel.get()), HttpStatus.OK);
            }
            log.warn("Book already exists " + book.getTitle() + " " + book.getAuthors().toString());
            return new ResponseEntity<BookDto>(HttpStatus.CONFLICT);
        }catch (Exception ex){
            log.warn("Impossible to update book " + id);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    public ResponseEntity<BookCopyDto> updateBookCopy(@ApiParam(value = "Book id to update copy from",required=true) @PathVariable("bookId") Long bookId,
                                                      @ApiParam(value = "BookCopy id to update",required=true) @PathVariable("copyId") Long copyId,
                                                      @ApiParam(value = "Book copy object updated" ,required=true )  @Valid @RequestBody BookCopyDto copy) {
        log.info("Update bookcopy : " + copyId + ", book : " + bookId);
        Optional<BookCopy> bookCopyModel = bookManagement.updateBookCopy(bookId, copyId, convertBookCopyDtoToBookCopyModel(copy));
        if (bookCopyModel.isPresent()) {
            log.info("Updated book : " + bookCopyModel.get().getId());
            return new ResponseEntity<BookCopyDto>(convertBookCopyModelToBookCopyDto(bookCopyModel.get()), HttpStatus.OK);
        }
        log.warn("Unknown bookcopy : " + copyId + ", book : " + bookId);
        return new ResponseEntity<BookCopyDto>(HttpStatus.NOT_FOUND);
    }

}
