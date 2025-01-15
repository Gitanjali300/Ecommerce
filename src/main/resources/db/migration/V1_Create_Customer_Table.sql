-- Ensure schema exists
CREATE SCHEMA IF NOT EXISTS shoppingDB;

-- Create the customer table in shoppingDB schema
CREATE TABLE IF NOT EXISTS shoppingDB.customer (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    address VARCHAR(200) NOT NULL
);

CREATE TABLE IF NOT EXISTS shoppingDB.product (
    id SERIAL PRIMARY KEY,              -- Auto-incrementing ID
    item_number VARCHAR(50) UNIQUE NOT NULL, -- Unique item number for the product
    name VARCHAR(100) NOT NULL,         -- Name of the product
    price DECIMAL(10, 2) NOT NULL,      -- Product price with two decimal places
    item_type VARCHAR(50) NOT NULL,    -- Type of product (e.g., Tech, Beauty)
    rating DECIMAL(3, 2) CHECK (rating >= 0 AND rating <= 5), -- Rating (0 to 5 scale)
    created_at TIMESTAMP DEFAULT NOW(), -- Timestamp of product creation
    updated_at TIMESTAMP DEFAULT NOW()  -- Timestamp of last update
);