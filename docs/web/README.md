# 🛒 Web — Storefront Người Dùng (SSR)

Trang bán hàng cho người dùng cuối, render phía server bằng Thymeleaf + Bootstrap 5 + Vanilla JS. Khách vãng lai xem được menu/sản phẩm; các tính năng giỏ hàng, đặt hàng, đánh giá, góp ý yêu cầu đăng nhập (`ROLE_USER` hoặc `ROLE_ADMIN`).

> Quay lại [README tổng quan](../../README.md) · Xem thêm: [Admin](../admin/README.md) · [API](../api/README.md)

## Tổng Quan

| Thành phần | Vị trí |
|---|---|
| Controllers | `com.huuhv.foodsndrinks.controller.web` |
| Templates | `src/main/resources/templates/web/` (layout: `web/layout/base.html`, Thymeleaf Layout Dialect) |
| Security | Chain `webSecurityFilterChain` (`@Order(3)`) — session-based, form login + OAuth2, remember-me 7 ngày |
| Xử lý lỗi | `WebExceptionHandler` (`@ControllerAdvice` scoped package web) + view `error/{404,409,500}` |
| Model advice | `WebLayoutModelAdvice` — inject `cartItemCount` (badge giỏ hàng trên navbar, 0 với khách) |

## Xác Thực

### Form login & đăng ký — `AuthController`

| Method | Path | Mô tả |
|---|---|---|
| GET | `/login` | Trang đăng nhập (form + nút social login) |
| GET / POST | `/register` | Đăng ký tài khoản (check trùng, check password nhập lại) → redirect `/login` |
| POST | `/logout` | Đăng xuất → `/login?logout=true`, xóa session + remember-me cookie |

Sau khi login: `ADMIN` → `/admin`, `USER` → `/` (hoặc quay lại trang yêu cầu trước đó).

### Social login (OAuth2)

Hỗ trợ **Google** (OIDC), **Facebook**, **Twitter/X** — cấu hình trong `application.yml`, cần set env `*_CLIENT_ID` / `*_CLIENT_SECRET` tương ứng:

- PKCE được bật cho mọi provider (`pkceAuthorizationRequestResolver`).
- Google dùng `email` làm principal key (map tài khoản theo email đã verify); xử lý ở `CustomOidcUserService`.
- Facebook/Twitter xử lý ở `CustomOAuth2UserService`; Twitter là provider cấu hình thủ công (không nằm trong `CommonOAuth2Provider`).
- Provider của tài khoản lưu ở enum `AuthProvider`.

## Chức Năng & Routes

### Menu & sản phẩm — `WebController`

| Method | Path | Auth | Mô tả |
|---|---|---|---|
| GET | `/`, `/menu` | Public | Trang menu: filter, sort, phân trang sản phẩm |
| GET | `/categories/{slug}` | Public | Sản phẩm theo danh mục |
| GET | `/products/{slug}` | Public | Chi tiết sản phẩm + danh sách đánh giá (+ đánh giá của tôi nếu đã login) |
| GET | `/contact` | Public | Trang liên hệ |

### Giỏ hàng & checkout — `CartController`

| Method | Path | Auth | Mô tả |
|---|---|---|---|
| GET | `/cart` | USER | Trang giỏ hàng |
| POST | `/cart/add` | USER | Thêm sản phẩm (AJAX, trả JSON `CartPayloadResDto`) |
| POST | `/cart/update/{detailId}` | USER | Đổi số lượng (AJAX, JSON) |
| POST | `/cart/remove/{detailId}` | USER | Xóa item (AJAX, JSON) |
| POST | `/cart/checkout` | USER | Đặt hàng → redirect `/orders` (đồng thời bắn Slack/email cho admin) |

Các endpoint AJAX vẫn thuộc chain web (session + CSRF token), không phải API JWT.

### Đơn hàng — `OrderController`

| Method | Path | Auth | Mô tả |
|---|---|---|---|
| GET | `/orders` | USER | Lịch sử đơn hàng của tôi (phân trang) |
| GET | `/orders/{id}` | USER | Chi tiết đơn hàng (chỉ đơn của chính mình) |

### Đánh giá sản phẩm — `RatingController`

| Method | Path | Auth | Mô tả |
|---|---|---|---|
| POST | `/products/{slug}/rate` | USER | Gửi đánh giá (số sao + bình luận) → redirect về trang sản phẩm |

Dưới mỗi sản phẩm có nút chia sẻ mạng xã hội (client-side, trong template chi tiết sản phẩm).

### Góp ý — `SuggestionController`

| Method | Path | Auth | Mô tả |
|---|---|---|---|
| GET | `/suggest` | USER | Danh sách góp ý của tôi (phân trang) |
| POST | `/suggest` | USER | Gửi góp ý mới |

### Hồ sơ cá nhân — `ProfileController`

| Method | Path | Auth | Mô tả |
|---|---|---|---|
| GET | `/profile` | USER | Trang hồ sơ (thông tin + đơn gần đây + số item giỏ hàng) |
| GET / POST | `/profile/edit` | USER | Cập nhật hồ sơ (validate, check trùng) |

## Xử Lý Lỗi

- Exception nghiệp vụ (`ResourceNotFoundException` → 404, `DuplicateResourceException` → 409, khác → 500) do `WebExceptionHandler` bắt, render view `error/*`.
- Lỗi dispatch cấp servlet do `GlobalErrorController` xử lý — URI ngoài `/api/*` và `/admin/*` nhận view lỗi web.
- Thông báo trên UI dùng `RedirectAttributes` (flash message).

## Quy Ước Template

- Layout chung `web/layout/base.html` (Thymeleaf Layout Dialect) — trang con chỉ định nghĩa fragment content.
- **Không hardcode domain** hay đường dẫn tuyệt đối — dùng cú pháp context-path của Thymeleaf `@{/duong-dan}`.
- Static assets: `/css`, `/js`, `/images` (trong `src/main/resources/static`), ảnh sản phẩm upload serve qua `/uploads/**`.
