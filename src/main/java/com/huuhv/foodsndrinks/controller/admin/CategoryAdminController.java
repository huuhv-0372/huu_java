package com.huuhv.foodsndrinks.controller.admin;

import com.huuhv.foodsndrinks.dto.request.CategoryReqDto;
import com.huuhv.foodsndrinks.dto.response.CategoryResDto;
import com.huuhv.foodsndrinks.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class CategoryAdminController {

    private static final String CATEGORY_FORM_VIEW = "admin/categories/category-form";
    private final CategoryService categoryService;

    @GetMapping
    public String listCategories(@RequestParam(required = false) String name,
                                 @RequestParam(required = false) String type,
                                 @RequestParam(required = false) String description,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 Model model) {
        int safePage = Math.max(page, 0);
        int safePageSize = Math.clamp(size, 1, 100);
        Page<CategoryResDto> categoryPage = categoryService.searchCategories(name, type, description, safePage, safePageSize);
        model.addAttribute("categories", categoryPage.getContent());
        model.addAttribute("currentPage", categoryPage.getNumber());
        model.addAttribute("totalPages", categoryPage.getTotalPages());
        model.addAttribute("totalElements", categoryPage.getTotalElements());
        model.addAttribute("pageSize", safePageSize);
        model.addAttribute("searchName", name);
        model.addAttribute("searchType", type);
        model.addAttribute("searchDescription", description);

        return "admin/categories/list";
    }

    @GetMapping("/add")
    public String showAddCategoryForm(Model model) {
        model.addAttribute("categoryReqDto", new CategoryReqDto());
        model.addAttribute("pageTitle", "Thêm Danh Mục");
        return CATEGORY_FORM_VIEW;
    }

    @PostMapping("/add")
    public String processAdd(@Valid @ModelAttribute("categoryReqDto") CategoryReqDto categoryReqDto,
                             BindingResult bindingResult,
                             RedirectAttributes ra,
                             Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Thêm Danh Mục");
            return CATEGORY_FORM_VIEW;
        }

        try {
            categoryService.addCategory(categoryReqDto);
            ra.addFlashAttribute("successMessage", "Thêm danh mục thành công!");
        } catch (IllegalArgumentException e) {
            model.addAttribute("pageTitle", "Thêm Danh Mục");
            model.addAttribute("errorMessage", e.getMessage());
            return CATEGORY_FORM_VIEW;
        }
        return "redirect:/admin/categories";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes ra) {
        try {
            model.addAttribute("categoryReqDto", categoryService.getCategoryById(id));
            model.addAttribute("editId", id);
            model.addAttribute("pageTitle", "Cập Nhật Danh Mục");
            return CATEGORY_FORM_VIEW;
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/categories";
        }
    }

    @PostMapping("/edit/{id}")
    public String processEdit(@PathVariable Long id,
                              @Valid @ModelAttribute("categoryReqDto") CategoryReqDto categoryReqDto,
                              BindingResult bindingResult,
                              RedirectAttributes ra,
                              Model model) {
        model.addAttribute("editId", id);

        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Cập Nhật Danh Mục");
            return CATEGORY_FORM_VIEW;
        }

        try {
            categoryService.updateCategory(categoryReqDto, id);
            ra.addFlashAttribute("successMessage", "Cập nhật danh mục thành công!");
        } catch (IllegalArgumentException e) {
            model.addAttribute("pageTitle", "Cập Nhật Danh Mục");
            model.addAttribute("errorMessage", e.getMessage());
            return CATEGORY_FORM_VIEW;
        }
        return "redirect:/admin/categories";
    }

    @PostMapping("/{id}/delete")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa danh mục thành công!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/categories";
    }
}
