package org.alain.library.api.business.contract;

import org.alain.library.api.model.loan.Loan;
import org.alain.library.api.model.loan.LoanStatus;

import java.util.List;
import java.util.Optional;

public interface LoanManagement extends CrudManagement<Loan> {
    List<Loan> findLoansByStatusAndUserId(String status, Long user);
    List<LoanStatus> getLoanStatusList(Long id);
    Loan createNewLoan(Long bookCopyId, Long userId);
    Optional<LoanStatus> updateLoan(Long id, String status);
    Optional<LoanStatus> extendLoan(Long id, Long userId, boolean isAdmin);
    List<Loan> updateAndFindLateLoans();
}
