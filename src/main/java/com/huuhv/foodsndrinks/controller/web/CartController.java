package com.huuhv.foodsndrinks.controller.web;

import com.huuhv.foodsndrinks.dto.response.CartPayloadResDto;
import com.huuhv.foodsndrinks.dto.response.ErrorResponse;
import com.huuhv.foodsndrinks.dto.response.OrderResDto;
import com.huuhv.foodsndrinks.entity.User;
import com.huuhv.foodsndrinks.service.OrderService;
import com.huuhv.foodsndrinks.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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

    // Add/update/remove are called via fetch() from the storefront — no page reload,
    // so they return the fresh cart state as JSON instead of a redirect.

    @PostMapping("/cart/add")
    @ResponseBody
    public ResponseEntity<?> addToCart(@RequestParam Long productId,
                                       @RequestParam(defaultValue = "1") int quantity,
                                       Principal principal) {
        User user = userService.getCurrentUser(principal.getName());
        try {
            orderService.addToCart(user, productId, quantity);
            return ResponseEntity.ok(buildCartPayload(user.getId()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ErrorResponse.builder().message(e.getMessage()).build());
        }
    }

    @PostMapping("/cart/update/{detailId}")
    @ResponseBody
    public ResponseEntity<?> updateCartItem(@PathVariable Long detailId,
                                            @RequestParam int quantity,
                                            Principal principal) {
        User user = userService.getCurrentUser(principal.getName());
        try {
            orderService.updateCartItemQuantity(user, detailId, quantity);
            return ResponseEntity.ok(buildCartPayload(user.getId()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ErrorResponse.builder().message(e.getMessage()).build());
        }
    }

    @PostMapping("/cart/remove/{detailId}")
    @ResponseBody
    public ResponseEntity<?> removeCartItem(@PathVariable Long detailId,
                                            Principal principal) {
        User user = userService.getCurrentUser(principal.getName());
        try {
            orderService.removeCartItem(user, detailId);
            return ResponseEntity.ok(buildCartPayload(user.getId()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ErrorResponse.builder().message(e.getMessage()).build());
        }
    }

    // Checkout still navigates to a new page (order confirmation / history), so it stays a normal redirect flow.
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

    private CartPayloadResDto buildCartPayload(Long userId) {
        OrderResDto cart = orderService.getCartForUser(userId);
        int cartItemCount = orderService.getCartItemCount(userId);
        return new CartPayloadResDto(cart, cartItemCount);
    }
}
