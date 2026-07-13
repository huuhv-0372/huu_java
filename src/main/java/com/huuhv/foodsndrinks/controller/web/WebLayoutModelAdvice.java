package com.huuhv.foodsndrinks.controller.web;

import com.huuhv.foodsndrinks.service.OrderService;
import com.huuhv.foodsndrinks.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(basePackages = "com.huuhv.foodsndrinks.controller.web")
@RequiredArgsConstructor
public class WebLayoutModelAdvice {

    private final UserService  userService;
    private final OrderService orderService;

    /** Cart item count shown on the navbar badge — 0 for guests */
    @ModelAttribute("cartItemCount")
    public int cartItemCount(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return 0;
        }
        Long userId = userService.getCurrentUser(authentication.getName()).getId();
        return orderService.getCartItemCount(userId);
    }
}
