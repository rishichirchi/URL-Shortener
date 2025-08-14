# URL Shortener Microservice Suite

A scalable, production-ready URL shortener system built with Spring Boot, Spring Cloud Gateway, Redis, and MongoDB.

---

## 1. Project Structure

1. **gateway/**: API Gateway (Spring Cloud Gateway)
2. **application/**: Main URL shortener service
3. **Redis**: In-memory cache and rate limiter
4. **MongoDB**: Persistent storage for URLs and analytics

---

## 2. Quick Start

1. **Start Redis and MongoDB**
    - Redis: `docker run -d --name redis -p 6379:6379 redis:alpine`
    - MongoDB: `docker run -d --name mongo -p 27017:27017 mongo:latest`
2. **Start the main application**
    - `cd application && mvn spring-boot:run` (runs on port 8081)
3. **Start the gateway**
    - `cd gateway && mvn spring-boot:run` (runs on port 8080)
4. **Test endpoints**
    - Shorten: `curl -X POST http://localhost:8080/api/urls/shorten -H "Content-Type: application/json" -d '{"longUrl":"https://example.com"}'`
    - Redirect: `curl -L http://localhost:8080/{shortCode}`

---

## 3. Architecture Overview

1. **Clients** send requests to the API Gateway (port 8080).
2. **Gateway** routes `/api/urls/**` and `/{shortCode}` to the main app (port 8081).
3. **Gateway** applies rate limiting (Redis), validation, and logging.
4. **Main app** persists URLs in MongoDB and caches lookups in Redis.

---

## 4. Scalability Features

### 4.1 Stateless Services
- Both gateway and application are stateless, enabling horizontal scaling (add more instances behind a load balancer).

### 4.2 Externalized State
- All state (URLs, analytics, rate limits) is stored in Redis and MongoDB, not in service memory.

### 4.3 Cache-Aside Pattern
- Application checks Redis for short URL lookups first, then MongoDB, then populates Redis. This reduces DB load and improves latency.

### 4.4 Rate Limiting
- Redis-backed rate limiting at the gateway protects backend services from abuse and burst traffic.
- Different rate limits for API and redirect routes.

### 4.5 Sharding and Partitioning
- Short codes can be generated to support sharding (e.g., hash-based, time-based, or range-based strategies).
- MongoDB can be sharded for very large datasets.
- Redis can be clustered for high throughput.

### 4.6 Reactive Gateway
- Spring Cloud Gateway uses Reactor for efficient, non-blocking I/O, supporting thousands of concurrent connections with minimal resources.

### 4.7 Observability
- Actuator endpoints for health and metrics.
- Micrometer integration for Prometheus/Grafana.
- Centralized logging and distributed tracing recommended for production.

---

## 5. Scaling Each Component

### 5.1 Gateway
1. Deploy multiple stateless gateway instances behind a load balancer (Kubernetes, AWS ALB, etc.).
2. Use Redis Cluster for distributed rate limiting.
3. Tune connection pools and timeouts for downstream services.
4. Autoscale based on request rate, CPU, and latency.

### 5.2 Application
1. Run multiple instances behind the gateway/load balancer.
2. Use Redis for caching and MongoDB for persistence.
3. Tune MongoDB and Redis client pools for high concurrency.
4. Use bounded thread pools or reactive I/O for maximum throughput.

### 5.3 Redis
1. Use Redis Cluster for sharding and high availability.
2. Enable persistence (AOF/RDB) as needed.
3. Monitor memory usage and set TTLs for cache keys.

### 5.4 MongoDB
1. Use replica sets for high availability.
2. Add sharding as dataset grows.
3. Create indexes on `shortCode` and TTL indexes for expiring URLs.

---

## 6. Short Code Generation & Partitioning

1. Use base62 encoding, hash-based, or time-based strategies to avoid global counters.
2. Allocate ID ranges per instance or shard for distributed generation.
3. Ensure idempotency to avoid duplicates during retries.

---

## 7. Resilience & Availability

1. Use retries with exponential backoff for downstream calls.
2. Implement circuit breakers for non-critical dependencies.
3. Gracefully handle shutdowns and drain connections.
4. Apply backpressure in the application for burst traffic.

---

## 8. Observability & Metrics

1. Instrument gateway and app with Micrometer (Prometheus).
2. Track request rate, error rate, latency, Redis hit/miss, DB ops/sec.
3. Use centralized logging (ELK, Loki) and distributed tracing (OpenTelemetry).

---

## 9. Security

1. Terminate TLS at the gateway.
2. Validate input at both gateway and application.
3. Rate limit per IP or API key.
4. Use authentication/authorization (JWT/OAuth) for protected APIs.

---

## 10. CI/CD & Deployment

1. Build container images for each service.
2. Use immutable image tags (avoid `latest` in production).
3. Deploy via CI/CD pipelines (GitHub Actions, GitLab CI, Jenkins).
4. Run integration tests with testcontainers for Redis and MongoDB.

---

## 11. Operational Checklist Before Scaling

1. Load test with tools like k6 or Locust.
2. Test Redis and MongoDB failover scenarios.
3. Validate autoscaling behavior in your environment.
4. Monitor end-to-end latency and error rates under load.

---

## 12. Example Configurations

- **Gateway HTTP client pool and timeouts:**

```properties
spring.cloud.gateway.httpclient.pool.max-connections=200
spring.cloud.gateway.httpclient.connect-timeout=10000
spring.cloud.gateway.httpclient.response-timeout=30s
```

- **Redis TTL for cached URLs:**

```properties
app.cache.url.ttl=86400
```

- **MongoDB indexes:**
  - Ensure an index on `shortCode` and TTL indexes for expiring URLs.

---

## 13. Next Steps / Improvements

1. Add OpenTelemetry tracing for full request visibility.
2. Implement Redis Cluster + Sentinel for HA.
3. Add MongoDB sharding for large datasets.
4. Build analytics pipeline (Kafka) for real-time metrics.
5. Add comprehensive load and chaos testing in CI.

---

For questions or contributions, please open an issue or pull request!
