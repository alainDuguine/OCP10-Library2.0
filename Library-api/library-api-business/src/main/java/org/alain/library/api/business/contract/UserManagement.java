package org.alain.library.api.business.contract;


import org.alain.library.api.model.user.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface UserManagement extends CrudManagement<User>{
    List<User> findUsersByEMail(String email);
    Optional<User> getUserByEmail(String email);
    Optional<User> saveUser(User user);
    Optional<User> updateUser(Long id, User userForm);
    void deleteUser(Long id);
    boolean login(String email, String password);
}
