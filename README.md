# Payment Processor

A Spring Boot application that processes payment messages from an in-memory queue using multiple worker threads. No external services are required: the queue and database (H2) are in-memory.

## Prerequisites

- Java 17+
- Maven 3.6+

## Build

```bash
mvn clean install
```

## Run

```bash
mvn spring-boot:run
```

The app will:

1. Start an in-memory H2 database and create the `payments` table.
2. Start four worker threads that poll the queue and process messages.
3. Expose REST API at `http://localhost:8080`.

## API

- **Submit a payment for processing** (async; workers will process it):

  ```bash
  curl -X POST http://localhost:8080/api/payments/submit \
    -H "Content-Type: application/json" \
    -d '{"paymentId": "99", "amount": 50.00}'
  ```

  Returns `202 Accepted` with `{"status":"accepted","paymentId":"99"}`.

- **Get the most recent payments** (ordered by creation time, newest first):

  ```bash
  curl -s http://localhost:8080/api/payments/recent | jq
  ```

  Optional query param: `limit` (default 10, max 100). Example: `curl -s http://localhost:8080/api/payments/recent?limit=25 | jq`

  Returns `200 OK` with a JSON array of payment objects (`id`, `paymentId`, `amount`, `createdAt`).

## H2 Console (optional)

When the app is running, you can inspect the database at:

- URL: http://localhost:8080/h2-console  
- JDBC URL: `jdbc:h2:mem:paymentdb`  
- Username: `sa`  
- Password: (leave empty)

Use the H2 console to query `SELECT * FROM payments;` and see payment rows.

## Configuration

- `app.queue.visibility-timeout-seconds` – visibility timeout for received messages (default: 30).
- `app.worker.thread-count` – number of worker threads (default: 4).
- `app.worker.poll-interval-ms` – delay between polls when queue is empty (default: 500).

Config is in `src/main/resources/application.yml`.
