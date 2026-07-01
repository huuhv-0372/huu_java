package com.huuhv.foodsndrinks.service;

import com.huuhv.foodsndrinks.dto.response.OrderResDto;
import com.huuhv.foodsndrinks.entity.Order;
import com.huuhv.foodsndrinks.entity.OrderDetail;
import com.huuhv.foodsndrinks.entity.Product;
import com.huuhv.foodsndrinks.entity.User;
import com.huuhv.foodsndrinks.enums.OrderStatus;
import com.huuhv.foodsndrinks.repository.OrderDetailRepository;
import com.huuhv.foodsndrinks.repository.OrderRepository;
import com.huuhv.foodsndrinks.repository.ProductImageRepository;
import com.huuhv.foodsndrinks.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository        orderRepository;
    private final OrderDetailRepository  orderDetailRepository;
    private final ProductRepository      productRepository;
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
        return buildOrderResDto(order, order.getOrderDetails());
    }

    // -------------------------------------------------------
    // User-facing: cart
    // -------------------------------------------------------

    @Transactional(readOnly = true)
    public OrderResDto getCartForUser(Long userId) {
        Order cart = orderRepository.findCartByUserId(userId).orElse(null);
        if (cart == null || cart.getOrderDetails().isEmpty()) return null;
        return buildOrderResDto(cart, cart.getOrderDetails());
    }

    @Transactional(readOnly = true)
    public int getCartItemCount(Long userId) {
        return orderDetailRepository.sumCartQuantityByUserId(userId);
    }

    @Transactional
    public void addToCart(User user, Long productId, int quantity) {
        int qty = Math.max(quantity, 1);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại!"));
        if (!Boolean.TRUE.equals(product.getIsAvailable())) {
            throw new IllegalArgumentException("Sản phẩm hiện không còn kinh doanh!");
        }

        Order cart = orderRepository.findCartByUserId(user.getId()).orElseGet(() -> createEmptyCart(user));

        OrderDetail existing = cart.getOrderDetails().stream()
                .filter(od -> od.getProduct() != null && od.getProduct().getId().equals(productId))
                .findFirst().orElse(null);

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + qty);
            existing.setSubtotal(existing.getUnitPrice().multiply(BigDecimal.valueOf(existing.getQuantity())));
        } else {
            OrderDetail detail = new OrderDetail();
            detail.setOrder(cart);
            detail.setProduct(product);
            detail.setQuantity(qty);
            detail.setUnitPrice(product.getPrice());
            detail.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(qty)));
            cart.getOrderDetails().add(detail);
        }

        recalculateTotal(cart);
        orderRepository.save(cart);
    }

    @Transactional
    public void updateCartItemQuantity(User user, Long orderDetailId, int quantity) {
        OrderDetail detail = requireOwnedCartItem(user, orderDetailId);
        Order cart = detail.getOrder();

        if (quantity < 1) {
            cart.getOrderDetails().remove(detail);
        } else {
            detail.setQuantity(quantity);
            detail.setSubtotal(detail.getUnitPrice().multiply(BigDecimal.valueOf(quantity)));
        }

        recalculateTotal(cart);
        orderRepository.save(cart);
    }

    @Transactional
    public void removeCartItem(User user, Long orderDetailId) {
        OrderDetail detail = requireOwnedCartItem(user, orderDetailId);
        Order cart = detail.getOrder();
        cart.getOrderDetails().remove(detail);
        recalculateTotal(cart);
        orderRepository.save(cart);
    }

    @Transactional
    public void checkout(User user, String shippingAddress, String note) {
        Order cart = orderRepository.findCartByUserId(user.getId())
                .filter(o -> !o.getOrderDetails().isEmpty())
                .orElseThrow(() -> new IllegalArgumentException("Giỏ hàng của bạn đang trống!"));

        if (blank(shippingAddress)) {
            throw new IllegalArgumentException("Vui lòng nhập địa chỉ giao hàng!");
        }

        cart.setShippingAddress(shippingAddress.trim());
        cart.setNote(note);
        cart.setStatus(OrderStatus.PENDING);
        cart.setOrderedAt(LocalDateTime.now());
        orderRepository.save(cart);
    }

    // -------------------------------------------------------
    // User-facing: order history
    // -------------------------------------------------------

    @Transactional(readOnly = true)
    public Page<OrderResDto> getOrderHistoryForUser(Long userId, int page, int size) {
        return orderRepository.findOrderHistoryByUserId(userId, PageRequest.of(Math.max(page, 0), size))
                .map(OrderResDto::forList);
    }

    @Transactional(readOnly = true)
    public OrderResDto getOrderDetailForUser(Long orderId, Long userId) {
        Order order = orderRepository.findByIdAndUserIdWithDetailsAndProducts(orderId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng #" + orderId));
        return buildOrderResDto(order, order.getOrderDetails());
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

    private OrderResDto buildOrderResDto(Order order, List<OrderDetail> details) {
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

    private Order createEmptyCart(User user) {
        Order cart = new Order();
        cart.setUser(user);
        cart.setStatus(OrderStatus.CART);
        cart.setOrderDetails(new ArrayList<>());
        return orderRepository.save(cart);
    }

    private OrderDetail requireOwnedCartItem(User user, Long orderDetailId) {
        OrderDetail detail = orderDetailRepository.findById(orderDetailId)
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm trong giỏ không tồn tại!"));

        Order order = detail.getOrder();
        if (order.getUser() == null || !order.getUser().getId().equals(user.getId())
                || order.getStatus() != OrderStatus.CART) {
            throw new IllegalArgumentException("Không thể cập nhật giỏ hàng này!");
        }
        return detail;
    }

    private void recalculateTotal(Order cart) {
        BigDecimal total = cart.getOrderDetails().stream()
                .map(OrderDetail::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotalPrice(total);
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

