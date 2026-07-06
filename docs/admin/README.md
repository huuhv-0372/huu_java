# 🛠 Admin — Trang Quản Trị (SSR)

Trang quản trị hệ thống, render phía server bằng Thymeleaf + Bootstrap 5. Toàn bộ đường dẫn `/admin/**` yêu cầu **`ROLE_ADMIN`**, đăng nhập bằng form login tại `/login` (session-based, hỗ trợ remember-me 7 ngày).

> Quay lại [README tổng quan](../../README.md) · Xem thêm: [API](../api/README.md) · [Web](../web/README.md)

## Tổng Quan

| Thành phần | Vị trí |
|---|---|
| Controllers | `com.huuhv.foodsndrinks.controller.admin` |
| Templates | `src/main/resources/templates/admin/` (layout: `admin/layout/base.html`, dùng Thymeleaf Layout Dialect) |
| Xử lý lỗi | `AdminExceptionHandler` (`@ControllerAdvice` scoped package admin) + view `error/admin/{404,403,405,409,500}` |
| Model advice | `AdminLayoutModelAdvice` — inject `currentPath` và các enum (categoryTypes, productTypes, orderStatuses, roles, suggestionStatuses) vào mọi view admin |

## Chức Năng & Routes

### Dashboard — `AdminController`

| Method | Path | Mô tả |
|---|---|---|
| GET | `/admin`, `/admin/dashboard` | Dashboard thống kê tổng quan |

### Quản lý sản phẩm — `ProductAdminController` (`/admin/products`)

| Method | Path | Mô tả |
|---|---|---|
| GET | `/admin/products` | Danh sách phân trang, filter theo tên / danh mục / loại / trạng thái bán |
| GET | `/admin/products/add` | Form thêm sản phẩm |
| POST | `/admin/products/add` | Tạo sản phẩm, upload nhiều ảnh (`newImages`, tối đa 10MB/file) |
| GET | `/admin/products/edit/{id}` | Form sửa sản phẩm |
| POST | `/admin/products/edit/{id}` | Cập nhật (thêm/xóa ảnh, chọn ảnh đại diện) |
| POST | `/admin/products/{id}/delete` | Xóa sản phẩm |

Ảnh upload lưu tại thư mục `UPLOAD_DIR` (mặc định `./uploads`), serve qua `/uploads/**` (cấu hình trong `WebMvcConfig`).

### Quản lý danh mục — `CategoryAdminController` (`/admin/categories`)

| Method | Path | Mô tả |
|---|---|---|
| GET | `/admin/categories` | Danh sách phân trang + filter |
| GET / POST | `/admin/categories/add` | Thêm danh mục (validate, check trùng) |
| GET / POST | `/admin/categories/edit/{id}` | Sửa danh mục |
| POST | `/admin/categories/{id}/delete` | Xóa danh mục |

### Quản lý đơn hàng — `OrderAdminController` (`/admin/orders`)

| Method | Path | Mô tả |
|---|---|---|
| GET | `/admin/orders` | Danh sách phân trang, filter theo trạng thái / keyword / mã đơn |
| GET | `/admin/orders/{id}` | Chi tiết đơn hàng |
| POST | `/admin/orders/{id}/status` | Cập nhật trạng thái đơn (`OrderStatus`) |

### Quản lý người dùng — `UserAdminController` (`/admin/users`)

| Method | Path | Mô tả |
|---|---|---|
| GET | `/admin/users` | Danh sách phân trang, filter theo keyword / role / trạng thái active |
| GET / POST | `/admin/users/{id}/edit` | Sửa thông tin người dùng |
| POST | `/admin/users/{id}/toggle-active` | Khóa / mở khóa tài khoản |

### Quản lý góp ý — `SuggestionAdminController` (`/admin/suggestions`)

| Method | Path | Mô tả |
|---|---|---|
| GET | `/admin/suggestions` | Danh sách phân trang, filter theo trạng thái / keyword |
| GET | `/admin/suggestions/{id}` | Chi tiết + form sửa |
| POST | `/admin/suggestions/{id}/update` | Lưu chỉnh sửa góp ý |
| POST | `/admin/suggestions/{id}/status` | Cập nhật trạng thái (`SuggestionStatus`) |

## Thông Báo Cho Admin

Admin nhận thông báo tự động (không cần thao tác trên UI):

- **Đơn hàng mới:** sau khi user checkout thành công → gửi **Slack** (incoming webhook) + **email** tới `ADMIN_NOTIFY_EMAIL`. Chạy async (`@Async`), fire sau khi transaction commit (`@TransactionalEventListener`).
- **Báo cáo doanh thu tháng:** `OrderStatisticsScheduler` chạy cron `ORDER_STATS_CRON` (mặc định 23h ngày cuối tháng, giờ VN) — email tổng doanh thu, số đơn hoàn thành, số đơn theo từng trạng thái.

Bật/tắt qua `SLACK_NOTIFY_ENABLED`, `MAIL_NOTIFY_ENABLED`. Xem package `service/notification/`.

## Xử Lý Lỗi

- Exception nghiệp vụ (`ResourceNotFoundException` → 404, `DuplicateResourceException` → 409, `Exception` khác → 500) được `AdminExceptionHandler` bắt và render view `error/admin/*`.
- Lỗi dispatch cấp servlet (404 URL không tồn tại, 403, 405...) do `GlobalErrorController` xử lý — với URI prefix `/admin/*` sẽ trả về view lỗi admin.
- Thông báo thao tác thành công/thất bại trên UI dùng `RedirectAttributes` (flash message).

## Monitoring

Admin đã đăng nhập có thể truy cập Actuator: `/actuator/health` (chi tiết), `/actuator/metrics`, `/actuator/info`, `/actuator/caches` (chỉ dev).
