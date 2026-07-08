package com.huuhv.foodsndrinks.controller.web;

import com.huuhv.foodsndrinks.dto.request.ProfileUpdateReqDto;
import com.huuhv.foodsndrinks.entity.User;
import com.huuhv.foodsndrinks.service.OrderService;
import com.huuhv.foodsndrinks.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private static final int RECENT_ORDERS_SIZE = 5;
    private static final String PROFILE_EDIT_VIEW = "web/profile-edit";

    private final UserService  userService;
    private final OrderService orderService;

    @GetMapping("/profile")
    public String profilePage(Principal principal, Model model) {
        User user = userService.getCurrentUser(principal.getName());

        model.addAttribute("user", user);
        model.addAttribute("recentOrders", orderService.getOrderHistoryForUser(user.getId(), 0, RECENT_ORDERS_SIZE));
        model.addAttribute("cartItemCount", orderService.getCartItemCount(user.getId()));

        return "web/profile";
    }

    @GetMapping("/profile/edit")
    public String editProfileForm(Principal principal, Model model) {
        User user = userService.getCurrentUser(principal.getName());
        model.addAttribute("profileUpdateReqDto", userService.getProfileForEdit(user.getId()));
        return PROFILE_EDIT_VIEW;
    }

    @PostMapping("/profile/edit")
    public String updateProfile(@Valid @ModelAttribute("profileUpdateReqDto") ProfileUpdateReqDto dto,
                                BindingResult bindingResult,
                                Principal principal,
                                Model model,
                                RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            return PROFILE_EDIT_VIEW;
        }

        User user = userService.getCurrentUser(principal.getName());
        try {
            userService.updateProfile(user.getId(), dto);
            ra.addFlashAttribute("successMessage", "Cập nhật thông tin thành công!");
            return "redirect:/profile";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return PROFILE_EDIT_VIEW;
        }
    }
}
