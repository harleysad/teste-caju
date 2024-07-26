-- Create user table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL
);

-- Create user wallet table
CREATE TABLE user_balances (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    mcc VARCHAR(255) NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    user_id BIGINT NOT NULL,
    UNIQUE (user_id, mcc),
    CONSTRAINT fk_user_balances
        FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Create transaction table
CREATE TABLE transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    mcc VARCHAR(255) NOT NULL,
    merchant VARCHAR(255) NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_user
        FOREIGN KEY (user_id) REFERENCES users(id)
);