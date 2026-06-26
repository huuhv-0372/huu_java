CREATE TABLE ratings (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT  NOT NULL,
    product_id BIGINT  NOT NULL,
    stars      TINYINT NOT NULL CHECK (stars >= 1 AND stars <= 5),
    comment    TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uq_rating_user_product (user_id, product_id), -- 1 user = 1 rating per product
    FOREIGN KEY (user_id)    REFERENCES users(id)    ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);
