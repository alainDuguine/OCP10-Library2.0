package org.alain.library.api.service.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.alain.library.api.business.contract.UserManagement;
import org.alain.library.api.business.exceptions.UnauthorizedException;
import org.alain.library.api.business.impl.UserPrincipal;
import org.alain.library.api.model.user.User;
import org.alain.library.api.service.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

import static org.alain.library.api.service.api.Converters.*;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2019-10-31T15:23:24.407+01:00")

@Controller
@Slf4j
public class UsersApiController implements UsersApi {

    private final ObjectMapper objectMapper;
    private final UserManagement userManagement;
    private final HttpServletRequest request;

    @org.springframework.beans.factory.annotation.Autowired
    public UsersApiController(ObjectMapper objectMapper, UserManagement userManagement, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.userManagement = userManagement;
        this.request = request;
    }

    public ResponseEntity<UserDto> getUser(@ApiParam(value = "Id of user to return", required = true) @PathVariable("id") Long id) {
        log.info("Getting user " + id);
        Optional<User> userModel = userManagement.findOne(id);
        if (userModel.isPresent()) {
            return new ResponseEntity<UserDto>(convertUserModelToUserDto(userModel.get()), HttpStatus.OK);
        }
        return new ResponseEntity<UserDto>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<List<UserDto>> getUsers(@ApiParam(value = "Email of user to return", defaultValue = "")
                                                  @Valid @RequestParam(value = "email", required = false, defaultValue = "") String email) {
        List<User> userList = userManagement.findUsersByEMail(email);
        log.info("Getting list of users : " + userList.size());
        return new ResponseEntity<List<UserDto>>(convertListUsersModelToListUsersDto(userList), HttpStatus.OK);
    }

    public ResponseEntity<UserDto> getUserByEmail(@NotNull @ApiParam(value = "Email of user to return", required = true)
                                                  @Valid @RequestParam(value = "email", required = true) String email,
                                                  @ApiParam(value = "User identification" ,required=true) @RequestHeader(value="Authorization", required=true) String authorization) {
        log.info("Getting userByEmail " + email);
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(userPrincipal.getUsername().equals(email)) {
            Optional<User> user = userManagement.getUserByEmail(email);
            if (user.isPresent()) {
                return new ResponseEntity<UserDto>(convertUserModelToUserDto(user.get()), HttpStatus.OK);
            }
            log.info("user unknown" + email);
            return new ResponseEntity<UserDto>(HttpStatus.NOT_FOUND);
        }else{
            log.warn("getUserByEmail Unauthorized " + email);
            return new ResponseEntity<UserDto>(HttpStatus.UNAUTHORIZED);
        }
    }

    public ResponseEntity<Void> deleteUser(@ApiParam(value = "User id to delete", required = true) @PathVariable("id") Long id) {
        try {
            log.info("deleteUser n°" + id);
            userManagement.deleteUser(id);
            return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
        } catch (UnauthorizedException ex) {
            log.warn("deleteUser Unauthorized " + id);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ex.getMessage());
        }
    }

    public ResponseEntity<UserDto> addUser(@ApiParam(value = "User object that needs to be added to the database", required = true) @Valid @RequestBody UserForm userForm) {
        log.info("creating new User");
        Optional<User> userModel = userManagement.saveUser(convertUserFormToUserModel(userForm));
        if (userModel.isPresent()) {
            log.info("New User :" + userModel.get().getId() + ' ' + userModel.get());
            return new ResponseEntity<UserDto>(convertUserModelToUserDto(userModel.get()), HttpStatus.CREATED);
        }
        log.warn("User not created " + userForm.getEmail());
        return new ResponseEntity<UserDto>(HttpStatus.CONFLICT);
    }

    public ResponseEntity<UserDto> updateUser(@ApiParam(value = "User id to update", required = true) @PathVariable("id") Long id,
                                              @ApiParam(value = "User object to update", required = true) @Valid @RequestBody UserFormUpdate userFormUpdate,
                                              @ApiParam(value = "User identification", required = true) @RequestHeader(value = "Authorization", required = true) String authorization) {
        log.info("Updating User : " + id);
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(userPrincipal.hasRole("ADMIN") || userPrincipal.getId() == id) {
            Optional<User> userModel = userManagement.updateUser(id, convertUserFormUpdateToUserModel(userFormUpdate));
            if (userModel.isPresent()) {
                return new ResponseEntity<UserDto>(convertUserModelToUserDto(userModel.get()), HttpStatus.OK);
            }
            log.warn("Unknown user : " + id);
            return new ResponseEntity<UserDto>(HttpStatus.NOT_FOUND);
        }else{
            log.warn("Unauthorized updateUser : " + id);
            return new ResponseEntity<UserDto>(HttpStatus.UNAUTHORIZED);
        }
    }

    public ResponseEntity<Void> login(@ApiParam(value = "User email and password" ,required=true )  @Valid @RequestBody UserCredentials userCredentials) {
        log.info("Login : " + userCredentials.getEmail());
        if(userManagement.login(userCredentials.getEmail(), userCredentials.getPassword())){
            return new ResponseEntity<Void>(HttpStatus.OK);
        }
        log.info("Wrong credentials" + userCredentials.getEmail());
        return new ResponseEntity<Void>(HttpStatus.UNAUTHORIZED);
    }
}
