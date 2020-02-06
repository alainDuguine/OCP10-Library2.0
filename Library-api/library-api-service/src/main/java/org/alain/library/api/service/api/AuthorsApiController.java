package org.alain.library.api.service.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.alain.library.api.business.contract.AuthorManagement;
import org.alain.library.api.model.book.Author;
import org.alain.library.api.service.dto.AuthorDto;
import org.alain.library.api.service.dto.AuthorForm;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

import static org.alain.library.api.service.api.Converters.*;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2019-10-31T08:40:05.054+01:00")

@Controller
@Validated
@Slf4j
public class AuthorsApiController implements AuthorsApi {

    private final ObjectMapper objectMapper;
    private final AuthorManagement authorManagement;
    private final HttpServletRequest request;

    @org.springframework.beans.factory.annotation.Autowired
    public AuthorsApiController(ObjectMapper objectMapper, AuthorManagement authorManagement, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.authorManagement = authorManagement;
        this.request = request;
    }

    public ResponseEntity<AuthorDto> getAuthor(@ApiParam(value = "Id of author to return", required = true) @PathVariable("id") Long id) {
        log.info("Getting author " + id);
        Optional<Author> author = authorManagement.findOne(id);
        if (author.isPresent()) {
            return new ResponseEntity<AuthorDto>(convertAuthorModelToAuthorDto(author.get()), HttpStatus.OK);
        }
        log.warn("Unknown author " + id);
        return new ResponseEntity<AuthorDto>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<List<AuthorDto>> getAuthors(@ApiParam(value = "Name of author to return", defaultValue = "") @Valid @RequestParam(value = "name", required = false, defaultValue = "") String name) {
        List<Author> authorList = authorManagement.findAuthorsByName(name);
        log.info("Getting list authors : " + authorList.size() + "name : " + name);
        return new ResponseEntity<List<AuthorDto>>(convertListAuthorModelToListAuthorDto(authorList), HttpStatus.OK);
    }

    public ResponseEntity<Void> deleteAuthor(@ApiParam(value = "Author id to delete", required = true) @PathVariable("id") Long id) {
        log.info("Deleting author : " + id);
        authorManagement.deleteAuthor(id);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    public ResponseEntity<AuthorDto> addAuthor(@ApiParam(value = "Author object that needs to be added to the database", required = true) @Valid @RequestBody AuthorForm authorForm) {
        log.info("creating new author");
        Optional<Author> authorModel = authorManagement.saveAuthor(convertAuthorFormToAuthorModel(authorForm));
        if (authorModel.isPresent()) {
            log.info("New author created : " +authorModel.get().getId());
            return new ResponseEntity<AuthorDto>(convertAuthorModelToAuthorDto(authorModel.get()), HttpStatus.CREATED);
        }
        log.warn("Author already exists" + authorForm.getFirstName() + " - " + authorForm.getLastName());
        return new ResponseEntity<AuthorDto>(HttpStatus.CONFLICT);
    }

    public ResponseEntity<AuthorDto> updateAuthor(@ApiParam(value = "Author id to update", required = true) @PathVariable("id") Long id,
                                                  @ApiParam(value = "Author object updated", required = true)
                                                  @Valid @RequestBody AuthorDto author) {
        log.info("Update loan : " + id);
        Optional<Author> authorModel = authorManagement.updateAuthor(id, convertAuthorDtoToAuthorModel(author));
        if (authorModel.isPresent()) {
            return new ResponseEntity<AuthorDto>(convertAuthorModelToAuthorDto(authorModel.get()), HttpStatus.OK);
        }
        log.warn("Unknown loan : " + id);
        return new ResponseEntity<AuthorDto>(HttpStatus.NOT_FOUND);
    }
}
