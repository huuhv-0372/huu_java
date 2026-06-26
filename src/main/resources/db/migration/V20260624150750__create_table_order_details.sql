CREATE TABLE order_details (
    id          BIGINT        AUTO_INCREMENT PRIMARY KEY,
    order_id    BIGINT        NOT NULL,
    product_id  BIGINT        NOT NULL,
    quantity    INT           NOT NULL DEFAULT 1,
    unit_price  DECIMAL(10,2) NOT NULL,                  -- snapshot price at order time
    subtotal    DECIMAL(10,2) NOT NULL,                  -- quantity * unit_price (stored for reporting)
    FOREIGN KEY (order_id)   REFERENCES orders(id)   ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);
