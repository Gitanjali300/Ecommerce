-- Only When schema exists
CREATE SCHEMA IF NOT EXISTS shoppingDB;

-- Create sequence for shopping_cart id
CREATE SEQUENCE IF NOT EXISTS shoppingDB.shopping_cart_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- Create sequence for product id
CREATE SEQUENCE IF NOT EXISTS shoppingDB.product_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- Create sequence for cart_item id
CREATE SEQUENCE IF NOT EXISTS shoppingDB.cart_item_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- Create the customer table in shoppingDB schema
CREATE TABLE IF NOT EXISTS shoppingDB.customer
(
    id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    address VARCHAR(200) NOT NULL,
    CONSTRAINT customer_pkey PRIMARY KEY (id),
    CONSTRAINT customer_email_key UNIQUE (email)
);

-- Create the shopping_cart table
CREATE TABLE IF NOT EXISTS shoppingDB.shopping_cart
(
    id INTEGER NOT NULL DEFAULT nextval('shoppingDB.shopping_cart_id_seq'),
    customer_id BIGINT NOT NULL,
    CONSTRAINT shopping_cart_pkey PRIMARY KEY (id),
    CONSTRAINT shopping_cart_customer_id_fkey FOREIGN KEY (customer_id)
        REFERENCES shoppingDB.customer (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
);

-- Create the product table
CREATE TABLE IF NOT EXISTS shoppingDB.product
(
    id INTEGER NOT NULL DEFAULT nextval('shoppingDB.product_id_seq'),
    name VARCHAR(100) NOT NULL,
    price NUMERIC(10,2) NOT NULL,
    item_type VARCHAR(50) NOT NULL,
    rating NUMERIC(3,2),
    created_at TIMESTAMP DEFAULT now(),
    updated_at TIMESTAMP DEFAULT now(),
    CONSTRAINT product_pkey PRIMARY KEY (id),
    CONSTRAINT product_rating_check CHECK (rating >= 0 AND rating <= 5)
);

-- Create the cart_item table
CREATE TABLE IF NOT EXISTS shoppingDB.cart_item
(
    id BIGINT NOT NULL DEFAULT nextval('shoppingDB.cart_item_id_seq'),
    shopping_cart_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    CONSTRAINT cart_item_pkey PRIMARY KEY (id),
    CONSTRAINT cart_item_product_id_fkey FOREIGN KEY (product_id)
        REFERENCES shoppingDB.product (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE,
    CONSTRAINT cart_item_shopping_cart_id_fkey FOREIGN KEY (shopping_cart_id)
        REFERENCES shoppingDB.shopping_cart (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
);
