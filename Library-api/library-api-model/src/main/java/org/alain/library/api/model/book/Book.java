package org.alain.library.api.model.book;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.alain.library.api.model.reservation.Reservation;
import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String isbn;
    @NotNull
    private String title;

    @JsonIgnore
    @Builder.Default
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookCopy> copyList = new ArrayList<>();

    @Builder.Default
    @OneToMany(
            mappedBy = "book",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("currentStatusDate desc")
    private List<Reservation> reservations=new ArrayList<>();

    @NotNull
    @Builder.Default
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "book_author",
               joinColumns = @JoinColumn(name = "book_id"),
               inverseJoinColumns = @JoinColumn(name = "author_id"))
    private Set<Author> authors = new HashSet<>();


    @Formula("(SELECT COUNT(bc.id) FROM book b left join book_copy bc on bc.book_id = b.id WHERE bc.available = 'true' and b.id = id)")
    private Long nbCopiesAvailable;

    // TODO CHECK
    @Formula("(SELECT COUNT(r.id) FROM book b left join reservation r on r.book_id = b.id WHERE r.current_status = 'RESERVED' and b.id = id)")
    private Long nbCopiesReserved;

    @Formula("(SELECT COUNT(r.id) FROM book b left join reservation r on r.book_id = b.id WHERE (r.current_status = 'PENDING' or r.current_status = 'RESERVED') and b.id = id)")
    private Long nbActiveReservations;

    @Formula("(SELECT MIN(l.end_date) FROM book b left join book_copy bc on bc.book_id = b.id left join loan l on l.book_copy_id = bc.id WHERE (l.current_status = 'LOANED' OR l.current_status = 'PROLONGED') and b.id = id)")
    private LocalDate nextReturnDate;

    public Book(String title) {
        this.title = title;
        this.isbn = RandomStringUtils.randomAlphanumeric(10);
    }

    public void addAuthor(Author author){
        if(this.authors==null){
            this.authors= new HashSet<>();
        }
        this.authors.add(author);
        author.getBooks().add(this);
    }

    public void removeAuthor(Author author){
        this.authors.remove(author);
        author.getBooks().remove(this);
    }

    public void addCopy(BookCopy bookCopy){
        this.copyList.add(bookCopy);
        bookCopy.setBook(this);
    }

    public void removeCopy(BookCopy bookCopy){
        this.copyList.remove(bookCopy);
        bookCopy.setBook(null);
    }

    public void addReservation(Reservation reservation){
        this.reservations.add(reservation);
        reservation.setBook(this);
    }

    public void removeReservation(Reservation reservation){
        this.reservations.remove(reservation);
        reservation.setBook(null);
    }

    public boolean isReservationListFull(){
        return (nbActiveReservations >= (this.getCopyList().size() * 2));
    }

    @Override
    public String toString() {
        StringBuilder listAuthors = new StringBuilder();
        for (Author author : this.authors) {
            listAuthors.append("\n\tAuthor id : ").append(author.getId()).append(", author name : ")
                    .append(author.getFirstName())
                    .append(author.getLastName());
        }

        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", authors :" + listAuthors.toString() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (this.getClass() != o.getClass()) return false;
        return id != null && id.equals(((Book)o).getId());
    }
    @Override
    public int hashCode() {
        return 13;
    }
}
