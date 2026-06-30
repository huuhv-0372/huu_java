package com.huuhv.foodsndrinks.dto.response;

import com.huuhv.foodsndrinks.entity.Order;
import com.huuhv.foodsndrinks.entity.OrderDetail;
import com.huuhv.foodsndrinks.entity.User;
import com.huuhv.foodsndrinks.enums.OrderStatus;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class OrderResDto {

    private final Long          id;
    private final Long          userId;
    private final String        username;
    private final String        userFullName;
    private final String        userEmail;
    private final BigDecimal    totalPrice;
    private final OrderStatus   status;
    private final String        shippingAddress;
    private final String        note;
    private final LocalDateTime orderedAt;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    /** Null for list view, populated for detail view */
    private final List<OrderItemDto> items;

    private OrderResDto(Order o, List<OrderItemDto> items) {
        User u           = o.getUser();
        this.id              = o.getId();
        this.userId          = u != null ? u.getId() : null;
        this.username        = u != null ? u.getUsername()   : "—";
        this.userFullName    = u != null ? u.getFullName()   : "—";
        this.userEmail       = u != null ? u.getEmail()      : "—";
        this.totalPrice      = o.getTotalPrice();
        this.status          = o.getStatus();
        this.shippingAddress = o.getShippingAddress();
        this.note            = o.getNote();
        this.orderedAt       = o.getOrderedAt();
        this.createdAt       = o.getCreatedAt();
        this.updatedAt       = o.getUpdatedAt();
        this.items           = items;
    }

    /** For list rows — no items loaded */
    public static OrderResDto forList(Order o) {
        return new OrderResDto(o, null);
    }

    /**
     * For detail page — items built with pre-loaded primary image URLs.
     * @param primaryImageUrls map of productId → primaryImageUrl (batch-loaded, no N+1)
     */
    public static OrderResDto forDetail(Order o, List<OrderDetail> details,
                                        Map<Long, String> primaryImageUrls) {
        List<OrderItemDto> items = details.stream()
                .map(od -> OrderItemDto.from(od, primaryImageUrls))
                .collect(Collectors.toList());
        return new OrderResDto(o, items);
    }

    // -------------------------------------------------------

    @Getter
    public static class OrderItemDto {
        private final Long       id;
        private final Long       productId;
        private final String     productName;
        private final String     productImageUrl;
        private final Integer    quantity;
        private final BigDecimal unitPrice;
        private final BigDecimal subtotal;

        private OrderItemDto(OrderDetail od, String primaryImageUrl) {
            this.id              = od.getId();
            this.productId       = od.getProduct() != null ? od.getProduct().getId()   : null;
            this.productName     = od.getProduct() != null ? od.getProduct().getName() : "—";
            this.productImageUrl = primaryImageUrl;   // pre-loaded, no lazy fetch
            this.quantity        = od.getQuantity();
            this.unitPrice       = od.getUnitPrice();
            this.subtotal        = od.getSubtotal();
        }

        public static OrderItemDto from(OrderDetail od, Map<Long, String> primaryImageUrls) {
            Long pid = od.getProduct() != null ? od.getProduct().getId() : null;
            return new OrderItemDto(od, pid != null ? primaryImageUrls.get(pid) : null);
        }
    }
}



