package com.huuhv.foodsndrinks.controller.admin;

import com.huuhv.foodsndrinks.dto.request.ProductReqDto;
import com.huuhv.foodsndrinks.dto.response.ProductResDto;
import com.huuhv.foodsndrinks.entity.Category;
import com.huuhv.foodsndrinks.service.CategoryService;
import com.huuhv.foodsndrinks.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/products")
@RequiredArgsConstructor
public class ProductAdminController {

    private static final String PRODUCT_FORM_VIEW = "admin/products/product-form";

    private final ProductService  productService;
    private final CategoryService categoryService;

    @ModelAttribute("allCategories")
    public List<Category> allCategories() {
        return categoryService.getAllCategories();
    }

    @GetMapping
    public String listProducts(@RequestParam(required = false) String name,
                               @RequestParam(required = false) Long categoryId,
                               @RequestParam(required = false) String type,
                               @RequestParam(required = false) String isAvailable,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               Model model) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.clamp(size, 1, 100);

        Page<ProductResDto> productPage =
                productService.searchProducts(name, categoryId, type, isAvailable, safePage, safeSize);

        model.addAttribute("products",       productPage.getContent());
        model.addAttribute("currentPage",    productPage.getNumber());
        model.addAttribute("totalPages",     productPage.getTotalPages());
        model.addAttribute("totalElements",  productPage.getTotalElements());
        model.addAttribute("pageSize",       safeSize);
        model.addAttribute("searchName",     name);
        model.addAttribute("searchCategoryId", categoryId);
        model.addAttribute("searchType",     type);
        model.addAttribute("searchIsAvailable", isAvailable);
        return "admin/products/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("productReqDto", new ProductReqDto());
        model.addAttribute("pageTitle", "Thêm Sản Phẩm");
        return PRODUCT_FORM_VIEW;
    }

    @PostMapping("/add")
    public String processAdd(@Valid @ModelAttribute("productReqDto") ProductReqDto dto,
                             BindingResult bindingResult,
                             @RequestParam(value = "newImages", required = false) List<MultipartFile> newImages,
                             RedirectAttributes ra,
                             Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Thêm Sản Phẩm");
            return PRODUCT_FORM_VIEW;
        }
        try {
            productService.createProduct(dto, newImages, 0);
            ra.addFlashAttribute("successMessage", "Thêm sản phẩm thành công!");
        } catch (IllegalArgumentException e) {
            model.addAttribute("pageTitle", "Thêm Sản Phẩm");
            model.addAttribute("errorMessage", e.getMessage());
            return PRODUCT_FORM_VIEW;
        }
        return "redirect:/admin/products";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes ra) {
        try {
            model.addAttribute("productReqDto",  productService.getProductForEdit(id));
            model.addAttribute("productDetail",  productService.getProductDetail(id));
            model.addAttribute("editId",         id);
            model.addAttribute("pageTitle",      "Cập Nhật Sản Phẩm");
            return PRODUCT_FORM_VIEW;
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/products";
        }
    }

    @PostMapping("/edit/{id}")
    public String processEdit(@PathVariable Long id,
                              @Valid @ModelAttribute("productReqDto") ProductReqDto dto,
                              BindingResult bindingResult,
                              @RequestParam(value = "newImages",     required = false) List<MultipartFile> newImages,
                              @RequestParam(value = "deleteImageIds",required = false) List<Long> deleteImageIds,
                              @RequestParam(value = "primaryImageId",required = false) Long primaryImageId,
                              RedirectAttributes ra,
                              Model model) {
        model.addAttribute("editId", id);

        if (bindingResult.hasErrors()) {
            try { model.addAttribute("productDetail", productService.getProductDetail(id)); } catch (Exception ignored) {}
            model.addAttribute("pageTitle", "Cập Nhật Sản Phẩm");
            return PRODUCT_FORM_VIEW;
        }
        try {
            productService.updateProduct(id, dto, newImages, deleteImageIds, primaryImageId);
            ra.addFlashAttribute("successMessage", "Cập nhật sản phẩm thành công!");
        } catch (IllegalArgumentException e) {
            try { model.addAttribute("productDetail", productService.getProductDetail(id)); } catch (Exception ignored) {}
            model.addAttribute("pageTitle", "Cập Nhật Sản Phẩm");
            model.addAttribute("errorMessage", e.getMessage());
            return PRODUCT_FORM_VIEW;
        }
        return "redirect:/admin/products";
    }

    @PostMapping("/{id}/delete")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes ra) {
        try {
            productService.deleteProduct(id);
            ra.addFlashAttribute("successMessage", "Xóa sản phẩm thành công!");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/products";
    }
}

