package com.huuhv.foodsndrinks.repository;

import com.huuhv.foodsndrinks.entity.User;
import com.huuhv.foodsndrinks.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    boolean existsByUsernameAndIdNot(String username, Long id);
    boolean existsByEmailAndIdNot(String email, Long id);
    boolean existsByPhoneAndIdNot(String phone, Long id);

    Optional<User> findByUsernameOrEmail(String username, String email);

    @Query(value = """
            SELECT u FROM User u
            WHERE (:keyword IS NULL
                   OR LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(u.email)    LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(u.phone)    LIKE LOWER(CONCAT('%', :keyword, '%')))
              AND (:role       IS NULL OR u.role       = :role)
              AND (:isActive   IS NULL OR u.isActive   = :isActive)
            ORDER BY u.id DESC
            """,
            countQuery = """
            SELECT COUNT(u) FROM User u
            WHERE (:keyword IS NULL
                   OR LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(u.email)    LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(u.phone)    LIKE LOWER(CONCAT('%', :keyword, '%')))
              AND (:role       IS NULL OR u.role       = :role)
              AND (:isActive   IS NULL OR u.isActive   = :isActive)
            """)
    Page<User> search(@Param("keyword")  String keyword,
                      @Param("role")     Role role,
                      @Param("isActive") Boolean isActive,
                      Pageable pageable);
}
