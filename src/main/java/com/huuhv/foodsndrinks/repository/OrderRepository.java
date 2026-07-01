package com.huuhv.foodsndrinks.repository;

import com.huuhv.foodsndrinks.entity.Order;
import com.huuhv.foodsndrinks.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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

    /**
     * Fetch order + user + orderDetails + products in 1 query for detail view.
     * products is @ManyToOne (not a collection) so no MultipleBagFetchException.
     */
    @Query("""
            SELECT DISTINCT o FROM Order o
            LEFT JOIN FETCH o.user
            LEFT JOIN FETCH o.orderDetails od
            LEFT JOIN FETCH od.product
            WHERE o.id = :id
            """)
    Optional<Order> findByIdWithDetailsAndProducts(@Param("id") Long id);

    // -------------------------------------------------------
    // Dashboard queries
    // -------------------------------------------------------

    @Query("SELECT COALESCE(SUM(o.totalPrice), 0) FROM Order o WHERE o.status = 'COMPLETED'")
    BigDecimal sumCompletedRevenue();

    long countByStatus(OrderStatus status);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status IN ('PENDING','PROCESSING')")
    long countActiveOrders();

    /** Status counts excluding CART */
    @Query("SELECT o.status, COUNT(o) FROM Order o WHERE o.status <> 'CART' GROUP BY o.status")
    List<Object[]> countGroupByStatus();

    /** Recent N orders (excluding CART) with user info */
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.user WHERE o.status <> 'CART' ORDER BY o.createdAt DESC")
    List<Order> findRecent(Pageable pageable);

    /**
     * Monthly revenue for COMPLETED orders since a given date.
     * Returns Object[]: [year(int), month(int), revenue(BigDecimal), count(long)]
     */
    @Query("""
            SELECT FUNCTION('YEAR', o.createdAt),
                   FUNCTION('MONTH', o.createdAt),
                   SUM(o.totalPrice),
                   COUNT(o)
            FROM Order o
            WHERE o.status = 'COMPLETED' AND o.createdAt >= :from
            GROUP BY FUNCTION('YEAR', o.createdAt), FUNCTION('MONTH', o.createdAt)
            ORDER BY FUNCTION('YEAR', o.createdAt), FUNCTION('MONTH', o.createdAt)
            """)
    List<Object[]> findMonthlyRevenue(@Param("from") LocalDateTime from);
}

