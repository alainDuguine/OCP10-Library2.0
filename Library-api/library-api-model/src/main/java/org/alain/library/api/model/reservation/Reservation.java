package org.alain.library.api.model.reservation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.alain.library.api.model.book.Book;
import org.alain.library.api.model.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    private Book book;

    @NotNull
    @Builder.Default
    @OneToMany(
            mappedBy = "reservation",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<ReservationStatus> statuses = new ArrayList<>();

    public void addStatus(ReservationStatus status){
        statuses.add(status);
        status.setReservation(this);
    }

    public void removeStatus(ReservationStatus status){
        statuses.remove(status);
        status.setReservation(null);
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", user=" + user.getId() +
                ", book=" + book.getId() +
                ", statuses=" + statuses +
                '}';
    }
}
