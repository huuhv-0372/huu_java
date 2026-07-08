# 🍔 F&B Store — Foods & Drinks

Mock project website bán đồ ăn & thức uống, xây dựng bằng Spring Boot. Dự án gồm **3 phần** chạy chung trong 1 ứng dụng:

| Phần | Đường dẫn | Mô tả | Tài liệu chi tiết |
|---|---|---|---|
| **Admin** | `/admin/**` | Trang quản trị SSR (Thymeleaf): dashboard, quản lý sản phẩm, danh mục, đơn hàng, người dùng, góp ý | [docs/admin/README.md](docs/admin/README.md) |
| **API** | `/api/v1/**` | REST API (JWT) chia sẻ để xây trang người dùng (SPA/mobile) | [docs/api/README.md](docs/api/README.md) |
| **Web** | `/`, `/menu`, ... | Storefront SSR cho người dùng cuối: xem menu, giỏ hàng, đặt hàng, đánh giá sản phẩm | [docs/web/README.md](docs/web/README.md) |

## Tech Stack

- **Ngôn ngữ:** Java 25
- **Framework:** Spring Boot 4.x (`spring-boot-starter-parent` 4.0.7)
- **Build tool:** Maven (Maven Wrapper đi kèm)
- **Database:** MySQL 8 + Spring Data JPA / Hibernate
- **Migration:** Flyway (`src/main/resources/db/migration`)
- **View:** Thymeleaf + Thymeleaf Layout Dialect, Bootstrap 5, Vanilla JS
- **Security:** Spring Security — Form Login + OAuth2 (Google/Facebook/Twitter) cho Web, JWT (jjwt 0.12) cho API
- **Khác:** Lombok, ModelMapper, Caffeine Cache, Spring Mail (Mailtrap), Slack Incoming Webhook, springdoc-openapi 3.x (Swagger UI), Actuator

## Prerequisites

- **JDK 25** (pom.xml pin `<java.version>25</java.version>` — JDK 17/21 sẽ không build được)
- **MySQL 8.x** đang chạy tại `localhost:3306` (hoặc override qua env var)
- Không cần cài Maven — dùng Maven Wrapper (`mvnw.cmd` trên Windows, `./mvnw` trên Mac/Linux)

## Quick Start

1. **Tạo database:**

   ```sql
   CREATE DATABASE foodsndrinks CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

2. **Khai báo biến môi trường bắt buộc** (dev profile không có default cho DB credentials):

   ```bash
   # Windows (PowerShell)
   $env:DB_USERNAME = "root"
   $env:DB_PASSWORD = "your-password"

   # Mac/Linux
   export DB_USERNAME=root
   export DB_PASSWORD=your-password
   ```

3. **Chạy ứng dụng** (Flyway tự tạo bảng khi khởi động):

   ```bash
   # Windows
   mvnw.cmd spring-boot:run

   # Mac/Linux
   ./mvnw spring-boot:run
   ```

4. Truy cập:
   - Web (người dùng): http://localhost:8080
   - Admin: http://localhost:8080/admin (cần tài khoản `ROLE_ADMIN`)
   - Swagger UI (chỉ dev): http://localhost:8080/api-docs/swagger

**Các lệnh khác:**

```bash
./mvnw test                        # Chạy unit test
./mvnw clean package -DskipTests   # Build file JAR (bỏ qua test)
./mvnw clean                       # Clean dự án
```

## Project Structure

Kiến trúc 3 lớp (3-Tier), package gốc `com.huuhv.foodsndrinks`:

```
src/main/java/com/huuhv/foodsndrinks/
├── config/          # SecurityConfig, WebMvcConfig, AsyncConfig, OpenApiConfig, AppConfig
├── controller/
│   ├── admin/       # Controller trang quản trị (SSR)  → docs/admin/README.md
│   ├── api/         # REST controller /api/v1/**       → docs/api/README.md
│   ├── web/         # Controller storefront (SSR)      → docs/web/README.md
│   └── GlobalErrorController.java  # Xử lý lỗi dispatch cấp servlet (/error)
├── dto/
│   ├── request/     # DTO hứng dữ liệu vào (không dùng Entity cho View/API)
│   └── response/    # DTO trả ra
├── entity/          # User, Product, ProductImage, Category, Order, OrderDetail, Rating, Suggestion
├── enums/           # Role, AuthProvider, CategoryType, ProductType, OrderStatus, SuggestionStatus
├── exception/       # AdminExceptionHandler, WebExceptionHandler, ApiExceptionHandler + custom exceptions
├── repository/      # Spring Data JPA repositories
├── security/        # JwtUtil, JwtAuthenticationFilter, CustomUserDetailsService, OAuth2 user services
├── service/         # Toàn bộ business logic (Controller chỉ gọi Service)
│   └── notification/  # Slack/Email notification, order event listener, statistics scheduler
└── utils/           # SlugUtils...

src/main/resources/
├── db/migration/    # Flyway migrations
├── templates/       # Thymeleaf (admin/, web/, error/)
├── static/          # CSS, JS, images
└── application*.yml # Cấu hình theo profile
```

**Quy ước quan trọng:** Constructor Injection qua `@RequiredArgsConstructor` + `private final` (không dùng field injection); Controller mỏng, logic nằm ở Service; query quan hệ dùng `LEFT JOIN FETCH` chống N+1. Xem thêm `CLAUDE.md`.

## Configuration

Profile mặc định là `dev` (đổi qua env `SPRING_PROFILES_ACTIVE=prod` khi deploy).

| File | Vai trò |
|---|---|
| `application.yml` | Cấu hình chung: mail (SMTP), OAuth2 client (Google/Facebook/Twitter), Slack webhook, cron thống kê |
| `application-dev.yml` | Datasource local, logging DEBUG, Swagger UI bật, JWT secret fallback, upload dir |
| `application-prod.yml` | Datasource từ env (bắt buộc), logging ra file `logs/foodsndrinks.log`, **Swagger tắt hoàn toàn** |

### Biến môi trường

| Biến | Bắt buộc | Mô tả |
|---|---|---|
| `DB_USERNAME` / `DB_PASSWORD` | ✅ | Tài khoản MySQL |
| `DB_FOODSNDRINKS_URL` | Prod | JDBC URL (dev mặc định `jdbc:mysql://localhost:3306/foodsndrinks`) |
| `JWT_SECRET` | Prod | Secret ký JWT, ≥ 32 ký tự (dev có fallback) |
| `REMEMBER_ME_KEY` | Prod | Key cho remember-me cookie (dev tự sinh random) |
| `GOOGLE_CLIENT_ID` / `GOOGLE_CLIENT_SECRET` | Khi dùng login Google | OAuth2 credentials từ Google Console |
| `FACEBOOK_CLIENT_ID` / `FACEBOOK_CLIENT_SECRET` | Khi dùng login Facebook | OAuth2 credentials |
| `TWITTER_CLIENT_ID` / `TWITTER_CLIENT_SECRET` | Khi dùng login Twitter/X | OAuth2 credentials |
| `MAIL_HOST` / `MAIL_PORT` / `MAIL_USERNAME` / `MAIL_PASSWORD` | Khi gửi mail thật | SMTP (mặc định Mailtrap sandbox) |
| `ADMIN_NOTIFY_EMAIL` | — | Email nhận thông báo đơn hàng & báo cáo tháng |
| `SLACK_WEBHOOK_URL` / `SLACK_NOTIFY_ENABLED` | — | Slack incoming webhook thông báo đơn mới |
| `MAIL_NOTIFY_ENABLED` | — | Bật/tắt email thông báo |
| `ORDER_STATS_CRON` | — | Cron gửi báo cáo tháng (mặc định `0 0 23 L * *`, 23h ngày cuối tháng, giờ VN) |
| `UPLOAD_DIR` | — | Thư mục lưu ảnh upload (mặc định `./uploads`) |
| `SERVER_PORT` | — | Port (mặc định 8080) |

## Database Migrations

Dùng **Flyway**, tự chạy khi ứng dụng khởi động. File đặt tại `src/main/resources/db/migration` với quy ước tên `V<yyyyMMddHHmmss>__<mo_ta>.sql`:

```
V20260624150700__create_table_users.sql
V20260624150710__create_table_categories.sql
V20260624150720__create_table_products.sql
V20260624150730__create_table_product_images.sql
V20260624150740__create_table_orders.sql
V20260624150750__create_table_order_details.sql
V20260624150760__create_table_ratings.sql
V20260624150770__create_table_suggestions.sql
V20260624150780__create_indexes.sql
V20260702000001__alter_users_phone_nullable.sql
```

Quy ước: tên bảng số nhiều (`users`, `products`...). **Không sửa migration đã chạy** — muốn đổi schema thì thêm file version mới.

## Notification & Batch Job

- **Đơn hàng mới:** sau khi checkout commit thành công (`OrderPlacedEvent` + `@TransactionalEventListener`), hệ thống gửi **Slack** + **email** cho admin (chạy async qua thread pool `notify-*`).
- **Báo cáo tháng:** `OrderStatisticsScheduler` chạy theo cron (mặc định 23h ngày cuối tháng, `Asia/Ho_Chi_Minh`) — tổng hợp doanh thu, số đơn theo trạng thái và email cho admin.

## Xử Lý Lỗi

- Mỗi luồng có `@ControllerAdvice` riêng cùng package: `AdminExceptionHandler` (view lỗi admin), `WebExceptionHandler` (view lỗi web), `ApiExceptionHandler` (JSON `ErrorResponse`).
- `GlobalErrorController` (`/error`) chỉ xử lý lỗi dispatch cấp servlet (404/403/405/409 và exception thoát khỏi mọi advice) — tự chọn trả JSON hay view theo prefix URI.

## Monitoring

- Actuator: `/actuator/health` public; các endpoint khác (`info`, `metrics`, `caches` — chỉ dev) yêu cầu `ROLE_ADMIN`. Prod chỉ expose `health`.

## Tài Liệu Chi Tiết

- 🛠 [Admin — trang quản trị](docs/admin/README.md)
- 🔌 [API — REST API cho trang người dùng](docs/api/README.md)
- 🛒 [Web — storefront người dùng](docs/web/README.md)
