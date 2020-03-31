package org.alain.library.webapp.business.contract;

import io.swagger.client.model.BookDto;
import org.alain.library.webapp.model.ExtendedBook;

import java.util.List;

public interface BookManagement{
    List<ExtendedBook> getExtendedBookDtoList(List<BookDto> bookDtoList);
}
