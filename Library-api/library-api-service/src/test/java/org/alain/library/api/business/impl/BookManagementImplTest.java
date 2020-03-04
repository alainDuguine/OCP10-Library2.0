package org.alain.library.api.business.impl;

import org.alain.library.api.consumer.repository.AuthorRepository;
import org.alain.library.api.consumer.repository.BookCopyRepository;
import org.alain.library.api.consumer.repository.BookRepository;
import org.alain.library.api.model.book.Book;
import org.alain.library.api.model.book.BookCopy;
import org.alain.library.api.model.loan.Loan;
import org.alain.library.api.model.loan.StatusDesignation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class BookManagementImplTest {

    @InjectMocks
    BookManagementImpl service;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookCopyRepository bookCopyRepository;
    @Mock
    private AuthorRepository authorRepository;


    @Test
    void getNextReturnDate() {
        Book book = Book.builder().id(1L).build();

        BookCopy copy1 = BookCopy.builder().id(1L).book(book).build();
        BookCopy copy2 = BookCopy.builder().id(2L).book(book).build();
        BookCopy copy3 = BookCopy.builder().id(3L).book(book).build();
        BookCopy copy4 = BookCopy.builder().id(4L).book(book).build();

        Loan loan1 = new Loan(1L, LocalDate.now().minusDays(15), LocalDate.now().plusDays(2), StatusDesignation.LOANED.name(), LocalDateTime.now().minusDays(7),copy1,null, null);
        Loan loan2 = new Loan(2L, LocalDate.now().minusDays(10), LocalDate.now().plusDays(4), StatusDesignation.PROLONGED.name(), LocalDateTime.now().minusDays(6),copy2,null, null);
        Loan loan3 = new Loan(3L, LocalDate.now().minusDays(6), LocalDate.now().plusDays(8), StatusDesignation.LOANED.name(), LocalDateTime.now().minusDays(6),copy3,null, null);
        Loan loan4 = new Loan(4L, LocalDate.now().minusDays(3), LocalDate.now().plusDays(12), StatusDesignation.LOANED.name(), LocalDateTime.now().minusDays(3),copy4,null, null);
        // Inactive loans
        Loan loan5 = new Loan(4L, LocalDate.now().minusDays(25), LocalDate.now().minusDays(12), StatusDesignation.RETURNED.name(), LocalDateTime.now().minusDays(12),copy4,null, null);
        Loan loan6 = new Loan(4L, LocalDate.now().minusDays(32), LocalDate.now().minusDays(19), StatusDesignation.LATE.name(), LocalDateTime.now().minusDays(15),copy4,null, null);

        copy1.addLoan(loan1);
        copy2.addLoan(loan2);
        copy3.addLoan(loan3);
        copy4.addLoan(loan4);
        copy4.addLoan(loan5);
        copy4.addLoan(loan6);

        book.setCopyList(Arrays.asList(copy1,copy2,copy3,copy4));

        given(bookRepository.findById(anyLong())).willReturn(Optional.of(book));
        LocalDate dateResult = service.getNextReturnDate(1L);

        assertThat(dateResult).isEqualTo(loan1.getEndDate());
    }

    @Test
    void getNextReturnDate_withNoLoans() {
        Book book = Book.builder().id(1L).build();

        BookCopy copy1 = BookCopy.builder().id(1L).book(book).build();
        BookCopy copy2 = BookCopy.builder().id(2L).book(book).build();
        BookCopy copy3 = BookCopy.builder().id(3L).book(book).build();
        BookCopy copy4 = BookCopy.builder().id(4L).book(book).build();

        book.setCopyList(Arrays.asList(copy1,copy2,copy3,copy4));

        given(bookRepository.findById(anyLong())).willReturn(Optional.of(book));
        LocalDate dateResult = service.getNextReturnDate(1L);

        assertThat(dateResult).isNull();
    }
}
