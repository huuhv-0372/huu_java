package com.huuhv.foodsndrinks.repository;

import com.huuhv.foodsndrinks.entity.Product;
import com.huuhv.foodsndrinks.enums.ProductType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Long id);
    boolean existsBySlug(String slug);
    boolean existsBySlugAndIdNot(String slug, Long id);

    long countByIsAvailableTrue();

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

    // Note: no fetch-join on p.images here — it's a @OneToMany collection, and combining a
    // collection fetch-join with Pageable forces Hibernate to paginate the whole result set
    // in memory. Primary image URLs for the page are batch-loaded separately (see
    // ProductImageRepository.findPrimaryUrlsByProductIds), same pattern as ProductService.searchProducts.
    @Query(value = """
            SELECT p FROM Product p
            LEFT JOIN FETCH p.category c
            WHERE p.isAvailable = true
              AND (:type IS NULL OR p.type = :type)
              AND (:categoryId IS NULL OR c.id = :categoryId)
              AND (:keyword IS NULL
                   OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """,
            countQuery = """
            SELECT COUNT(p) FROM Product p LEFT JOIN p.category c
            WHERE p.isAvailable = true
              AND (:type IS NULL OR p.type = :type)
              AND (:categoryId IS NULL OR c.id = :categoryId)
              AND (:keyword IS NULL
                   OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    Page<Product> filterProducts(@Param("type") ProductType type,
                                 @Param("categoryId") Long categoryId,
                                 @Param("keyword") String keyword,
                                 Pageable pageable);

    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.images WHERE p.slug = :slug AND p.isAvailable = true")
    Optional<Product> findBySlugAndIsAvailableTrue(@Param("slug") String slug);
}

