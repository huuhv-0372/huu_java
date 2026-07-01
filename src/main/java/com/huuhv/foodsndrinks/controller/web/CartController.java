package com.huuhv.foodsndrinks.controller.web;

import com.huuhv.foodsndrinks.dto.response.OrderResDto;
import com.huuhv.foodsndrinks.entity.User;
import com.huuhv.foodsndrinks.service.OrderService;
import com.huuhv.foodsndrinks.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class CartController {

    private final OrderService orderService;
    private final UserService  userService;

    @GetMapping("/cart")
    public String cartPage(Principal principal, Model model) {
        User user = userService.getCurrentUser(principal.getName());
        OrderResDto cart = orderService.getCartForUser(user.getId());
        model.addAttribute("cart", cart);
        return "web/cart";
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam Long productId,
                            @RequestParam(defaultValue = "1") int quantity,
                            Principal principal,
                            RedirectAttributes ra) {
        User user = userService.getCurrentUser(principal.getName());
        try {
            orderService.addToCart(user, productId, quantity);
            ra.addFlashAttribute("successMessage", "Đã thêm vào giỏ hàng!");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/cart";
    }

    @PostMapping("/cart/update/{detailId}")
    public String updateCartItem(@PathVariable Long detailId,
                                 @RequestParam int quantity,
                                 Principal principal,
                                 RedirectAttributes ra) {
        User user = userService.getCurrentUser(principal.getName());
        try {
            orderService.updateCartItemQuantity(user, detailId, quantity);
            ra.addFlashAttribute("successMessage", "Đã cập nhật giỏ hàng!");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/cart";
    }

    @PostMapping("/cart/remove/{detailId}")
    public String removeCartItem(@PathVariable Long detailId,
                                 Principal principal,
                                 RedirectAttributes ra) {
        User user = userService.getCurrentUser(principal.getName());
        try {
            orderService.removeCartItem(user, detailId);
            ra.addFlashAttribute("successMessage", "Đã xóa sản phẩm khỏi giỏ hàng!");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/cart";
    }

    @PostMapping("/cart/checkout")
    public String checkout(@RequestParam String shippingAddress,
                           @RequestParam(required = false) String note,
                           Principal principal,
                           RedirectAttributes ra) {
        User user = userService.getCurrentUser(principal.getName());
        try {
            orderService.checkout(user, shippingAddress, note);
            ra.addFlashAttribute("successMessage", "Đặt hàng thành công! Cảm ơn bạn đã mua hàng.");
            return "redirect:/orders";
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/cart";
        }
    }
}
