-- ==========================================
-- products
-- ==========================================
CREATE INDEX idx_products_name         ON products(name);
CREATE INDEX idx_products_type         ON products(type);
CREATE INDEX idx_products_price        ON products(price);
CREATE INDEX idx_products_rating_avg   ON products(rating_avg);
CREATE INDEX idx_products_category_id  ON products(category_id);
CREATE INDEX idx_products_is_available ON products(is_available);

-- ==========================================
-- product_images
-- ==========================================
CREATE INDEX idx_product_images_product_id ON product_images(product_id);

-- ==========================================
-- orders
-- ==========================================
CREATE INDEX idx_orders_user_id    ON orders(user_id);
CREATE INDEX idx_orders_status     ON orders(status);
CREATE INDEX idx_orders_ordered_at ON orders(ordered_at);  -- for monthly statistics

-- ==========================================
-- order_details
-- ==========================================
CREATE INDEX idx_order_details_order_id   ON order_details(order_id);
CREATE INDEX idx_order_details_product_id ON order_details(product_id);

-- ==========================================
-- ratings
-- ==========================================
CREATE INDEX idx_ratings_product_id ON ratings(product_id);

-- ==========================================
-- suggestions
-- ==========================================
CREATE INDEX idx_suggestions_user_id ON suggestions(user_id);
CREATE INDEX idx_suggestions_status  ON suggestions(status);
