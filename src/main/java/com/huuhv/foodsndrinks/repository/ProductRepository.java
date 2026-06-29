package com.huuhv.foodsndrinks.repository;

import com.huuhv.foodsndrinks.entity.Product;
import com.huuhv.foodsndrinks.enums.ProductType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Long id);
    boolean existsBySlug(String slug);
    boolean existsBySlugAndIdNot(String slug, Long id);

    @Query(value = """
            SELECT p FROM Product p LEFT JOIN FETCH p.category c
            WHERE (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))
              AND (:categoryId IS NULL OR (p.category IS NOT NULL AND c.id = :categoryId))
              AND (:type IS NULL OR p.type = :type)
              AND (:isAvailable IS NULL OR p.isAvailable = :isAvailable)
            ORDER BY p.id DESC
            """,
            countQuery = """
            SELECT COUNT(p) FROM Product p LEFT JOIN p.category c
            WHERE (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))
              AND (:categoryId IS NULL OR (p.category IS NOT NULL AND c.id = :categoryId))
              AND (:type IS NULL OR p.type = :type)
              AND (:isAvailable IS NULL OR p.isAvailable = :isAvailable)
            """)
    Page<Product> search(@Param("name") String name,
                         @Param("categoryId") Long categoryId,
                         @Param("type") ProductType type,
                         @Param("isAvailable") Boolean isAvailable,
                         Pageable pageable);
}

