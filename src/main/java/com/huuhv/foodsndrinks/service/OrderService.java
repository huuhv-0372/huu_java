package com.huuhv.foodsndrinks.service;

import com.huuhv.foodsndrinks.dto.response.OrderResDto;
import com.huuhv.foodsndrinks.entity.Order;
import com.huuhv.foodsndrinks.entity.OrderDetail;
import com.huuhv.foodsndrinks.enums.OrderStatus;
import com.huuhv.foodsndrinks.repository.OrderRepository;
import com.huuhv.foodsndrinks.repository.ProductImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository        orderRepository;
    private final ProductImageRepository productImageRepository;

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
        // 1 query: order + user + orderDetails + products (FETCH JOIN, no lazy-load loops)
        Order order = orderRepository.findByIdWithDetailsAndProducts(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng #" + id));

        List<OrderDetail> details = order.getOrderDetails();

        // Batch-load primary image URLs in 1 query — avoids N+1 per product
        List<Long> productIds = details.stream()
                .filter(od -> od.getProduct() != null)
                .map(od -> od.getProduct().getId())
                .collect(Collectors.toList());

        Map<Long, String> primaryUrls = productIds.isEmpty() ? Map.of() :
                productImageRepository.findPrimaryUrlsByProductIds(productIds)
                        .stream()
                        .collect(Collectors.toMap(
                                row -> (Long) row[0],
                                row -> (String) row[1],
                                (a, b) -> a
                        ));

        return OrderResDto.forDetail(order, details, primaryUrls);
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

