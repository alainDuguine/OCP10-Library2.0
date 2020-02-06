package org.alain.library.api.service.api;

import lombok.extern.slf4j.Slf4j;
import org.alain.library.api.business.exceptions.UnknownAuthorException;
import org.alain.library.api.model.book.Author;
import org.alain.library.api.model.book.Book;
import org.alain.library.api.model.book.BookCopy;
import org.alain.library.api.model.loan.Loan;
import org.alain.library.api.model.loan.LoanStatus;
import org.alain.library.api.model.user.User;
import org.alain.library.api.service.dto.*;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
class Converters {

    private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/YYYY");
    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/YYYY 'à' HH:mm");

    private Converters() {
    }

/*
    ===============================================================
    ========== AUTHOR =============================================
    ===============================================================
 */

    static AuthorDto convertAuthorModelToAuthorDto(Author author){
        AuthorDto authorDto = new AuthorDto();
        authorDto.setId(author.getId());
        authorDto.setFirstName(author.getFirstName());
        authorDto.setLastName(author.getLastName());
        for (Book book: author.getBooks()) {
            authorDto.addBooksItem(book.getTitle());
        }
        return authorDto;
    }

    static List<AuthorDto> convertListAuthorModelToListAuthorDto(List<Author> authorList) {
        List<AuthorDto> authorDtoList = new ArrayList<>();
        for (Author author:authorList) {
            authorDtoList.add(convertAuthorModelToAuthorDto(author));
        }
        return authorDtoList;
    }

    static Author convertAuthorFormToAuthorModel(AuthorForm author) {
        Author authorModel = new Author();
        authorModel.setFirstName(author.getFirstName());
        authorModel.setLastName(author.getLastName());
        return authorModel;
    }

    static Author convertAuthorDtoToAuthorModel(AuthorDto authorDto) {
        Author authorModel = new Author();
        authorModel.setFirstName(authorDto.getFirstName());
        authorModel.setLastName(authorDto.getLastName());
        return authorModel;
    }

/*
    ===============================================================
    ========== BOOK ===============================================
    ===============================================================
 */

    static BookDto convertBookModelToBookDto(Book bookModel) {
        BookDto bookDto = new BookDto();
        bookDto.setId(bookModel.getId());
        bookDto.setIsbn(bookModel.getIsbn());
        bookDto.setTitle(bookModel.getTitle());
        List<Author> authorsList = new ArrayList<>(bookModel.getAuthors());
        bookDto.setAuthors(convertListAuthorModelToListAuthorDto(authorsList));
        bookDto.setCopiesAvailable(bookModel.getNbCopiesAvailable());
        return bookDto;
    }

    static List<BookDto> convertListBookModelToListBookDto(List<Book> bookList) {
        List<BookDto> bookDtoList = new ArrayList<>();
        for (Book bookModel : bookList) {
            bookDtoList.add(convertBookModelToBookDto(bookModel));
        }
        return bookDtoList;
    }

    static Book convertBookDtoToBookModel(BookDto bookDto) {
        Book bookModel = new Book(bookDto.getTitle());
        try {
            for (AuthorDto bookAuthor : bookDto.getAuthors()) {
                bookModel.addAuthor(convertAuthorDtoToAuthorModel(bookAuthor));
            }
            return bookModel;
        }catch (NullPointerException ex){
            log.warn("No author in book " + ex.getMessage());
            throw new UnknownAuthorException("A book should have at least one author");
        }
    }

    static Book convertBookFormToBookModel(BookForm bookForm) {
        Book bookModel = new Book(bookForm.getTitle());
        try {
            for (BooksAuthors booksAuthors : bookForm.getAuthors()) {
                bookModel.addAuthor(convertBooksAuthorsToAuthorModel(booksAuthors));
            }
            return bookModel;
        }catch (NullPointerException ex){
            log.warn("No author in book " + ex.getMessage());
            throw new UnknownAuthorException("A book should have at least one author");
        }
    }

/*
    ===============================================================
    ========== BOOK COPIE =========================================
    ===============================================================
 */

    static BookCopyDto convertBookCopyModelToBookCopyDto(BookCopy bookCopy) {
        BookCopyDto bookCopyDto = new BookCopyDto();
        bookCopyDto.setId(bookCopy.getId());
        bookCopyDto.setBarcode(bookCopy.getBarcode());
        bookCopyDto.setEditor(bookCopy.getEditor());
        bookCopyDto.setAvailable(bookCopy.isAvailable());
        bookCopyDto.setBook(convertBookModelToBookDto(bookCopy.getBook()));
        return bookCopyDto;
    }

    static List<BookCopyDto> convertListBookCopyModelToListBookCopyDto(List<BookCopy> bookCopyList) {
        List<BookCopyDto> bookCopyDtoList = new ArrayList<>();
        for (BookCopy bookCopy : bookCopyList){
            bookCopyDtoList.add(convertBookCopyModelToBookCopyDto(bookCopy));
        }
        return bookCopyDtoList;
    }

    static BookCopy convertBookCopyFormToBookCopyModel(CopyForm bookCopyForm) {
        BookCopy bookCopy = new BookCopy();
        bookCopy.setEditor(bookCopyForm.getEditor());
        if(bookCopyForm.getBarcode() != null){
            bookCopy.setBarcode(bookCopyForm.getBarcode());
        }
        return bookCopy;
    }

    static BookCopy convertBookCopyDtoToBookCopyModel(BookCopyDto bookCopyDto) {
        BookCopy bookCopyModel = new BookCopy();
        bookCopyModel.setEditor(bookCopyDto.getEditor());
        return bookCopyModel;
    }

    static Author convertBooksAuthorsToAuthorModel(BooksAuthors booksAuthors) {
        Author author = new Author();
        author.setFirstName(booksAuthors.getFirstName());
        author.setLastName(booksAuthors.getLastName());
        return author;
    }

/*
    ===============================================================
    ========== LOAN ===============================================
    ===============================================================
 */

    static LoanDto convertLoanModelToLoanDto(Loan loanModel) {
        LoanDto loanDto = new LoanDto();
        loanDto.setId(loanModel.getId());
        loanDto.startDate(dateFormatter.format(loanModel.getStartDate()));
        loanDto.endDate(dateFormatter.format(loanModel.getEndDate()));
        loanDto.setBookCopy(convertBookCopyModelToBookCopyDto(loanModel.getBookCopy()));
        loanDto.setUserId(loanModel.getUser().getId());
        loanDto.setUserEmail(loanModel.getUser().getEmail());
        loanDto.setCurrentStatus(loanModel.getCurrentStatus());
        loanDto.setCurrentStatusDate(dateTimeFormatter.format(loanModel.getCurrentStatusDate()));
        return loanDto;
    }

    static List<LoanDto> convertListLoanModelToListLoanDto(List<Loan> loanList) {
        List<LoanDto> loanDtoList = new ArrayList<>();
        for (Loan loan : loanList) {
            loanDtoList.add(convertLoanModelToLoanDto(loan));
        }
        return loanDtoList;
    }

/*
    ===============================================================
    ========== LOAN STATUS ========================================
    ===============================================================
 */

    static LoanStatusDto convertLoanStatusModelToLoanStatusDto(LoanStatus loanStatusModel){
        LoanStatusDto loanStatusDto = new LoanStatusDto();
        loanStatusDto.setId(loanStatusModel.getLoan().getId());
        loanStatusDto.setDate(dateFormatter.format(loanStatusModel.getDate()));
        loanStatusDto.setStatus(loanStatusModel.getStatus().getDesignation().name());
        return loanStatusDto;
    }

    static List<LoanStatusDto> convertListLoanStatusModelToListLoanStatusDto(List<LoanStatus> loanStatusListModel) {
        List<LoanStatusDto> loanStatusDtoList = new ArrayList<>();
        for(LoanStatus loanStatus : loanStatusListModel){
            loanStatusDtoList.add(convertLoanStatusModelToLoanStatusDto(loanStatus));
        }
        return loanStatusDtoList;
    }

/*
    ===============================================================
    ========== Users ============================================
    ===============================================================
 */

    static UserDto convertUserModelToUserDto(User userModel) {
        UserDto userDto = new UserDto();
        userDto.setId(userModel.getId());
        userDto.setEmail(userModel.getEmail());
        userDto.setPassword(userModel.getPassword());
        userDto.setFirstName(userModel.getFirstName());
        userDto.setLastName(userModel.getLastName());
        userDto.setRoles(userModel.getRoles());
        userDto.setLoans(convertListLoanModelToListLoanDto(userModel.getLoans()));
        return userDto;
    }

    static List<UserDto> convertListUsersModelToListUsersDto(List<User> listUsers) {
        List<UserDto> listUsersDto = new ArrayList<>();
        listUsers.forEach(user -> listUsersDto.add(convertUserModelToUserDto(user)));
        return listUsersDto;
    }

    static User convertUserFormToUserModel(UserForm userForm) {
        User userModel = new User();
        userModel.setEmail(userForm.getEmail());
        userModel.setPassword(userForm.getPassword());
        userModel.setPasswordConfirmation(userForm.getPasswordConfirmation());
        userModel.setFirstName(userForm.getFirstName());
        userModel.setLastName(userForm.getLastName());
        return userModel;
    }

    static User convertUserFormUpdateToUserModel(UserFormUpdate userFormUpdate) {
        User userModel = new User();
        userModel.setFirstName(userFormUpdate.getFirstName());
        userModel.setLastName(userFormUpdate.getLastName());
        return userModel;
    }
}
