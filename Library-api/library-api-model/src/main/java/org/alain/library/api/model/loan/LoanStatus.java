package org.alain.library.api.model.loan;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents the association table between Loan and Status,
 * which has as primary key the object @Embeddable LoanStatusId
 */
@Entity(name = "LoanStatus")
@Table(name = "loan_status")
public class LoanStatus {

    @EmbeddedId
    private LoanStatusId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("loanId")
    @JoinColumn(name="loan_id")
    private Loan loan;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("statusId")
    @JoinColumn(name="status_id")
    private Status status;

    @Column
    private LocalDateTime date = LocalDateTime.now();

    public LoanStatus() {
    }

    public LoanStatus(Loan loan, Status status) {
        this.loan = loan;
        this.status = status;
        this.id = new LoanStatusId(loan.getId(), status.getId());
    }

    public LoanStatusId getId() {
        return id;
    }

    public void setId(LoanStatusId id) {
        this.id = id;
    }

    public Loan getLoan() {
        return loan;
    }

    public void setLoan(Loan loan) {
        this.loan = loan;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoanStatus that = (LoanStatus) o;
        return Objects.equals(loan, that.loan) &&
                Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(loan, status);
    }

    @Override
    public String toString() {
        return "LoanStatus{" +
                "id=" + id +
                ", loan=" + loan.getId() +
                ", status=" + status.getDesignation() +
                ", date=" + date +
                '}';
    }
}
