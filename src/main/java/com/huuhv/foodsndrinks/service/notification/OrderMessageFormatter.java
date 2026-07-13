package com.huuhv.foodsndrinks.service.notification;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Shared text formatting for order notifications (Slack message + admin email body). */
final class OrderMessageFormatter {

    private static final NumberFormat CURRENCY = NumberFormat.getInstance(new Locale("vi", "VN"));
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private OrderMessageFormatter() {
    }

    static String buildOrderSummary(OrderPlacedEvent event) {
        StringBuilder sb = new StringBuilder();
        sb.append("Đơn hàng mới #").append(event.orderId()).append('\n');
        if (event.orderedAt() != null) {
            sb.append("Thời gian: ").append(event.orderedAt().format(DATE_TIME)).append('\n');
        }
        sb.append("Khách hàng: ").append(event.customerName())
                .append(" (").append(event.customerEmail()).append(')');
        if (event.customerPhone() != null && !event.customerPhone().isBlank()) {
            sb.append(" - SĐT: ").append(event.customerPhone());
        }
        sb.append('\n');
        sb.append("Địa chỉ giao hàng: ").append(event.shippingAddress()).append('\n');
        if (event.note() != null && !event.note().isBlank()) {
            sb.append("Ghi chú: ").append(event.note()).append('\n');
        }
        sb.append("Chi tiết đơn hàng:\n");
        for (OrderPlacedEvent.OrderLineItem item : event.items()) {
            sb.append("  - ").append(item.productName())
                    .append(" x").append(item.quantity())
                    .append(" = ").append(CURRENCY.format(item.subtotal())).append(" VND\n");
        }
        sb.append("Tổng tiền: ").append(CURRENCY.format(event.totalPrice())).append(" VND");
        return sb.toString();
    }
}
