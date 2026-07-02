package com.huuhv.foodsndrinks.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** JSON payload returned by the AJAX cart endpoints (add/update/remove) — lets the client
 *  re-render the cart and navbar badge without a page reload. */
@Getter
@AllArgsConstructor
public class CartPayloadResDto {
    private final OrderResDto cart;   // null when the cart is empty
    private final int cartItemCount;
}
