package org.alain.library.api.model.book;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size( min = 2, max = 30)
    @Column (length = 30)
    private String firstName;

    @Size(max = 30)
    @Column (length = 30)
    private String lastName;

    @JsonIgnore
    @ManyToMany(mappedBy = "authors")
    private Set<Book> books = new HashSet<>();

    public Author(@NotNull @Size(min = 2, max = 30) String firstName, @NotNull @Size(min = 2, max = 30) String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public void addBook(Book book){
        this.books.add(book);
        book.getAuthors().add(this);
    }

    public void removeBook(Book book){
        this.books.remove(book);
        book.getAuthors().remove(this);
    }

    @Override
    public String toString() {
        StringBuilder listBooks = new StringBuilder();
        for (Book book : this.books) {
            listBooks.append("\n\tBook id : ").append(book.getId()).append(", book title : ")
                    .append(book.getTitle());
        }
        return "Author{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", books=" + listBooks.toString() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (this.getClass() != o.getClass()) return false;
        return id != null && id.equals(((Author)o).getId());
    }
    @Override
    public int hashCode() {
        return 17;
    }
}
