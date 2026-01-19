# Spring Boot Example Template

A production-ready Spring Boot template with comprehensive features for building scalable, maintainable applications.

## Features

- **Spring Boot 3.5.9** with Amazon Corretto 25
- **PostgreSQL 18.1** with Flyway migrations
- **Redis caching** with Lettuce (default implementation)
- **Kafka** event-driven architecture with DLQ support
- **REST API** with pagination and comprehensive error handling
- **OpenTelemetry** observability with Prometheus metrics
- **Testcontainers** for integration testing
- **GitHub Actions** CI/CD with test coverage reporting
- **Docker** multi-stage builds and docker-compose setup

## Technology Stack

- **Java**: Amazon Corretto 25 (LTS) - [Source](https://aws.amazon.com/corretto/)
- **Spring Boot**: 3.5.9 - [EOL Info](https://endoflife.date/spring-boot)
- **PostgreSQL**: 18.1 - [EOL Info](https://endoflife.date/postgresql)
- **Redis**: 7 - [EOL Info](https://endoflife.date/redis)
- **Kafka**: Latest - [EOL Info](https://endoflife.date/apache-kafka)
- **Prometheus**: Latest - [EOL Info](https://endoflife.date/prometheus)

## How to Run Locally

### Prerequisites

- Amazon Corretto 25 JDK installed
- Maven 3.9+
- Docker and Docker Compose

### Step 1: Clone the Repository

```bash
git clone <repository-url>
cd spring-boot-example-template
```

### Step 2: Start Dependencies with Docker Compose

```bash
cd docker
docker-compose up -d
```

This will start:
- PostgreSQL on port 5432
- Redis on port 6379
- Kafka on port 9092
- Zookeeper on port 2181

Wait for all services to be healthy (check with `docker-compose ps`).

### Step 3: Run Database Migrations

Migrations run automatically on application startup via Flyway.

### Step 4: Start the Application

```bash
mvn spring-boot:run
```

Or with a specific profile:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Step 5: Verify Health Endpoints

```bash
# Health check
curl http://localhost:8080/actuator/health

# Metrics
curl http://localhost:8080/actuator/metrics

# Prometheus metrics
curl http://localhost:8080/actuator/prometheus
```

### Step 6: Access API Documentation

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/api-docs

## Development Setup

### Running Tests

```bash
# Unit tests
mvn test

# Integration tests (requires Docker for Testcontainers)
mvn verify

# With coverage report
mvn test jacoco:report
```

### Building the Application

```bash
mvn clean package
```

The JAR will be created in `target/example-template-1.0.0.jar`.

### Running with Docker

```bash
# Build image
docker build -f docker/Dockerfile -t example-template:latest .

# Run container
docker run -p 8080:8080 \
  -e DB_HOST=host.docker.internal \
  -e REDIS_HOST=host.docker.internal \
  -e KAFKA_BOOTSTRAP_SERVERS=host.docker.internal:9092 \
  example-template:latest
```

## Project Structure

```
src/main/java/com/example/template/
├── config/          # Configuration classes
├── controller/      # REST controllers
├── service/         # Business logic
├── repository/      # Data access
├── entity/          # JPA entities
├── dto/             # Data transfer objects
├── exception/       # Custom exceptions
├── event/           # Kafka event handling
├── web/             # Filters and web components
├── client/          # HTTP clients
└── security/        # Security configuration
```

## API Endpoints

- `GET /api/v1/examples` - List all examples (paginated)
- `GET /api/v1/examples/{id}` - Get example by ID
- `POST /api/v1/examples` - Create new example
- `PUT /api/v1/examples/{id}` - Update example
- `DELETE /api/v1/examples/{id}` - Delete example

All endpoints include correlation IDs in response headers (`X-Correlation-Id`).

## Error Handling

All errors follow a consistent format:

```json
{
  "timestamp": "2025-01-18T10:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Resource not found",
  "correlationId": "uuid-here",
  "path": "/api/v1/examples/999"
}
```

Validation errors include a `details` array with field-level errors.

## Configuration

Configuration files:
- `application.yml` - Base configuration
- `application-dev.yml` - Development overrides
- `application-prod.yml` - Production settings
- `application-test.yml` - Test configuration

Environment variables:
- `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`
- `REDIS_HOST`, `REDIS_PORT`, `REDIS_PASSWORD`
- `KAFKA_BOOTSTRAP_SERVERS`
- `OTEL_TRACES_ENDPOINT`, `OTEL_METRICS_ENDPOINT`

## Testing

- **Unit Tests**: Mockito-based service and controller tests
- **Integration Tests**: Testcontainers for PostgreSQL, Kafka, Redis
- **Error Contract Tests**: Ensure consistent error responses

## CI/CD

GitHub Actions workflow:
- Runs on every PR
- Executes unit and integration tests
- Generates coverage reports
- Comments coverage on PRs
- Builds Docker image

## Documentation

- [Setup Guide](docs/SETUP.md) - Detailed setup instructions
- [Architecture](docs/ARCHITECTURE.md) - System architecture
- [Version Tracking](docs/VERSION_TRACKING.md) - Dependency versions
- [Version Comparison](docs/VERSION_COMPARISON.md) - How to compare versions
- [System Designs](docs/SYSTEM_DESIGNS.md) - Architecture patterns

## License

MIT License - see [LICENSE](LICENSE) file for details
