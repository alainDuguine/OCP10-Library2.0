package org.alain.library.api.business.contract;

import org.alain.library.api.model.loan.Loan;
import org.alain.library.api.model.loan.LoanStatus;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

public interface LoanManagement extends CrudManagement<Loan> {
    List<Loan> findLoansByStatusAndUserId(String status, Long user);
    List<Loan> findLoansByBookId(Long bookId);
    List<LoanStatus> getLoanStatusList(Long id);
    Loan createNewLoan(Long bookCopyId, Long userId);
    Optional<Loan> updateLoan(Long id, String status);
    Optional<LoanStatus> extendLoan(Long id, Long userId, boolean isAdmin);
    List<Loan> updateAndFindLateLoans();
    List<Loan> findFutureLateLoans(@Valid Integer days);
}
