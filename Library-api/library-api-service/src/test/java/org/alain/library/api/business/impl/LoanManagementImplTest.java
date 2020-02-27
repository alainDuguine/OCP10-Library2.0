package org.alain.library.api.business.impl;

import org.alain.library.api.consumer.repository.BookCopyRepository;
import org.alain.library.api.consumer.repository.LoanRepository;
import org.alain.library.api.consumer.repository.LoanStatusRepository;
import org.alain.library.api.consumer.repository.StatusRepository;
import org.alain.library.api.model.book.Book;
import org.alain.library.api.model.book.BookCopy;
import org.alain.library.api.model.loan.Loan;
import org.alain.library.api.model.loan.LoanStatus;
import org.alain.library.api.model.loan.Status;
import org.alain.library.api.model.loan.StatusDesignation;
import org.alain.library.api.model.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class LoanManagementImplTest {

    @Mock
    private LoanRepository loanRepository;
    @Mock
    private LoanStatusRepository loanStatusRepository;
    @Mock
    private StatusRepository statusRepository;
    @Mock
    private BookCopyRepository bookCopyRepository;

    @InjectMocks
    private LoanManagementImpl loanManagement;

    @Test
    void extendLoan_withExpiredDate_returnEmpty() {
        LocalDateTime dateTime = LocalDateTime.now().minusDays(1);

        Loan loan = new Loan();
        loan.setUser(User.builder().id(1L).build());
        loan.setBookCopy(BookCopy.builder().id(1L).book(Book.builder().id(1L).build()).build());
        loan.setCurrentStatus(StatusDesignation.LATE.name());
        loan.setCurrentStatusDate(dateTime);

        given(loanRepository.findById(anyLong())).willReturn(Optional.of(loan));

        Optional<LoanStatus> status = loanManagement.extendLoan(1L, 1L, true);

        assertThat(status.isPresent()).isFalse();
        assertThat(loan.getCurrentStatus()).isEqualTo(StatusDesignation.LATE.name());
        assertThat(loan.getCurrentStatusDate()).isEqualTo(dateTime);
    }

    @Test
    void extendLoan() {
        LocalDate endDate = LocalDate.now().plusDays(3);
        Loan loan = new Loan();
        loan.setUser(User.builder().id(1L).build());
        loan.setBookCopy(BookCopy.builder().id(1L).book(Book.builder().id(1L).build()).build());
        loan.setCurrentStatus(StatusDesignation.LOANED.name());
        loan.setCurrentStatusDate(LocalDateTime.now().minusDays(1));
        loan.setStartDate(LocalDate.now().minusDays(12));
        loan.setEndDate(endDate);

        Status status = new Status();
        status.setId(3L);
        status.setDesignation(StatusDesignation.PROLONGED);

        given(loanRepository.findById(anyLong())).willReturn(Optional.of(loan));
        given(statusRepository.findStatusByDesignation(any())).willReturn(status);

        Optional<LoanStatus> loanStatus = loanManagement.extendLoan(1L, 1L, true);

        assertThat(loanStatus.isPresent()).isTrue();
        assertThat(loan.getCurrentStatus()).isEqualTo(StatusDesignation.PROLONGED.name());
        assertThat(loan.getEndDate()).isEqualTo(endDate.plusWeeks(4));
    }
}
