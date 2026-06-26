package com.huuhv.foodsndrinks.repository;

import com.huuhv.foodsndrinks.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);

    Optional<User> findByUsernameOrEmail(String username, String email);
}
