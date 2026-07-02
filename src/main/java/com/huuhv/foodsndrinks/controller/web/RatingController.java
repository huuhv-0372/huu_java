package com.huuhv.foodsndrinks.controller.web;

import com.huuhv.foodsndrinks.entity.User;
import com.huuhv.foodsndrinks.service.ProductService;
import com.huuhv.foodsndrinks.service.RatingService;
import com.huuhv.foodsndrinks.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class RatingController {

    private final RatingService  ratingService;
    private final ProductService productService;
    private final UserService    userService;

    @PostMapping("/products/{slug}/rate")
    public String rateProduct(@PathVariable String slug,
                              @RequestParam Byte stars,
                              @RequestParam(required = false) String comment,
                              Principal principal,
                              RedirectAttributes ra) {
        User user = userService.getCurrentUser(principal.getName());
        try {
            Long productId = productService.getProductBySlug(slug).getId();
            ratingService.rateProduct(user, productId, stars, comment);
            ra.addFlashAttribute("successMessage", "Cảm ơn bạn đã đánh giá sản phẩm!");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/products/" + slug;
    }
}
