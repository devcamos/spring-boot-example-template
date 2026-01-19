# Version Tracking

This document tracks the current versions of all dependencies and provides links to official version sources.

## Current Versions

| Component | Version | Source |
|-----------|---------|--------|
| Java | Amazon Corretto 25 | [AWS Corretto](https://aws.amazon.com/corretto/) |
| Spring Boot | 3.5.9 | [Spring Boot EOL](https://endoflife.date/spring-boot) |
| PostgreSQL | 18.1 | [PostgreSQL EOL](https://endoflife.date/postgresql) |
| Redis | 7 | [Redis EOL](https://endoflife.date/redis) |
| Kafka | Latest | [Kafka EOL](https://endoflife.date/apache-kafka) |
| Prometheus | Latest | [Prometheus EOL](https://endoflife.date/prometheus) |
| Flyway | 11.19.0 | [Flyway Docs](https://flywaydb.org/documentation/) |
| Testcontainers | 1.21.4 | [Testcontainers Releases](https://github.com/testcontainers/testcontainers-java/releases) |
| OpenTelemetry | 2.1.0 | [OpenTelemetry](https://opentelemetry.io/) |

## Update Schedule

- **Monthly**: Maven dependencies via Dependabot
- **Weekly**: GitHub Actions updates via Dependabot
- **Manual**: Review and update major versions as needed

## Compatibility Matrix

| Spring Boot | Java | PostgreSQL | Redis | Kafka |
|-------------|------|------------|-------|-------|
| 3.5.9 | 17-25 | 12+ | 6+ | 2.8+ |

## End of Life Dates

- **Spring Boot 3.5.9**: OSS support until June 30, 2026; Commercial until June 30, 2032
- **Amazon Corretto 25**: Supported until October 2032

## Links to Version Sources

- Java (Amazon Corretto): https://aws.amazon.com/corretto/
- Spring Boot: https://endoflife.date/spring-boot
- PostgreSQL: https://endoflife.date/postgresql
- Redis: https://endoflife.date/redis
- Kafka: https://endoflife.date/apache-kafka
- Prometheus: https://endoflife.date/prometheus
- Flyway: Official documentation
- Testcontainers: GitHub releases
- OpenTelemetry: https://opentelemetry.io/
