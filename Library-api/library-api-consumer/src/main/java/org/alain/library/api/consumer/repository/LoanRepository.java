package org.alain.library.api.consumer.repository;

import org.alain.library.api.model.loan.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    @Query("Select l from Loan l WHERE (:status is null or l.currentStatus = :status) and (:id is null or  l.user.id = :id)")
    List<Loan> findByCurrentStatusAndUserId(@Param("status") String status, @Param("id")Long id);

    @Query("select l from Loan l where l.endDate < CURRENT_DATE and l.currentStatus <> 'RETURNED'")
    List<Loan> findLateLoans();
}
