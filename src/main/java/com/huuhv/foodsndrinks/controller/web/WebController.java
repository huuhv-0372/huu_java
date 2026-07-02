package com.huuhv.foodsndrinks.controller.web;

import com.huuhv.foodsndrinks.entity.Category;
import com.huuhv.foodsndrinks.entity.Product;
import com.huuhv.foodsndrinks.enums.ProductType;
import com.huuhv.foodsndrinks.service.CategoryService;
import com.huuhv.foodsndrinks.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class WebController {

    private static final int PAGE_SIZE = 9;

    private final ProductService productService;
    private final CategoryService categoryService;

    // Filter products
    @GetMapping({"", "/", "/menu"})
    public String menuPage(
            @RequestParam(required = false) ProductType type,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "name_asc") String sort,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        loadProductList(type, categoryId, keyword, sort, page, model);
        model.addAttribute("basePath", "/menu");
        return "web/index";
    }

    // Products by category
    @GetMapping("/categories/{slug}")
    public String categoryPage(
            @PathVariable String slug,
            @RequestParam(required = false) ProductType type,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "name_asc") String sort,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        Category category = categoryService.getCategoryBySlug(slug);
        loadProductList(type, category.getId(), keyword, sort, page, model);
        model.addAttribute("category", category);
        model.addAttribute("basePath", "/categories/" + category.getSlug());
        return "web/index";
    }

    // Contact page — static content, no business logic needed
    @GetMapping("/contact")
    public String contactPage() {
        return "web/contact";
    }

    // Product detail
    @GetMapping("/products/{slug}")
    public String productDetail(@PathVariable String slug, Model model) {

        Product product = productService.getProductBySlug(slug);

        model.addAttribute("product", product);
        // Resolved from the actual incoming request — never hardcode scheme/host/port in templates.
        model.addAttribute("baseUrl", ServletUriComponentsBuilder.fromCurrentContextPath().toUriString());
        return "web/product-detail";
    }

    private void loadProductList(ProductType type, Long categoryId, String keyword, String sort, int page, Model model) {
        Sort sortObj = switch (sort) {
            case "price_asc" -> Sort.by("price").ascending();
            case "price_desc" -> Sort.by("price").descending();
            case "rating_desc" -> Sort.by("ratingAvg").descending();
            default -> Sort.by("name").ascending();
        };

        Pageable pageable = PageRequest.of(Math.max(page, 0), PAGE_SIZE, sortObj);
        Page<Product> productPage = productService.filterProductsForUser(type, categoryId, keyword, pageable);

        List<Long> productIds = productPage.getContent().stream().map(Product::getId).collect(Collectors.toList());

        model.addAttribute("productPage", productPage);
        model.addAttribute("primaryImageUrls", productService.getPrimaryImageUrls(productIds));
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("currentType", type);
        model.addAttribute("currentCategoryId", categoryId);
        model.addAttribute("currentKeyword", keyword);
        model.addAttribute("currentSort", sort);
    }
}
