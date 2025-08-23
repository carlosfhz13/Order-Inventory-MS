# Order & Inventory Management System

[![Java CI](https://github.com/carlosfhz13/Order-Inventory-MS/actions/workflows/ci.yml/badge.svg)](https://github.com/carlosfhz13/Order-Inventory-MS/actions/workflows/ci.yml)

A mini-Picnic style backend built with **Java 21, Spring Boot 3, PostgreSQL, Docker, and Kafka**.  
Customers can place orders against a product catalog while stock is kept consistent under concurrency. The system also publishes `order.created` events for async processing.

---

## How to run

### One command (app + deps in Docker)
```bash
docker compose up --build
```
This builds the app image and starts **app + Postgres + Kafka**. The app uses the `docker` profile (datasource `db:5432`, kafka `kafka:29092`).

---

## Quick curl examples

### 1) Create an order
Use a SKU and quantity for each item. (Make sure the product exists; e.g., a product with `sku="SKU1"` and sufficient stock.)

```bash
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -H "Idempotency-Key: 1a2b3c-unique-key" \
  -d '{
        "email": "alice@example.com",
        "items": [
          { "sku": "SKU1", "quantity": 2 }
        ]
      }'
```

**Response (201):**
```json
{{
  "id": 123,
  "status": "CREATED",
  "totalPriceDollars": 20,
  "items": [{{ "sku": "SKU1", "quantity": 2 }}]
}}
```

### 2) Cancel an order (restore stock)
```bash
curl -X PUT http://localhost:8080/orders/change-status/123 \
  -H "Content-Type: application/json" \
  -d '{ "status": "CANCELLED" }'
```

### 3) Get order by id
```bash
curl http://localhost:8080/orders/123
```

### 4) List orders by email
```bash
curl "http://localhost:8080/orders/by-email?email=alice@example.com"
```

> The API accepts an `Idempotency-Key` header on create; repeated requests with the same key will return the same response instead of creating duplicates.

---

## Concurrency & consistency

- **Atomic stock reservation** in a single transaction when creating an order.  
- **Optimistic locking** on `Product` (JPA `@Version`) to prevent overselling under concurrent requests; the service retries on `OptimisticLockException` a few times.
- **Cancellation** increments stock back in the same transaction.  
- **Tests** include a concurrent-order scenario (10 threads) to prove **no oversell** and that `final_sold + remaining_stock == initial_stock`.

---

## Event-driven add-on

- After a successful order commit, the service **publishes `order.created`** with `{ orderId, email, total }` via Spring Kafka.
- A consumer (`OrderCreatedConsumer`) handles the event (e.g., log/send confirmation/write projection).  

**Serialization**
- Producer: `JsonSerializer` (sends JSON)  
- Consumer: `ErrorHandlingDeserializer` + `JsonDeserializer` for `OrderCreatedEvent`

**Integration tests**
- Use **EmbeddedKafka** to verify publish/consume and to avoid requiring a real broker in CI.

---

## Tech stack

- **Java 21**, **Spring Boot 3**
- **Spring Data JPA** + **PostgreSQL** (DDL via **Flyway**)
- **JUnit 5**, **Spring Boot Test**, **MockMvc**, **EmbeddedKafka**
- **Docker / Docker Compose**
- **Spring Kafka**, **Actuator**, **springdoc-openapi**

---

## CI

GitHub Actions runs on each push/PR:

- Build & tests (`mvn verify`)
- Status badge at the top of this README

[![Java CI](https://github.com/carlosfhz13/Order-Inventory-MS/actions/workflows/ci.yml/badge.svg)](https://github.com/carlosfhz13/Order-Inventory-MS/actions/workflows/ci.yml)

---

## Roadmap

- [x] Week 1 — MVP API + DB
- [x] Week 2 — Concurrency & consistency
- [x] Week 3 — Async events with Kafka
- [x] Week 4 — Docker polish, observability, docs demo GIF

---

## License

MIT — feel free to use this as a reference project.
