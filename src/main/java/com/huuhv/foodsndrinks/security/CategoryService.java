package com.huuhv.foodsndrinks.security;

import com.huuhv.foodsndrinks.dto.request.CategoryReqDto;
import com.huuhv.foodsndrinks.dto.response.CategoryResDto;
import com.huuhv.foodsndrinks.entity.Category;
import com.huuhv.foodsndrinks.enums.CategoryType;
import com.huuhv.foodsndrinks.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<CategoryResDto> searchCategories(String name, String type, String description, int page, int size) {
        CategoryType categoryType = null;
        if (type != null && !type.isBlank()) {
            try {
                categoryType = CategoryType.valueOf(type.trim().toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException ignored) {}
        }
        String nameParam = (name == null || name.isBlank()) ? null : name.trim();
        String descParam = (description == null || description.isBlank()) ? null : description.trim();
        Pageable pageable = PageRequest.of(Math.max(page, 0), size);
        return categoryRepository.searchCategoryByNameTypeDesc(nameParam, categoryType, descParam, pageable)
                .map(CategoryResDto::from);
    }

    @Transactional(readOnly = true)
    public CategoryReqDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy danh mục!"));

        CategoryReqDto dto = new CategoryReqDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setType(category.getType());
        dto.setDescription(category.getDescription());
        return dto;
    }

    // Add new category
    @Transactional
    public void addCategory(CategoryReqDto categoryReqDto) {
        String normalizedName = normalizeName(categoryReqDto.getName());

        if (categoryRepository.existsByName(normalizedName)) {
            throw new IllegalArgumentException("Tên danh mục đã tồn tại!");
        }

        Category category = new Category();
        category.setName(normalizedName);
        category.setSlug(generateUniqueSlug(normalizedName));
        category.setType(categoryReqDto.getType() == null ? CategoryType.ALL : categoryReqDto.getType());
        category.setDescription(categoryReqDto.getDescription());
        categoryRepository.save(category);
    }

    // Edit category
    @Transactional
    public void updateCategory(CategoryReqDto updateCategoryReqDto, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy danh mục!"));

        String normalizedName = normalizeName(updateCategoryReqDto.getName());

        if (categoryRepository.existsByNameAndIdNot(normalizedName, categoryId)) {
            throw new IllegalArgumentException("Tên danh mục đã tồn tại!");
        }

        category.setName(normalizedName);
        category.setSlug(generateUniqueSlug(normalizedName, category.getId()));
        category.setType(updateCategoryReqDto.getType() == null ? CategoryType.ALL : updateCategoryReqDto.getType());
        category.setDescription(updateCategoryReqDto.getDescription());
        categoryRepository.save(category);
    }

    // Delete category by id
    @Transactional
    public void deleteCategory(Long id) {
        try {
            categoryRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("Danh mục không tồn tại!");
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Không thể xóa danh mục này vì đang được sử dụng.");
        }
    }

    // Convert category name to slug
    private String convertToSlug(String input) {
        if (input == null || input.isEmpty()) return "";
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String slug = pattern.matcher(normalized).replaceAll("");
        return slug.toLowerCase(Locale.ROOT)
                .replace("đ", "d")
                .replaceAll("[^a-z0-9\\s-]", "") // Xóa ký tự đặc biệt
                .replaceAll("\\s+", "-")         // Thay khoảng trắng bằng gạch ngang
                .replaceAll("-+", "-");          // Xóa gạch ngang thừa
    }

    private String normalizeName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Tên danh mục không được để trống!");
        }
        return name.trim();
    }

    private String generateUniqueSlug(String name) {
        return generateUniqueSlug(name, null);
    }

    private String generateUniqueSlug(String name, Long currentCategoryId) {
        String baseSlug = convertToSlug(name);
        if (baseSlug.isBlank()) {
            throw new IllegalArgumentException("Tên danh mục không hợp lệ để tạo slug!");
        }

        String candidate = baseSlug;
        int index = 1;
        while ((currentCategoryId == null && categoryRepository.existsBySlug(candidate))
                || (currentCategoryId != null && categoryRepository.existsBySlugAndIdNot(candidate, currentCategoryId))) {
            candidate = baseSlug + "-" + index++;
        }
        return candidate;
    }
}
