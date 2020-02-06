package org.alain.library.api.consumer.repository;

import org.alain.library.api.model.book.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("select b, a from Book b join b.authors a where lower(b.title) like %:title% and lower(concat(a.firstName, ' ', a.lastName))like %:author%")
    List<Book> findByTitleAndAuthor(@Param("title")String title,@Param("author") String author);

    List<Book> findByTitle(String title);
}
