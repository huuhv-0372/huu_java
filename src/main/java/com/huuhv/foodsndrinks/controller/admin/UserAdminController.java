package com.huuhv.foodsndrinks.controller.admin;

import com.huuhv.foodsndrinks.dto.request.UserEditReqDto;
import com.huuhv.foodsndrinks.dto.response.UserResDto;
import com.huuhv.foodsndrinks.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class UserAdminController {

    private static final String USER_FORM_VIEW = "admin/users/user-form";

    private final UserService userService;

    // -------------------------------------------------------
    // List + search
    // -------------------------------------------------------

    @GetMapping
    public String listUsers(@RequestParam(required = false) String keyword,
                            @RequestParam(required = false) String role,
                            @RequestParam(required = false) String isActive,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "15") int size,
                            Model model) {
        Page<UserResDto> userPage =
                userService.searchUsers(keyword, role, isActive, page, size);

        model.addAttribute("users",          userPage.getContent());
        model.addAttribute("currentPage",    userPage.getNumber());
        model.addAttribute("totalPages",     userPage.getTotalPages());
        model.addAttribute("totalElements",  userPage.getTotalElements());
        model.addAttribute("pageSize",       size);
        model.addAttribute("searchKeyword",  keyword);
        model.addAttribute("searchRole",     role);
        model.addAttribute("searchIsActive", isActive);
        return "admin/users/list";
    }

    // -------------------------------------------------------
    // Edit form
    // -------------------------------------------------------

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes ra) {
        try {
            model.addAttribute("userEditReqDto", userService.getUserForEdit(id));
            model.addAttribute("userDetail",     userService.getUserDetail(id));
            model.addAttribute("pageTitle",      "Chỉnh sửa tài khoản");
            return USER_FORM_VIEW;
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/users";
        }
    }

    @PostMapping("/{id}/edit")
    public String processEdit(@PathVariable Long id,
                              @Valid @ModelAttribute("userEditReqDto") UserEditReqDto dto,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            try { model.addAttribute("userDetail", userService.getUserDetail(id)); } catch (Exception ignored) {}
            model.addAttribute("pageTitle", "Chỉnh sửa tài khoản");
            return USER_FORM_VIEW;
        }
        try {
            userService.updateUser(id, dto);
            ra.addFlashAttribute("successMessage", "Cập nhật tài khoản thành công!");
        } catch (IllegalArgumentException e) {
            try { model.addAttribute("userDetail", userService.getUserDetail(id)); } catch (Exception ignored) {}
            model.addAttribute("pageTitle", "Chỉnh sửa tài khoản");
            model.addAttribute("errorMessage", e.getMessage());
            return USER_FORM_VIEW;
        }
        return "redirect:/admin/users";
    }

    // -------------------------------------------------------
    // Quick toggle active (from list)
    // -------------------------------------------------------

    @PostMapping("/{id}/toggle-active")
    public String toggleActive(@PathVariable Long id, RedirectAttributes ra) {
        try {
            userService.toggleActive(id);
            ra.addFlashAttribute("successMessage", "Đã cập nhật trạng thái tài khoản #" + id);
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/users";
    }
}

