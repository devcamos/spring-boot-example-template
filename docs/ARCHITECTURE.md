# Architecture Documentation

## System Architecture

The Spring Boot Example Template follows a layered architecture pattern with clear separation of concerns.

## Component Overview

### Web Layer
- **Controllers**: Handle HTTP requests and responses
- **Exception Handlers**: Global exception handling with consistent error responses
- **Filters**: Request/response processing (correlation IDs)

### Business Layer
- **Services**: Business logic and orchestration
- **DTOs**: Data transfer objects for API contracts

### Data Access Layer
- **Repositories**: JPA repositories for database access
- **Entities**: JPA entities representing database tables

### Infrastructure Layer
- **Configuration**: Spring configuration classes
- **Event Handling**: Kafka producers and consumers
- **Caching**: Redis cache configuration
- **HTTP Clients**: WebClient for outbound calls

## Data Flow

1. **Request Flow**:
   - HTTP Request → CorrelationIdFilter → Controller → Service → Repository → Database
   - Errors flow back through GlobalExceptionHandler

2. **Event Flow**:
   - Service → EventProducer → Kafka → EventConsumer → Service

3. **Caching Flow**:
   - Service → Cache (Redis) → Service (if cache miss: Repository → Database)

## Error Handling Strategy

- Domain exceptions thrown by services
- GlobalExceptionHandler maps to HTTP status codes
- All errors include correlation ID for tracing
- Validation errors include field-level details

## Caching Strategy

- Redis with Lettuce (reactive, thread-safe)
- Cache-aside pattern
- TTL: 1 hour
- Cache keys: `examples:{id}`

## Event-Driven Architecture

- Kafka topics: `example-events`, `example-events-dlq`
- Manual acknowledgment for reliability
- DLQ for failed messages
- Retry mechanism with exponential backoff

## Security

- Permit-all by default (customize as needed)
- CORS configuration
- CSRF protection
- Ready for authentication/authorization

## Observability

- **Metrics**: Prometheus format via Actuator, scraped by Prometheus
- **Metrics Visualization**: Grafana dashboards for metrics visualization
- **Tracing**: OpenTelemetry distributed tracing with OTLP exporter
- **Tracing Bridge**: Micrometer tracing bridge for OpenTelemetry integration
- **Metrics Export**: Micrometer OTLP registry for metrics export
- **Logging**: Structured JSON logging (production)
- **Correlation IDs**: Request tracing across services

### Observability Stack

The observability stack includes:

1. **Spring Boot Actuator**: Exposes metrics endpoints (`/actuator/prometheus`)
2. **Prometheus**: Time-series database that scrapes metrics from the application
3. **Grafana**: Visualization platform that queries Prometheus for dashboards
4. **OpenTelemetry**: Distributed tracing with OTLP exporter for trace export
5. **Micrometer**: Metrics collection and export (Prometheus, OTLP)
6. **Structured Logging**: JSON-formatted logs with correlation IDs

### Access Points

- **Prometheus UI**: http://localhost:9090
- **Grafana UI**: http://localhost:3000 (admin/admin)
- **Actuator Endpoints**: http://localhost:8080/actuator

## Testing Strategy

- **Unit Tests**: Service and controller logic
- **Integration Tests**: Testcontainers for real dependencies
- **Error Contract Tests**: Ensure consistent error responses
