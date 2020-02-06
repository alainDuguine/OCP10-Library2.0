package org.alain.library.api.model.loan;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;


/**
 * Composite Primary Key to the intermediary table between
 * Loan and Status
 */

@Embeddable
public class LoanStatusId implements Serializable {

    @Column(name = "loan_id")
    private Long loanId;

    @Column(name = "status_id")
    private Long statusId;

    public LoanStatusId() {
    }

    public LoanStatusId(Long loanId, Long statusId) {
        this.loanId = loanId;
        this.statusId = statusId;
    }

    public Long getLoanId() {
        return loanId;
    }

    public void setLoanId(Long loanId) {
        this.loanId = loanId;
    }

    public Long getStatusId() {
        return statusId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoanStatusId that = (LoanStatusId) o;
        return Objects.equals(getLoanId(), that.getLoanId()) &&
                Objects.equals(getStatusId(), that.getStatusId());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((loanId == null) ? 0 : loanId.hashCode());
        result = prime * result
                + ((statusId == null) ? 0 : statusId.hashCode());
        return result;
    }
}
