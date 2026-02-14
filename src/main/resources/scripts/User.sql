CREATE TABLE IF NOT EXISTS users
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NULL,
    verification_code VARCHAR(6) NULL,
    role BIGINT NOT NULL DEFAULT 1
);

INSERT INTO users (email, verification_code, role) VALUES ('admin@hyperativa.com', '123456', 1) ON DUPLICATE KEY UPDATE email=email;