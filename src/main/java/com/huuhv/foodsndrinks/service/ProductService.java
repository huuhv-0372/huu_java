package com.huuhv.foodsndrinks.service;

import com.huuhv.foodsndrinks.dto.request.ProductReqDto;
import com.huuhv.foodsndrinks.dto.response.ProductResDto;
import com.huuhv.foodsndrinks.entity.Category;
import com.huuhv.foodsndrinks.entity.Product;
import com.huuhv.foodsndrinks.entity.ProductImage;
import com.huuhv.foodsndrinks.enums.ProductType;
import com.huuhv.foodsndrinks.repository.CategoryRepository;
import com.huuhv.foodsndrinks.repository.ProductImageRepository;
import com.huuhv.foodsndrinks.repository.ProductRepository;
import com.huuhv.foodsndrinks.utils.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private static final String IMAGE_FOLDER = "products";

    private final ProductRepository      productRepository;
    private final ProductImageRepository productImageRepository;
    private final CategoryRepository     categoryRepository;
    private final FileStorageService     fileStorageService;

    // -------------------------------------------------------
    // Read
    // -------------------------------------------------------

    @Transactional(readOnly = true)
    public Page<ProductResDto> searchProducts(String name, Long categoryId, String type,
                                              String isAvailableStr, int page, int size) {
        ProductType productType = parseEnum(ProductType.class, type);
        Boolean isAvailable = parseBoolean(isAvailableStr);
        String nameParam = blank(name) ? null : name.trim();

        Page<Product> productPage = productRepository.search(
                nameParam, categoryId, productType, isAvailable,
                PageRequest.of(Math.max(page, 0), size));

        // Batch-load primary image URLs in 1 query — avoids N+1
        List<Long> ids = productPage.getContent().stream()
                .map(Product::getId).collect(Collectors.toList());

        Map<Long, String> primaryUrls = ids.isEmpty() ? Map.of() :
                productImageRepository.findPrimaryUrlsByProductIds(ids)
                        .stream()
                        .collect(Collectors.toMap(
                                row -> (Long) row[0],
                                row -> (String) row[1],
                                (a, b) -> a   // keep first if somehow duplicate
                        ));

        return productPage.map(p -> ProductResDto.forList(p, primaryUrls.get(p.getId())));
    }

    @Transactional(readOnly = true)
    public ProductReqDto getProductForEdit(Long id) {
        Product p = findById(id);
        ProductReqDto dto = new ProductReqDto();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setCategoryId(p.getCategory() != null ? p.getCategory().getId() : null);
        dto.setType(p.getType());
        dto.setPrice(p.getPrice());
        dto.setDescription(p.getDescription());
        dto.setIsAvailable(p.getIsAvailable());
        return dto;
    }

    @Transactional(readOnly = true)
    public ProductResDto getProductDetail(Long id) {
        Product p = findById(id);
        List<ProductResDto.ImageDto> images = productImageRepository
                .findByProductIdOrderBySortOrderAsc(id)
                .stream().map(ProductResDto.ImageDto::from)
                .collect(Collectors.toList());
        return ProductResDto.forDetail(p, images);
    }

    // -------------------------------------------------------
    // Write
    // -------------------------------------------------------

    @Transactional
    public void createProduct(ProductReqDto dto,
                              List<MultipartFile> newImages,
                              int primaryIndex) {
        String normalizedName = normalizeName(dto.getName());
        if (productRepository.existsByName(normalizedName)) {
            throw new IllegalArgumentException("Tên sản phẩm đã tồn tại!");
        }

        Category category = findCategory(dto.getCategoryId());

        Product product = new Product();
        product.setName(normalizedName);
        product.setSlug(uniqueSlug(normalizedName, null));
        product.setCategory(category);
        product.setType(dto.getType());
        product.setPrice(dto.getPrice());
        product.setDescription(dto.getDescription());
        product.setIsAvailable(dto.getIsAvailable() != null && dto.getIsAvailable());
        productRepository.save(product);

        saveImages(product, newImages, primaryIndex, new ArrayList<>());
    }

    @Transactional
    public void updateProduct(Long id,
                              ProductReqDto dto,
                              List<MultipartFile> newImages,
                              List<Long> deleteImageIds,
                              Long primaryImageId) {
        Product product = findById(id);
        String normalizedName = normalizeName(dto.getName());

        if (productRepository.existsByNameAndIdNot(normalizedName, id)) {
            throw new IllegalArgumentException("Tên sản phẩm đã tồn tại!");
        }

        // Delete marked images
        if (deleteImageIds != null) {
            for (Long imgId : deleteImageIds) {
                productImageRepository.findById(imgId).ifPresent(img -> {
                    if (!img.getProduct().getId().equals(id)) return;
                    fileStorageService.delete(img.getImageUrl());
                    productImageRepository.delete(img);
                });
            }
        }

        // Re-load remaining images and update primary flag
        List<ProductImage> remaining = productImageRepository.findByProductIdOrderBySortOrderAsc(id);
        if (primaryImageId != null) {
            remaining.forEach(img -> img.setIsPrimary(img.getId().equals(primaryImageId)));
        }

        if (remaining.stream().noneMatch(ProductImage::getIsPrimary) && !remaining.isEmpty()) {
            remaining.getFirst().setIsPrimary(true);
        }
        productImageRepository.saveAll(remaining);

        // Upload new images
        saveImages(product, newImages, -1, remaining.isEmpty() ? new ArrayList<>() : remaining);

        product.setName(normalizedName);
        product.setSlug(uniqueSlug(normalizedName, id));
        product.setCategory(findCategory(dto.getCategoryId()));
        product.setType(dto.getType());
        product.setPrice(dto.getPrice());
        product.setDescription(dto.getDescription());
        product.setIsAvailable(dto.getIsAvailable() != null && dto.getIsAvailable());
        productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = findById(id);
        List<String> imageUrls = productImageRepository.findByProductIdOrderBySortOrderAsc(id)
                .stream()
                .map(ProductImage::getImageUrl)
                .toList();
        try {
            productRepository.delete(product);
            productRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Không thể xóa sản phẩm này vì đang được tham chiếu!");
        }

        // Delete images from storage
        imageUrls.forEach(fileStorageService::delete);
    }

    // -------------------------------------------------------
    // Helpers
    // -------------------------------------------------------

    private void saveImages(Product product, List<MultipartFile> files, int primaryIndex,
                            List<ProductImage> existingImages) {
        if (files == null || files.isEmpty()) return;

        boolean hasPrimary = existingImages.stream().anyMatch(ProductImage::getIsPrimary);
        int order = existingImages.stream().mapToInt(ProductImage::getSortOrder).max().orElse(-1) + 1;

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            if (file == null || file.isEmpty()) continue;

            String url = fileStorageService.store(file, IMAGE_FOLDER);
            boolean isPrimary = !hasPrimary && (primaryIndex < 0 ? i == 0 : i == primaryIndex);
            if (isPrimary) hasPrimary = true;

            ProductImage img = new ProductImage();
            img.setProduct(product);
            img.setImageUrl(url);
            img.setIsPrimary(isPrimary);
            img.setSortOrder(order++);
            productImageRepository.save(img);
        }
    }

    private Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm!"));
    }

    private Category findCategory(Long categoryId) {
        if (categoryId == null) return null;
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy danh mục!"));
    }

    private String normalizeName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Tên sản phẩm không được để trống!");
        }
        return name.trim();
    }

    private String uniqueSlug(String name, Long currentId) {
        String base = SlugUtils.toSlug(name);
        if (base.isBlank()) throw new IllegalArgumentException("Tên sản phẩm không hợp lệ để tạo slug!");
        String candidate = base;
        int i = 1;
        while (currentId == null ? productRepository.existsBySlug(candidate)
                                 : productRepository.existsBySlugAndIdNot(candidate, currentId)) {
            candidate = base + "-" + i++;
        }
        return candidate;
    }

    private static <E extends Enum<E>> E parseEnum(Class<E> type, String value) {
        if (blank(value)) return null;
        try {
            return Enum.valueOf(type, value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static Boolean parseBoolean(String value) {
        if (blank(value)) return null;
        return switch (value.trim().toLowerCase(Locale.ROOT)) {
            case "true", "1" -> Boolean.TRUE;
            case "false", "0" -> Boolean.FALSE;
            default -> null;
        };
    }

    private static boolean blank(String s) {
        return s == null || s.isBlank();
    }

    // Filter products for user
    @Transactional(readOnly = true)
    public Page<Product> filterProductsForUser(ProductType type, Long categoryId, String keyword, Pageable pageable) {
        String keywordParam = blank(keyword) ? null : keyword.trim();
        return productRepository.filterProducts(type, categoryId, keywordParam, pageable);
    }

    /** Batch-load primary image URLs for a page of products — avoids N+1 when rendering the list. */
    @Transactional(readOnly = true)
    public Map<Long, String> getPrimaryImageUrls(List<Long> productIds) {
        if (productIds.isEmpty()) return Map.of();
        return productImageRepository.findPrimaryUrlsByProductIds(productIds)
                .stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (String) row[1],
                        (a, b) -> a
                ));
    }

    // Get product by slug for user
    public Product getProductBySlug(String slug) {
        return productRepository.findBySlugAndIsAvailableTrue(slug)
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại hoặc đã ngừng kinh doanh!"));
    }
}



