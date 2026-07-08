package com.huuhv.foodsndrinks.repository;

import com.huuhv.foodsndrinks.entity.Category;
import com.huuhv.foodsndrinks.enums.CategoryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Long id);
    boolean existsBySlug(String slug);
    boolean existsBySlugAndIdNot(String slug, Long id);

    @Query("""
            SELECT c FROM Category c
            WHERE (:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')))
              AND (:type IS NULL OR c.type = :type)
              AND (:description IS NULL OR LOWER(c.description) LIKE LOWER(CONCAT('%', :description, '%')))
            ORDER BY c.id DESC
            """)
    Page<Category> searchCategoryByNameTypeDesc(@Param("name") String name,
                          @Param("type") CategoryType type,
                          @Param("description") String description,
                          Pageable pageable);
}
