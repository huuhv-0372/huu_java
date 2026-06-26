CREATE TABLE products (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_id  BIGINT,
    name         VARCHAR(255)  NOT NULL,
    slug         VARCHAR(255)  NOT NULL UNIQUE,           -- for SEO / filter by alphabet
    type         ENUM('FOOD','DRINK') NOT NULL,
    price        DECIMAL(10,2) NOT NULL,
    description  TEXT,
    rating_avg   DECIMAL(3,2)  NOT NULL DEFAULT 0.00,     -- cached average, updated on each rating
    rating_count INT           NOT NULL DEFAULT 0,        -- total number of ratings
    is_available BOOLEAN       NOT NULL DEFAULT TRUE,     -- Admin can hide products
    created_at   TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL
);
