package com.huuhv.foodsndrinks.repository;

import com.huuhv.foodsndrinks.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    Optional<Rating> findByUserIdAndProductId(Long userId, Long productId);

    long countByProductId(Long productId);

    @Query("SELECT r FROM Rating r LEFT JOIN FETCH r.user WHERE r.product.id = :productId ORDER BY r.createdAt DESC")
    List<Rating> findByProductIdOrderByCreatedAtDesc(@Param("productId") Long productId);

    /** JPQL AVG() of an empty set is NULL — callers must handle that case. */
    @Query("SELECT AVG(r.stars) FROM Rating r WHERE r.product.id = :productId")
    Double avgStarsByProductId(@Param("productId") Long productId);
}
