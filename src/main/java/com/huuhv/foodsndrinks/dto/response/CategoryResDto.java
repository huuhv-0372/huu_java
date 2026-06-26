package com.huuhv.foodsndrinks.dto.response;

import com.huuhv.foodsndrinks.entity.Category;
import com.huuhv.foodsndrinks.enums.CategoryType;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CategoryResDto {

    private final Long id;
    private final String name;
    private final String slug;
    private final CategoryType type;
    private final String description;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

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

