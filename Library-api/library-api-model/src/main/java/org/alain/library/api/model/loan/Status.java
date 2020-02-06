package org.alain.library.api.model.loan;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Data
@NoArgsConstructor
public class Status {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private StatusDesignation designation;

    @OneToMany(mappedBy = "status")
    private List<LoanStatus> loanStatuses = new ArrayList<>();

    public Status(StatusDesignation designation) {
        this.designation = designation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Status status = (Status) o;
        return Objects.equals(id, status.id) &&
                designation == status.designation;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, designation);
    }

}
