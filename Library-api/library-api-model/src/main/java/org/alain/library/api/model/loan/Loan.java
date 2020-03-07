package org.alain.library.api.model.loan;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.alain.library.api.model.book.BookCopy;
import org.alain.library.api.model.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table
public class Loan {
    public static final List<String> ACTIVE_STATUSES = Arrays.asList(StatusDesignation.PROLONGED.name(), StatusDesignation.LOANED.name());

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private LocalDate startDate;
    private LocalDate endDate;
    private String currentStatus;
    private LocalDateTime currentStatusDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private BookCopy bookCopy;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LoanStatus> loanStatuses = new ArrayList<>();

    public LoanStatus addLoanStatus(Status status){
        LoanStatus loanStatus = new LoanStatus(this, status);
        this.loanStatuses.add(loanStatus);
        status.getLoanStatuses().add(loanStatus);
        return loanStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Loan loan = (Loan) o;
        return Objects.equals(id, loan.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
