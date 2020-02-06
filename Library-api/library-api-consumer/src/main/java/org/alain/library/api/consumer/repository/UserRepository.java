package org.alain.library.api.consumer.repository;

import org.alain.library.api.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByEmailLike(String email);
    User findByEmail(String email);
}
