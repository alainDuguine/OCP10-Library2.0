package org.alain.library.api.consumer.repository;

import org.alain.library.api.model.book.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    @Query("select a FROM Author a where concat(a.firstName, ' ', a.lastName) like %:name%")
    List<Author> findAuthorsListByName(@Param("name") String name);

    Optional<Author> findByFirstNameAndLastName(String firstName, String lastName);
}