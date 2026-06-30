package com.huuhv.foodsndrinks.dto.response;

import com.huuhv.foodsndrinks.entity.Category;
import com.huuhv.foodsndrinks.enums.CategoryType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResDto {

    private Long id;
    private String name;
    private String slug;
    private CategoryType type;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private CategoryResDto(Category category) {
        this.id          = category.getId();
        this.name        = category.getName();
        this.slug        = category.getSlug();
        this.type        = category.getType();
        this.description = category.getDescription();
        this.createdAt   = category.getCreatedAt();
        this.updatedAt   = category.getUpdatedAt();
    }

    public static CategoryResDto from(Category category) {
        return new CategoryResDto(category);
    }
}

