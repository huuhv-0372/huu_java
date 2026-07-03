package com.huuhv.foodsndrinks.service.notification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Flat snapshot of a just-placed order, captured inside the checkout transaction
 * so the (later, async, post-commit) notification listener never touches lazy
 * JPA associations from a closed persistence context.
 */
public record OrderPlacedEvent(
        Long orderId,
        String customerName,
        String customerEmail,
        String customerPhone,
        String shippingAddress,
        String note,
        BigDecimal totalPrice,
        LocalDateTime orderedAt,
        List<OrderLineItem> items
) {
    public record OrderLineItem(String productName, int quantity, BigDecimal unitPrice, BigDecimal subtotal) {
    }
}
