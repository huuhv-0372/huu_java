# 🔌 API — REST API Cho Trang Người Dùng

REST API dưới prefix `/api/v1`, trả JSON, xác thực bằng **JWT (Bearer token)**. Đây là API chia sẻ để xây trang người dùng dạng SPA/mobile app trong tương lai.

> Quay lại [README tổng quan](../../README.md) · Xem thêm: [Admin](../admin/README.md) · [Web](../web/README.md)

## Tổng Quan

| Thành phần | Vị trí |
|---|---|
| Controllers | `com.huuhv.foodsndrinks.controller.api` (`@RestController`) |
| Security | Chain riêng `apiSecurityFilterChain` (`@Order(2)`, matcher `/api/**`) — **stateless**, CSRF off, `JwtAuthenticationFilter` |
| JWT | `JwtUtil` (jjwt 0.12), HS256, secret từ `JWT_SECRET`, hết hạn sau **1 giờ** (`jwt.expiration: 3600000`) |
| Xử lý lỗi | `ApiExceptionHandler` (`@RestControllerAdvice`) — trả JSON `ErrorResponse` |
| OpenAPI | `OpenApiConfig` + springdoc-openapi 3.x |

## Swagger / OpenAPI

Chỉ bật ở profile **dev** (prod tắt hoàn toàn):

- **Swagger UI:** `http://localhost:8080/api-docs/swagger`
- **OpenAPI JSON:** `http://localhost:8080/api/v1/api-docs`

Swagger có nút **Authorize** (security scheme `bearerAuth`) — dán JWT lấy từ endpoint login để gọi các API cần xác thực.

## Xác Thực

### Đăng nhập lấy token

```
POST /api/v1/auth/login
Content-Type: application/json

{
  "username": "user@example.com",   // username hoặc email
  "password": "secret"
}
```

Response (`AuthResDto`) chứa JWT. Các request sau gửi kèm header:

```
Authorization: Bearer <token>
```

Token sai/thiếu/hết hạn → **401 Unauthorized**. Tài khoản bị khóa → 401 (bắt `DisabledException`).

## Endpoints

### Auth — `AuthApiController` (`/api/v1/auth`)

| Method | Path | Auth | Mô tả |
|---|---|---|---|
| POST | `/api/v1/auth/login` | Public | Đăng nhập bằng username/email + password, trả JWT |

### Products — `ProductApiController` (`/api/v1/products`)

| Method | Path | Auth | Mô tả |
|---|---|---|---|
| GET | `/api/v1/products` | `USER` / `ADMIN` | Danh sách sản phẩm phân trang (chỉ `isAvailable=true`), filter: `name`, `categoryId`, `type` |
| GET | `/api/v1/products/{id}` | `USER` / `ADMIN` | Chi tiết sản phẩm kèm danh sách ảnh |

Response danh sách bọc trong `PageResDto` (nội dung + thông tin phân trang).

### Ví dụ với curl

```bash
# 1. Login
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user@example.com","password":"secret"}'

# 2. Gọi API với token
curl http://localhost:8080/api/v1/products?page=0&size=10 \
  -H "Authorization: Bearer <token>"
```

## Định Dạng Lỗi

`ApiExceptionHandler` trả JSON `ErrorResponse` thống nhất:

| Exception | HTTP status |
|---|---|
| `ResourceNotFoundException` | 404 |
| `IllegalArgumentException` | 400 |
| `MethodArgumentNotValidException` (validation) | 400 — kèm map `field → message` |
| `DuplicateResourceException` | 409 |
| `BadCredentialsException` / `DisabledException` | 401 |
| `Exception` khác | 500 |

Lỗi dispatch cấp servlet (ví dụ 404 URL không tồn tại) do `GlobalErrorController` xử lý — URI prefix `/api/*` vẫn nhận JSON `ErrorResponse`, không bao giờ nhận HTML.

## Quy Ước Khi Thêm API Mới

- Đặt controller trong `controller.api`, base path `/api/v1/...`, thêm `@Tag` để Swagger gom nhóm (springdoc chỉ scan package `controller.api`).
- Request/response dùng DTO trong `dto/request` / `dto/response` — **không expose Entity**.
- Khai báo rule phân quyền cho path mới trong `apiSecurityFilterChain` (`SecurityConfig`).
- Exception nghiệp vụ mới → thêm handler vào `ApiExceptionHandler`, không sửa `GlobalErrorController`.
