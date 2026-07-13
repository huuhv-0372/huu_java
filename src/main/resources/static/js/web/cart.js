// Cart page — quantity stepper / remove, all via AJAX (no page reload).
// Relies on postForm(), showToast(), updateCartBadge() defined in main.js.
document.addEventListener("DOMContentLoaded", function () {
    const itemsBody = document.getElementById("cartItemsBody");
    if (!itemsBody) return; // not on the cart page

    const emptyState = document.getElementById("cartEmptyState");
    const fullState = document.getElementById("cartFullState");
    const totalPriceEl = document.getElementById("cartTotalPrice");

    itemsBody.addEventListener("click", function (e) {
        const row = e.target.closest("tr[data-detail-id]");
        if (!row) return;
        const detailId = row.dataset.detailId;
        const input = row.querySelector(".js-qty-input");

        if (e.target.closest(".js-qty-increase")) {
            updateQuantity(detailId, parseInt(input.value, 10) + 1);
        } else if (e.target.closest(".js-qty-decrease")) {
            const next = parseInt(input.value, 10) - 1;
            if (next >= 1) updateQuantity(detailId, next);
        } else if (e.target.closest(".js-remove-item")) {
            removeItem(detailId);
        }
    });

    itemsBody.addEventListener("change", function (e) {
        if (!e.target.classList.contains("js-qty-input")) return;
        const row = e.target.closest("tr[data-detail-id]");
        const qty = parseInt(e.target.value, 10);
        if (!qty || qty < 1) {
            removeItem(row.dataset.detailId);
        } else {
            updateQuantity(row.dataset.detailId, qty);
        }
    });

    function updateQuantity(detailId, quantity) {
        postForm("/cart/update/" + detailId, { quantity: quantity })
            .then(function (data) {
                renderCart(data.cart);
                updateCartBadge(data.cartItemCount);
            })
            .catch(function (err) { showToast(err.message, true); });
    }

    function removeItem(detailId) {
        postForm("/cart/remove/" + detailId, {})
            .then(function (data) {
                renderCart(data.cart);
                updateCartBadge(data.cartItemCount);
                showToast("Đã xóa sản phẩm khỏi giỏ hàng!", false);
            })
            .catch(function (err) { showToast(err.message, true); });
    }

    function renderCart(cart) {
        if (!cart || !cart.items || cart.items.length === 0) {
            emptyState.classList.remove("d-none");
            fullState.classList.add("d-none");
            itemsBody.innerHTML = "";
            return;
        }

        emptyState.classList.add("d-none");
        fullState.classList.remove("d-none");
        itemsBody.innerHTML = cart.items.map(cartItemRowHtml).join("");
        totalPriceEl.textContent = formatVnd(cart.totalPrice);
    }

    function cartItemRowHtml(item) {
        const img = item.productImageUrl ? item.productImageUrl : "/uploads/products/default-food.png";
        return "<tr data-detail-id=\"" + item.id + "\">"
            + "<td><div class=\"d-flex align-items-center gap-2\">"
            + "<img src=\"" + escapeHtml(img) + "\" style=\"width: 50px; height: 50px; object-fit: cover;\" class=\"rounded\">"
            + "<span class=\"fw-medium\">" + escapeHtml(item.productName) + "</span>"
            + "</div></td>"
            + "<td class=\"text-center\">" + formatVnd(item.unitPrice) + "</td>"
            + "<td><div class=\"d-flex align-items-center gap-1\">"
            + "<button type=\"button\" class=\"btn btn-outline-secondary btn-sm js-qty-decrease\"" + (item.quantity <= 1 ? " disabled" : "") + ">-</button>"
            + "<input type=\"number\" class=\"form-control form-control-sm text-center js-qty-input\" style=\"width: 55px;\" value=\"" + item.quantity + "\" min=\"1\" max=\"50\">"
            + "<button type=\"button\" class=\"btn btn-outline-secondary btn-sm js-qty-increase\"" + (item.quantity >= 50 ? " disabled" : "") + ">+</button>"
            + "</div></td>"
            + "<td class=\"text-end fw-bold\">" + formatVnd(item.subtotal) + "</td>"
            + "<td class=\"text-end\">"
            + "<button type=\"button\" class=\"btn btn-outline-danger btn-sm js-remove-item\" title=\"Xóa\"><i class=\"fa-solid fa-trash\"></i></button>"
            + "</td></tr>";
    }

    function formatVnd(amount) {
        return Math.round(Number(amount)).toLocaleString("vi-VN") + " đ";
    }

    function escapeHtml(str) {
        const div = document.createElement("div");
        div.textContent = str == null ? "" : String(str);
        return div.innerHTML;
    }
});
