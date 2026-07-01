package com.huuhv.foodsndrinks.repository;

import com.huuhv.foodsndrinks.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    List<ProductImage> findByProductIdOrderBySortOrderAsc(Long productId);

    /**
     * Batch-load primary image URLs for a list of product IDs.
     * Returns Object[] rows: [productId (Long), imageUrl (String)]
     * — avoids N+1 when rendering product lists.
     */
    @Query("""
            SELECT i.product.id, i.imageUrl
            FROM ProductImage i
            WHERE i.product.id IN :productIds AND i.isPrimary = true
            """)
    List<Object[]> findPrimaryUrlsByProductIds(@Param("productIds") List<Long> productIds);
}

