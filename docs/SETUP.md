# Setup Guide

## Prerequisites

- **Amazon Corretto 25 JDK**: [Download](https://aws.amazon.com/corretto/)
- **Maven 3.9+**: [Download](https://maven.apache.org/download.cgi)
- **Docker & Docker Compose**: [Download](https://www.docker.com/get-started)

## Local Development Setup

### 1. Install Prerequisites

Verify installations:

```bash
java -version  # Should show Amazon Corretto 25
mvn -version   # Should show Maven 3.9+
docker --version
docker-compose --version
```

### 2. Start Infrastructure Services

```bash
cd docker
docker-compose up -d
```

Verify services are running:

```bash
docker-compose ps
```

All services should show "healthy" status.

### 3. Configure Environment Variables (Optional)

Create `.env` file in project root:

```env
DB_HOST=localhost
DB_PORT=5432
DB_NAME=example_db
DB_USER=example_user
DB_PASSWORD=example_pass

REDIS_HOST=localhost
REDIS_PORT=6379

KAFKA_BOOTSTRAP_SERVERS=localhost:9092
```

### 4. Run the Application

```bash
mvn spring-boot:run
```

Or with dev profile:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 5. Verify Setup

```bash
# Health check
curl http://localhost:8080/actuator/health

# Should return: {"status":"UP"}
```

## Database Migration

Flyway runs migrations automatically on startup. To manually run:

```bash
mvn flyway:migrate
```

## Running Tests

### Unit Tests

```bash
mvn test
```

### Integration Tests

Requires Docker for Testcontainers:

```bash
mvn verify
```

### With Coverage

```bash
mvn test jacoco:report
# View report: open target/site/jacoco/index.html
```

## Troubleshooting

### Port Already in Use

If port 8080 is in use:

```bash
# Change in application.yml or use environment variable
SERVER_PORT=8081 mvn spring-boot:run
```

### Database Connection Issues

1. Verify PostgreSQL is running: `docker-compose ps postgres`
2. Check connection: `docker-compose exec postgres psql -U example_user -d example_db`
3. Verify credentials in `application.yml`

### Redis Connection Issues

1. Verify Redis is running: `docker-compose ps redis`
2. Test connection: `docker-compose exec redis redis-cli ping`
3. Should return: `PONG`

### Kafka Connection Issues

1. Verify Kafka is running: `docker-compose ps kafka`
2. Check logs: `docker-compose logs kafka`
3. Verify Zookeeper is healthy first

## Environment Variables Reference

| Variable | Default | Description |
|----------|---------|-------------|
| `SERVER_PORT` | 8080 | Application port |
| `DB_HOST` | localhost | PostgreSQL host |
| `DB_PORT` | 5432 | PostgreSQL port |
| `DB_NAME` | example_db | Database name |
| `DB_USER` | example_user | Database user |
| `DB_PASSWORD` | example_pass | Database password |
| `REDIS_HOST` | localhost | Redis host |
| `REDIS_PORT` | 6379 | Redis port |
| `REDIS_PASSWORD` | (empty) | Redis password |
| `KAFKA_BOOTSTRAP_SERVERS` | localhost:9092 | Kafka brokers |
| `OTEL_TRACES_ENDPOINT` | (empty) | OpenTelemetry traces endpoint |
| `OTEL_METRICS_ENDPOINT` | (empty) | OpenTelemetry metrics endpoint |
| `SPRING_PROFILES_ACTIVE` | (default) | Active Spring profiles |
