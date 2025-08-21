-- Products (with optimistic locking)
CREATE TABLE products (
  id BIGSERIAL PRIMARY KEY,
  sku TEXT NOT NULL UNIQUE,
  name TEXT NOT NULL,
  price_cents INT NOT NULL CHECK (price_cents >= 0),
  stock INT NOT NULL CHECK (stock >= 0),
  version BIGINT NOT NULL DEFAULT 0
);

-- Customers
CREATE TABLE customers (
  id BIGSERIAL PRIMARY KEY,
  email TEXT NOT NULL UNIQUE,
  name  TEXT NOT NULL
);

-- Orders
CREATE TABLE orders (
  id BIGSERIAL PRIMARY KEY,
  customer_id BIGINT NOT NULL REFERENCES customers(id),
  status TEXT NOT NULL CHECK (status IN ('CREATED','CONFIRMED','CANCELLED')),
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Order items (composite PK mapped via @EmbeddedId: (orderId, productId))
CREATE TABLE order_items (
  id BIGSERIAL PRIMARY KEY,

  order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,

  product_id BIGINT NOT NULL REFERENCES products(id),

  quantity INT NOT NULL CHECK (quantity > 0),

  UNIQUE (order_id, product_id)
);


-- Helpful indexes
CREATE INDEX idx_orders_customer ON orders(customer_id);
CREATE INDEX idx_orders_created_at ON orders(created_at);
