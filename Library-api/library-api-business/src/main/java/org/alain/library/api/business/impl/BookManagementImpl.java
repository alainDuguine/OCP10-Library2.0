package org.alain.library.api.business.impl;

import lombok.extern.slf4j.Slf4j;
import org.alain.library.api.business.contract.BookManagement;
import org.alain.library.api.business.exceptions.UnknownAuthorException;
import org.alain.library.api.business.exceptions.UnknownBookException;
import org.alain.library.api.consumer.repository.AuthorRepository;
import org.alain.library.api.consumer.repository.BookCopyRepository;
import org.alain.library.api.consumer.repository.BookRepository;
import org.alain.library.api.model.book.Author;
import org.alain.library.api.model.book.Book;
import org.alain.library.api.model.book.BookCopy;
import org.alain.library.api.model.loan.Loan;
import org.alain.library.api.model.loan.StatusDesignation;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookManagementImpl extends CrudManagementImpl<Book> implements BookManagement {

    private final BookRepository bookRepository;
    private final BookCopyRepository bookCopyRepository;
    private final AuthorRepository authorRepository;

    public BookManagementImpl(BookRepository bookRepository, BookCopyRepository bookCopyRepository, AuthorRepository authorRepository) {
        super(bookRepository);
        this.bookRepository = bookRepository;
        this.bookCopyRepository = bookCopyRepository;
        this.authorRepository = authorRepository;
    }

    @Override
    public Optional<BookCopy> findCopyInBook(Long bookId, Long copyId) {
        return bookCopyRepository.findOneByIdInBook(bookId, copyId);
    }

    @Override
    public List<BookCopy> findCopiesInBook(Long id) {
        if(bookRepository.findById(id).isPresent()) {
            return bookCopyRepository.findAllByBookId(id);
        }else {
            throw new UnknownBookException("The book doesn't exists");
        }
    }

    @Override
    public void deleteCopyInBook(Long bookId, Long copyId) {
        bookCopyRepository.deleteByIdAndAndBookId(copyId, bookId);
    }

    @Override
    public Optional<Book> saveBook(Book book) {
        try {
            this.manageAuthors(book);
        } catch (UnknownAuthorException e) {
            log.warn("Unknown author exception" + e.getMessage());
            throw new UnknownAuthorException("The author doesn't exist in the database");
        }
        if (bookAlreadyExists(book)) {
            return Optional.empty();
        }else{
            return Optional.of(bookRepository.save(book));
        }
    }

    @Override
    public Optional<Book> updateBook(Long id, Book bookForm) {
        Optional<Book> book = bookRepository.findById(id);
        if (book.isPresent()) {
            try {
                this.manageAuthors(bookForm);
                bookForm.setId(book.get().getId());
            } catch (UnknownAuthorException e) {
                log.warn("Unknown author exception" + e.getMessage());
                throw new UnknownAuthorException("The author doesn't exist in the database");
            }
            if (bookAlreadyExists(bookForm)) {
                return Optional.empty();
            } else {
                book.get().setTitle(bookForm.getTitle());
                book.get().setIsbn(bookForm.getIsbn());
                if(!book.get().getAuthors().equals(bookForm.getAuthors())) {
                    book.get().setAuthors(bookForm.getAuthors());
                }
                return Optional.of(bookRepository.save(book.get()));
            }
        }else{
            throw new UnknownBookException("The book number"+id+"doesn't exists");
        }
    }

    /**
     * This method retrieve the {@link LocalDate}
     * which correspond to the next planned return of a book
     * in a {@link Loan}.
     * All {@link Loan} in the {@link Book} will be retrieved and parsed
     * @param id the id of the {@link Book}
     * @return the next return date
     */
    @Override
    public LocalDate getNextReturnDate(Long id) {
        log.info("Retrieving loans for bookId {}", id);
        Optional<Book> book = bookRepository.findById(id);
        if(book.isPresent()){
            log.info("One book found : {}", book.get().getTitle());
            List<Loan> loans = this.getActiveLoansFromBook(book.get());
            log.info("{} active loans found", loans.size());
            return this.findNextReturnInLoanList(loans);
        }
        throw new UnknownBookException("Book "+id+" doesn't exists");
    }


    @Override
    public List<Book> findByTitleAndAuthor(String title, String author) {
        List<Book> bookList = bookRepository.findByTitleAndAuthor(title.toLowerCase(), author.toLowerCase());
        bookList.forEach(book -> {
            List<Loan> loans = this.getActiveLoansFromBook(book);
            book.setNextReturnDate(this.findNextReturnInLoanList(loans));
        });
        return bookList;
    }

    /**
     * parse a list of {@link Loan}
     * and return the earliest {@link LocalDate} for next return
     * @param loans the {@link Loan} list
     * @return the {@link LocalDate} of next return
     */
    private LocalDate findNextReturnInLoanList(List<Loan> loans) {
        log.info("Retrieving date earliest return from {} loans", loans.size());
        return loans.stream()
                .map(Loan::getEndDate)
                .min(LocalDate::compareTo)
                .orElse(null);
    }

    /**
     * Parse all {@link BookCopy} from {@link Book}
     * and retrieve all {@link Loan} with {@link StatusDesignation}
     * considered as Active
     * @param book {@link Book} element from which to retrieve loans
     * @return Loan arraylist
     */
    private List<Loan> getActiveLoansFromBook(Book book) {
        log.info("Parsing book {} to find all active loans", book.getId());
        List<String> activeStatuses = Arrays.asList(StatusDesignation.LOANED.name(),StatusDesignation.PROLONGED.name());
        log.info("Active statuses : {}", activeStatuses.toString());
        return book.getCopyList()
                .stream()
                .flatMap(bookCopy -> bookCopy.getLoanList().stream())
                .filter(loan -> activeStatuses.contains(loan.getCurrentStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<BookCopy> saveBookCopy(Long id, BookCopy bookCopy) {
        Optional<Book> book = bookRepository.findById(id);
        if(book.isPresent()){
            book.get().addCopy(bookCopy);
            // TODO check reservations list for this book
//            reservationManagement.checkPendingList(book.get());
            return Optional.of(bookCopyRepository.save(bookCopy));
        }
        return Optional.empty();
    }

    @Override
    public Optional<BookCopy> updateBookCopy(Long bookId, Long copyId, BookCopy bookCopyForm) {
        Optional<BookCopy> bookCopy = bookCopyRepository.findOneByIdInBook(bookId, copyId);
        if (bookCopy.isPresent()){
            bookCopy.get().setEditor(bookCopyForm.getEditor());
            return Optional.of(bookCopyRepository.save(bookCopy.get()));
        }
        return Optional.empty();
    }


    /**
     * Check if author exists, extract them as Set, and add association between book and author
     * @param book to check the authors from
     */
    private void manageAuthors(Book book) {
        log.info("Extracting author from bookForm");
        Set<Author> listAuthor = new HashSet<>();
        for (Iterator<Author> it = book.getAuthors().iterator(); it.hasNext();) {
            Author author = it.next();
            log.info("Author : " + author.getFirstName() + " - " +author.getLastName());
            Optional<Author> authorInDb = authorRepository.findByFirstNameAndLastName(author.getFirstName(), author.getLastName());
            if(authorInDb.isPresent()){
                it.remove();
                listAuthor.add(authorInDb.get());
            }else{
                throw new UnknownAuthorException();
            }
        }
        book.setAuthors(listAuthor);
    }

    private boolean bookAlreadyExists(Book book) {
        List<Book> bookList = bookRepository.findByTitle(book.getTitle());
        if (!bookList.isEmpty()) {
            for (Book bookInList : bookList) {
                if(bookInList.getAuthors().equals(book.getAuthors()) && !bookInList.getId().equals(book.getId())){
                    return true;
                }
            }
        }
        return false;
    }

}
