package org.alain.library.webapp.business.impl;

import io.swagger.client.model.BookDto;
import lombok.extern.slf4j.Slf4j;
import org.alain.library.webapp.business.contract.BookManagement;
import org.alain.library.webapp.model.ExtendedBook;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class BookManagementImpl implements BookManagement {

    @Override
    public List<ExtendedBook> getExtendedBookDtoList(List<BookDto> bookDtoList) {
        log.info("Converting bookDto to ExtendedBook : {} books", bookDtoList.size());
        return bookDtoList.stream()
                .map(ExtendedBook::new)
                .collect(Collectors.toList());
    }

}
