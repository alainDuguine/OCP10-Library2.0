package org.alain.library.api.business.impl;

import lombok.extern.slf4j.Slf4j;
import org.alain.library.api.business.contract.LoanManagement;
import org.alain.library.api.business.exceptions.*;
import org.alain.library.api.consumer.repository.BookCopyRepository;
import org.alain.library.api.consumer.repository.LoanRepository;
import org.alain.library.api.consumer.repository.LoanStatusRepository;
import org.alain.library.api.consumer.repository.StatusRepository;
import org.alain.library.api.model.book.BookCopy;
import org.alain.library.api.model.loan.Loan;
import org.alain.library.api.model.loan.LoanStatus;
import org.alain.library.api.model.loan.Status;
import org.alain.library.api.model.loan.StatusDesignation;
import org.alain.library.api.model.user.User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class LoanManagementImpl extends CrudManagementImpl<Loan> implements LoanManagement {

    private final LoanRepository loanRepository;
    private final LoanStatusRepository loanStatusRepository;
    private final StatusRepository statusRepository;
    private final BookCopyRepository bookCopyRepository;

    public LoanManagementImpl(LoanRepository loanRepository, LoanStatusRepository loanStatusRepository, StatusRepository statusRepository, BookCopyRepository bookCopyRepository) {
        super(loanRepository);
        this.loanRepository = loanRepository;
        this.loanStatusRepository = loanStatusRepository;
        this.statusRepository = statusRepository;
        this.bookCopyRepository = bookCopyRepository;
    }

    @Override
    public List<Loan> findLoansByStatusAndUserId(String status, Long userId) {
        if(status!=null)
            status = status.toUpperCase();
        return loanRepository.findByCurrentStatusAndUserId(status, userId);
    }

    @Override
    public List<LoanStatus> getLoanStatusList(Long id) {
        if(loanRepository.findById(id).isPresent()) {
            return loanStatusRepository.findAllByLoanIdOrderByDateDesc(id);
        }else{
            throw new UnknownLoanException("Unknown loan "+id);
        }
    }

    @Override
    public Loan createNewLoan(Long bookCopyId, Long userId) {
        Optional<BookCopy> bookCopy = bookCopyRepository.findById(bookCopyId);
        if (bookCopy.isPresent()){
            if (!bookCopy.get().isAvailable()) {
                throw new BookCopyNotAvailableException("Book Copy Unavailable "+bookCopyId);
            }
            Loan loan = new Loan();
            loan.setBookCopy(bookCopy.get());
            bookCopy.get().setAvailable(false);
            loan.setUser(User.builder().id(userId).build());
            loan.setStartDate(LocalDate.now());
            loan.setEndDate(LocalDate.now().plusWeeks(4));
            try {
                this.addLoanStatusToLoan(loan, StatusDesignation.LOANED);
            }catch (Exception e) {
                log.warn("Wrong parameter on createNewLoan : " + userId + e.getMessage());
                throw new UnknownParameterException(String.format("Cannot create loan for bookCopy %d and user %d", bookCopyId, userId), e);
            }
            return loan;
        }else{
            throw new UnknownBookCopyException("Unknown bookCopy "+bookCopyId);
        }
    }

    @Override
    public Optional<Loan> updateLoan(Long id, String status) {
        try {
            Optional<Loan> loan = loanRepository.findById(id);
            StatusDesignation statusDesignation = StatusDesignation.valueOf(status.toUpperCase());
            loan.ifPresent(value -> this.addLoanStatusToLoan(value, statusDesignation));
            return loan;
        }catch(IllegalArgumentException ex){
            log.warn("Wrong status on updateLoan : " + status + " - " + ex.getMessage());
            throw new UnknowStatusException("Unknown status "+status);
        }

    }

    private LoanStatus addLoanStatusToLoan(Loan loan, StatusDesignation statusDesignation) {
        Status status = statusRepository.findStatusByDesignation(statusDesignation);
        if(statusDesignation == StatusDesignation.RETURNED){
            loan.getBookCopy().setAvailable(true);
//   TODO         reservationManagement.checkPendingListAndNotify(loan.getBookCopy().getBook().getId());
        }
        loan.setCurrentStatus(status.getDesignation().toString());
        loan.setCurrentStatusDate(LocalDateTime.now());
        LoanStatus loanStatus = loan.addLoanStatus(status);
        loanRepository.save(loan);
        return loanStatus;
    }

    @Override
    public Optional<LoanStatus> extendLoan(Long id, Long userId, boolean isAdmin) {
        Optional<Loan> loan = loanRepository.findById(id);
        if (loan.isPresent()){
            if( isAdmin || userId.equals(loan.get().getUser().getId())) {
                if (loan.get().getCurrentStatus().equals(StatusDesignation.LOANED.name())) {
                    loan.get().setEndDate(loan.get().getEndDate().plusWeeks(4));
                    return Optional.of(this.addLoanStatusToLoan(loan.get(), StatusDesignation.PROLONGED));
                } else {
                    return Optional.empty();
                }
            }else{
                throw new UnauthorizedException("Impossible to extend loan");
            }
        }else{
            throw new UnknownLoanException("Unknown loan "+id);
        }
    }

    @Override
    public List<Loan> updateAndFindLateLoans() {
        List<Loan> loanList = loanRepository.findLateLoans();
        for (Loan loan : loanList){
            if(!loan.getCurrentStatus().equals("LATE")){
                this.addLoanStatusToLoan(loan, StatusDesignation.valueOf("LATE"));
            }
        }
        return loanList;
    }

    /**
     * Returns loans which will end within the LocalDate.now() + days (including last day)
     * @param days the value of days added to LocalDate.now()
     * @return the list of loans who will expired within this date limit
     */
    @Override
    public List<Loan> findFutureLateLoans(Integer days) {
        LocalDate futureExpiration = LocalDate.now().plusDays(days);
        List<Loan> loanList = loanRepository.findFutureLateLoans(futureExpiration);
        log.info("Retrieving {} loan that will expire within {}", loanList.size(), futureExpiration);
        return loanList;
    }

    @Override
    public List<Loan> findLoansByBookId(Long bookId) {
        log.info("Service : Retrieving loans for bookId {}", bookId);
        return loanRepository.findByBookId(bookId);
    }
}
