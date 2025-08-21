CREATE TABLE idempotency_key (
  key TEXT PRIMARY KEY,
  request_hash TEXT NOT NULL,
  order_id BIGINT,                 -- set after success
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_idem_created_at ON idempotency_key(created_at);
