package com.huuhv.foodsndrinks.dto.response;

import com.huuhv.foodsndrinks.entity.Product;
import com.huuhv.foodsndrinks.entity.ProductImage;
import com.huuhv.foodsndrinks.enums.ProductType;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ProductResDto {

    private final Long id;
    private final String name;
    private final String slug;
    private final Long categoryId;
    private final String categoryName;
    private final ProductType type;
    private final BigDecimal price;
    private final String description;
    private final Boolean isAvailable;
    private final BigDecimal ratingAvg;
    private final Integer ratingCount;
    private final String primaryImageUrl;
    private final List<ImageDto> images;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private ProductResDto(Product p, String primaryImageUrl, List<ImageDto> images) {
        this.id             = p.getId();
        this.name           = p.getName();
        this.slug           = p.getSlug();
        this.categoryId     = p.getCategory() != null ? p.getCategory().getId() : null;
        this.categoryName   = p.getCategory() != null ? p.getCategory().getName() : "—";
        this.type           = p.getType();
        this.price          = p.getPrice();
        this.description    = p.getDescription();
        this.isAvailable    = p.getIsAvailable();
        this.ratingAvg      = p.getRatingAvg();
        this.ratingCount    = p.getRatingCount();
        this.primaryImageUrl = primaryImageUrl;
        this.images         = images;
        this.createdAt      = p.getCreatedAt();
        this.updatedAt      = p.getUpdatedAt();
    }

    /** For list view — no image list needed */
    public static ProductResDto forList(Product p, String primaryImageUrl) {
        return new ProductResDto(p, primaryImageUrl, List.of());
    }

    /** For detail / edit view — includes full image list */
    public static ProductResDto forDetail(Product p, List<ImageDto> images) {
        String primaryUrl = images.stream()
                .filter(ImageDto::getIsPrimary)
                .map(ImageDto::getImageUrl)
                .findFirst().orElse(null);
        return new ProductResDto(p, primaryUrl, images);
    }

    // -------------------------------------------------------

    @Getter
    public static class ImageDto {
        private final Long id;
        private final String imageUrl;
        private final Boolean isPrimary;
        private final Integer sortOrder;

        private ImageDto(ProductImage img) {
            this.id        = img.getId();
            this.imageUrl  = img.getImageUrl();
            this.isPrimary = img.getIsPrimary();
            this.sortOrder = img.getSortOrder();
        }

        public static ImageDto from(ProductImage img) {
            return new ImageDto(img);
        }
    }
}

