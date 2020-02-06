package org.alain.library.api.business.contract;


import org.alain.library.api.model.book.Book;
import org.alain.library.api.model.book.BookCopy;

import java.util.List;
import java.util.Optional;

public interface BookManagement extends CrudManagement<Book> {
    List<Book> findByTitleAndAuthor(String title, String author);
    Optional<BookCopy> findCopyInBook(Long bookId, Long copyId);
    List<BookCopy> findCopiesInBook(Long id);
    void deleteCopyInBook(Long bookId, Long copyId);
    Optional<Book> saveBook(Book convertBookFormToBookModel);
    Optional<BookCopy> saveBookCopy(Long id, BookCopy bookCopy);
    Optional<BookCopy> updateBookCopy(Long bookId, Long copyId, BookCopy bookCopy);
    Optional<Book> updateBook(Long id, Book book);
}
