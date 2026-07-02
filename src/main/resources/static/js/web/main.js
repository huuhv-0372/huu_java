// Main Javascript for Web — shared helpers (CSRF, toast, cart badge) + AJAX cart actions.
document.addEventListener("DOMContentLoaded", function () {
    console.log("Web Storefront Loaded!");
    wireAddToCartForms(document);
});

// -------------------------------------------------------
// CSRF
// -------------------------------------------------------

function getCsrfHeaders() {
    const token = document.querySelector('meta[name="_csrf"]');
    const header = document.querySelector('meta[name="_csrf_header"]');
    if (!token || !header) return {};
    return { [header.content]: token.content };
}

// -------------------------------------------------------
// Fetch helper — POST form-encoded data, parse JSON response
// -------------------------------------------------------

function postForm(url, params) {
    const body = new URLSearchParams(params || {});
    return fetch(url, {
        method: "POST",
        headers: Object.assign(
            { "Content-Type": "application/x-www-form-urlencoded" },
            getCsrfHeaders()
        ),
        body: body.toString()
    }).then(function (response) {
        return response.json().catch(function () { return {}; }).then(function (data) {
            if (!response.ok) {
                const message = (data && data.message) ? data.message : "Đã có lỗi xảy ra, vui lòng thử lại!";
                throw new Error(message);
            }
            return data;
        });
    });
}

// -------------------------------------------------------
// Toast notifications (Bootstrap)
// -------------------------------------------------------

function showToast(message, isError) {
    const toastEl = document.getElementById("appToast");
    const bodyEl = document.getElementById("appToastBody");
    if (!toastEl || !bodyEl) return;

    bodyEl.textContent = message;
    toastEl.classList.remove("text-bg-success", "text-bg-danger");
    toastEl.classList.add(isError ? "text-bg-danger" : "text-bg-success");

    const toast = bootstrap.Toast.getOrCreateInstance(toastEl, { delay: 2500 });
    toast.show();
}

// -------------------------------------------------------
// Navbar cart badge
// -------------------------------------------------------

function updateCartBadge(count) {
    const badge = document.getElementById("cartBadge");
    if (!badge) return;
    badge.textContent = count;
    badge.classList.toggle("d-none", !count || count <= 0);
}

// -------------------------------------------------------
// "Add to cart" forms — used on the menu/category listing and product detail pages
// -------------------------------------------------------

function wireAddToCartForms(scope) {
    scope.querySelectorAll(".js-add-to-cart-form").forEach(function (form) {
        if (form.dataset.wired) return;
        form.dataset.wired = "true";

        form.addEventListener("submit", function (e) {
            e.preventDefault();
            const submitBtn = form.querySelector("button[type=submit]");
            const productId = form.querySelector("[name=productId]").value;
            const quantityInput = form.querySelector("[name=quantity]");
            const quantity = quantityInput ? quantityInput.value : 1;

            if (submitBtn) submitBtn.disabled = true;

            postForm("/cart/add", { productId: productId, quantity: quantity })
                .then(function (data) {
                    updateCartBadge(data.cartItemCount);
                    showToast("Đã thêm vào giỏ hàng!", false);
                })
                .catch(function (err) {
                    showToast(err.message, true);
                })
                .finally(function () {
                    if (submitBtn) submitBtn.disabled = false;
                });
        });
    });
}
