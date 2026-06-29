package com.huuhv.foodsndrinks.repository;

import com.huuhv.foodsndrinks.entity.Order;
import com.huuhv.foodsndrinks.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query(value = """
            SELECT o FROM Order o LEFT JOIN FETCH o.user u
            WHERE (:status   IS NULL OR o.status = :status)
              AND (:orderId  IS NULL OR o.id     = :orderId)
              AND (:keyword  IS NULL
                   OR LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(u.email)    LIKE LOWER(CONCAT('%', :keyword, '%')))
            ORDER BY o.id DESC
            """,
            countQuery = """
            SELECT COUNT(o) FROM Order o LEFT JOIN o.user u
            WHERE (:status   IS NULL OR o.status = :status)
              AND (:orderId  IS NULL OR o.id     = :orderId)
              AND (:keyword  IS NULL
                   OR LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(u.email)    LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    Page<Order> search(@Param("status")  OrderStatus status,
                       @Param("keyword") String keyword,
                       @Param("orderId") Long orderId,
                       Pageable pageable);

    /** Fetch order + user eagerly for detail view */
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.user WHERE o.id = :id")
    Optional<Order> findByIdWithUser(@Param("id") Long id);
}

