package org.alain.library.api.model.book;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String isbn;
    @NotNull
    private String title;

    @JsonIgnore
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookCopy> copyList = new ArrayList<>();

    //Owning side
    @NotNull
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "book_author",
               joinColumns = @JoinColumn(name = "book_id"),
               inverseJoinColumns = @JoinColumn(name = "author_id"))
    private Set<Author> authors = new HashSet<>();

    @Formula("(SELECT COUNT(bc.id) FROM book b left join book_copy bc on bc.book_id = b.id WHERE bc.available = 'true' and b.id = id)")
    private Long nbCopiesAvailable;

    public Book(String title) {
        this.title = title;
        this.isbn = RandomStringUtils.randomAlphanumeric(10);
    }

    public void addAuthor(Author author){
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
