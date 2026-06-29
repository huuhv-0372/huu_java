package com.huuhv.foodsndrinks.service;

import com.huuhv.foodsndrinks.dto.response.OrderResDto;
import com.huuhv.foodsndrinks.entity.Order;
import com.huuhv.foodsndrinks.entity.OrderDetail;
import com.huuhv.foodsndrinks.enums.OrderStatus;
import com.huuhv.foodsndrinks.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public Page<OrderResDto> searchOrders(String statusStr, String keyword,
                                          Long orderId, int page, int size) {
        OrderStatus status = parseStatus(statusStr);
        String kw = blank(keyword) ? null : keyword.trim();

        return orderRepository
                .search(status, kw, orderId, PageRequest.of(Math.max(page, 0), size))
                .map(OrderResDto::forList);
    }

    @Transactional(readOnly = true)
    public OrderResDto getOrderDetail(Long id) {
        Order order = orderRepository.findByIdWithUser(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng #" + id));

        // Lazy-load order details + products within this transaction
        List<OrderDetail> details = order.getOrderDetails();
        // Trigger lazy load of each product + its primary image
        details.forEach(od -> {
            if (od.getProduct() != null) {
                od.getProduct().getImages().size(); // initialize images collection
            }
        });

        return OrderResDto.forDetail(order, details);
    }

    @Transactional
    public void updateStatus(Long id, OrderStatus newStatus) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng #" + id));

        OrderStatus current = order.getStatus();
        if (current == newStatus) return;

        // Set orderedAt when transitioning from CART → PENDING
        if (current == OrderStatus.CART && newStatus == OrderStatus.PENDING
                && order.getOrderedAt() == null) {
            order.setOrderedAt(LocalDateTime.now());
        }

        order.setStatus(newStatus);
        orderRepository.save(order);
    }

    private static OrderStatus parseStatus(String value) {
        if (blank(value)) return null;
        try {
            return OrderStatus.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static boolean blank(String s) {
        return s == null || s.isBlank();
    }
}

