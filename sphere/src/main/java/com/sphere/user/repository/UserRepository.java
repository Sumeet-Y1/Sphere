package com.sphere.user.repository;

import com.sphere.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByUsernameIgnoreCase(String username);
    List<User> findTop8ByUsernameContainingIgnoreCaseOrderByUsernameAsc(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}
