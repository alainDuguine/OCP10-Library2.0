package org.alain.library.api.business.contract;

import org.alain.library.api.model.book.Author;
import java.util.List;
import java.util.Optional;

public interface AuthorManagement extends CrudManagement<Author> {
    List<Author> findAuthorsByName(String name);
    Optional<Author> saveAuthor(Author author);
    Optional<Author> updateAuthor(Long id, Author author);
    void deleteAuthor(Long id);
}
