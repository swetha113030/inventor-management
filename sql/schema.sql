CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    quantity INT NOT NULL
    );

CREATE INDEX idx_products_category
    ON products(category);

CREATE INDEX idx_products_quantity
    ON products(quantity);

CREATE INDEX idx_products_category_lower
    ON products ((LOWER(category)));

CREATE INDEX idx_products_category_quantity
    ON products(category, quantity);
