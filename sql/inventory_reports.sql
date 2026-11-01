USE inventory_db;

-- Products with fewer than 5 units
SELECT name, quantity
FROM products
WHERE quantity < 5
ORDER BY quantity ASC;

-- Total inventory value
SELECT SUM(price * quantity) AS inventory_value
FROM products;

-- Inventory summary by category
SELECT category,
       COUNT(*) AS product_count,
       SUM(quantity) AS total_units,
       SUM(price * quantity) AS category_value
FROM products
GROUP BY category
ORDER BY category_value DESC;

-- Average price by category
SELECT category, AVG(price) AS average_price
FROM products
GROUP BY category;
