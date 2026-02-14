CREATE TABLE IF NOT EXISTS cards (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    identifier VARCHAR(1) NOT NULL,
    number_in_batch VARCHAR(6) NOT NULL,
    card_number VARCHAR(19) NOT NULL
);
