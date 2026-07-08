package com.huuhv.foodsndrinks.repository;

import com.huuhv.foodsndrinks.entity.OrderDetail;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

    /**
     * Top-selling products by total quantity sold (COMPLETED orders only).
     * Returns Object[]: [productId(Long), productName(String), totalQty(Long)]
     */
    @Query("""
            SELECT od.product.id, od.product.name, SUM(od.quantity)
            FROM OrderDetail od
            JOIN od.order o
            WHERE o.status = 'COMPLETED' AND od.product IS NOT NULL
            GROUP BY od.product.id, od.product.name
            ORDER BY SUM(od.quantity) DESC
            """)
    List<Object[]> findTopSellingProducts(Pageable pageable);
}

