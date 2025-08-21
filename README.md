
# Order & Inventory Management System

[![Java CI](https://github.com/your-username/your-repo/actions/workflows/ci.yml/badge.svg)](https://github.com/your-username/your-repo/actions/workflows/ci.yml)

A mini-Picnic style backend built with **Java 21, Spring Boot 3, PostgreSQL, Docker and Kafka**.  
It lets customers place orders, manage inventory safely under concurrency, and demonstrates modern backend practices.

---

## Features
- REST API for products & orders
- Atomic stock reservations with **optimistic locking**
- Order cancellation restores stock
- OpenAPI docs (`/swagger-ui.html`)
- Dockerized (Postgres + App + optional Kafka)
- Continuous Integration with GitHub Actions ✅

---

## Tech Stack
- **Java 21**, **Spring Boot 3**
- **Spring Data JPA** + **PostgreSQL**
- **Flyway** for DB migrations
- **JUnit 5 + MockMvc** for testing
- **Docker / Docker Compose**
- *(Optional)* Kafka or RabbitMQ for async events
- **GitHub Actions** for CI

---

## Getting Started

### 1. Clone and build
```bash
git clone https://github.com/your-username/your-repo.git
cd your-repo
mvn clean verify


### 2. Run with Docker

```bash
docker compose up --build
```

The API will be available at `http://localhost:8080`.

### 3. Example request

```bash
curl -X POST http://localhost:8080/customers \
  -H "Content-Type: application/json" \
  -d '{
        "email": "alice@example.com",
        "name": "Alice"
      }'
```

---

## Tests

Run tests locally:

```bash
mvn test
```

CI pipeline ensures all tests pass before merging.

---

## Roadmap

* [x] MVP API + DB (Week 1)
* [x] Concurrency & consistency (Week 2)
* [ ] Async worker with Kafka (Week 3)
* [ ] CI + Docker polish (Week 4)

---

## License

MIT License — feel free to use this as a reference project.


