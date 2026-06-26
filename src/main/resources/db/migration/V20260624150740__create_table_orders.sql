CREATE TABLE orders (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id          BIGINT        NOT NULL,
    total_price      DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    status           ENUM('CART','PENDING','PROCESSING','COMPLETED','CANCELLED')
                                   NOT NULL DEFAULT 'CART',
    shipping_address VARCHAR(500),
    note             TEXT,                                -- customer note on order
    ordered_at       TIMESTAMP     NULL,                 -- set when CART → PENDING
    created_at       TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
