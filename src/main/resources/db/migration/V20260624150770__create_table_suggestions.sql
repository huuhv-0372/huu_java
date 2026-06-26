CREATE TABLE suggestions (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT NOT NULL,
    content    TEXT   NOT NULL,
    status     ENUM('PENDING','REVIEWED','APPROVED','REJECTED') NOT NULL DEFAULT 'PENDING',
    admin_note TEXT,                                     -- admin feedback / response
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
