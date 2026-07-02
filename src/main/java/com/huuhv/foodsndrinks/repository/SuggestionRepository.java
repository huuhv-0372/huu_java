package com.huuhv.foodsndrinks.repository;

import com.huuhv.foodsndrinks.entity.Suggestion;
import com.huuhv.foodsndrinks.enums.SuggestionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SuggestionRepository extends JpaRepository<Suggestion, Long> {

    @Query(value = """
            SELECT s FROM Suggestion s LEFT JOIN FETCH s.user u
            WHERE (:status  IS NULL OR s.status = :status)
              AND (:keyword IS NULL
                   OR LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(s.content)  LIKE LOWER(CONCAT('%', :keyword, '%')))
            ORDER BY s.id DESC
            """,
            countQuery = """
            SELECT COUNT(s) FROM Suggestion s LEFT JOIN s.user u
            WHERE (:status  IS NULL OR s.status = :status)
              AND (:keyword IS NULL
                   OR LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(s.content)  LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    Page<Suggestion> search(@Param("status")  SuggestionStatus status,
                            @Param("keyword") String keyword,
                            Pageable pageable);

    /** A user's own suggestion history — customer-facing "/suggest" page. */
    @Query(value = """
            SELECT s FROM Suggestion s
            WHERE s.user.id = :userId
            ORDER BY s.createdAt DESC
            """,
            countQuery = """
            SELECT COUNT(s) FROM Suggestion s
            WHERE s.user.id = :userId
            """)
    Page<Suggestion> findByUserId(@Param("userId") Long userId, Pageable pageable);
}

