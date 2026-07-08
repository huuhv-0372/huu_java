package com.huuhv.foodsndrinks.controller.web;

import com.huuhv.foodsndrinks.dto.response.OrderResDto;
import com.huuhv.foodsndrinks.entity.User;
import com.huuhv.foodsndrinks.service.OrderService;
import com.huuhv.foodsndrinks.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private static final int PAGE_SIZE = 10;

    private final OrderService orderService;
    private final UserService  userService;

    @GetMapping("/orders")
    public String orderHistory(@RequestParam(defaultValue = "0") int page,
                               Principal principal,
                               Model model) {
        User user = userService.getCurrentUser(principal.getName());
        Page<OrderResDto> orderPage = orderService.getOrderHistoryForUser(user.getId(), page, PAGE_SIZE);
        model.addAttribute("orderPage", orderPage);
        return "web/orders";
    }

    @GetMapping("/orders/{id}")
    public String orderDetail(@PathVariable Long id, Principal principal, Model model) {
        User user = userService.getCurrentUser(principal.getName());
        OrderResDto order = orderService.getOrderDetailForUser(id, user.getId());
        model.addAttribute("order", order);
        return "web/order-detail";
    }
}
