package org.alain.library.api.business.impl;

import lombok.extern.slf4j.Slf4j;
import org.alain.library.api.business.contract.UserManagement;
import org.alain.library.api.business.exceptions.UnauthorizedException;
import org.alain.library.api.consumer.repository.UserRepository;
import org.alain.library.api.model.loan.Loan;
import org.alain.library.api.model.loan.StatusDesignation;
import org.alain.library.api.model.user.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@Slf4j
public class UserManagementImpl extends CrudManagementImpl<User> implements UserManagement, UserDetailsService{

    private final UserRepository userRepository;

    public UserManagementImpl(UserRepository userRepository) {
        super(userRepository);
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String s){
        User user = userRepository.findByEmail(s);
        if (user == null){
            throw new UsernameNotFoundException("Unknown user " +s);
        }
        return new UserPrincipal(user);
    }

    @Override
    public List<User> findUsersByEMail(String email) {
        return userRepository.findByEmailLike("%"+email+"%");
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user != null){
            return Optional.of(user);
        }
        return Optional.empty();
    }

    @Override
    public boolean login(String email, String password) {
        User user = userRepository.findByEmail(email);
        if(user != null){
            return BCrypt.checkpw(password, user.getPassword());
        }
        return false;
    }

    @Override
    public Optional<User> saveUser(User user) {
        if (!this.getUserByEmail(user.getEmail()).isPresent()){
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            if(user.getRoles().isEmpty()) {
                user.setRoles("USER");
            }
            return Optional.of(userRepository.save(user));
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> updateUser(Long id, User userForm) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()){
            if (userForm.getFirstName() != null) {
                user.get().setFirstName(userForm.getFirstName());
            }
            if (userForm.getLastName() != null) {
                user.get().setLastName(userForm.getLastName());
            }
            return Optional.of(userRepository.save(user.get()));
        }
        return Optional.empty();
    }

    @Override
    public void deleteUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()){
            List<Loan> loanList = user.get().getLoans();
            if(!loanList.isEmpty()) {
                for (Loan loan : loanList) {
                    if (!loan.getCurrentStatus().equals(StatusDesignation.RETURNED.toString())) {
                        throw new UnauthorizedException("Impossible to delete user n°" + id + ", user concerned by loans who are still active");
                    }
                }
            }else{
                userRepository.deleteById(id);
            }
        }
    }
}
