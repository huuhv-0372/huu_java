CREATE TABLE users (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50)   NOT NULL UNIQUE,
    email         VARCHAR(255)  NOT NULL UNIQUE,
    password      VARCHAR(255),                           -- NULL when using OAuth
    full_name     VARCHAR(100)  NOT NULL,
    phone         VARCHAR(20)   NOT NULL UNIQUE,
    avatar_url    VARCHAR(500),
    role          ENUM('ROLE_USER','ROLE_ADMIN') NOT NULL DEFAULT 'ROLE_USER',
    auth_provider ENUM('LOCAL','GOOGLE','FACEBOOK','TWITTER') NOT NULL DEFAULT 'LOCAL',
    provider_id   VARCHAR(100),                           -- OAuth provider user ID
    is_active     BOOLEAN NOT NULL DEFAULT TRUE,          -- Admin can disable accounts
    created_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
