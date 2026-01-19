# Version Comparison Guide

This guide explains how to compare versions and ensure no mismatches in dependencies.

## Purpose

Ensure all dependencies are using compatible and up-to-date versions while maintaining stability.

## Version Sources

All version sources are documented in [VERSION_TRACKING.md](VERSION_TRACKING.md).

## Comparison Process

1. **Check Current Versions**: Review `pom.xml` for current dependency versions
2. **Visit Official Sources**: Use the links in VERSION_TRACKING.md to check latest versions
3. **Compare Versions**: Compare current vs. latest versions
4. **Check Compatibility**: Verify compatibility with Spring Boot version
5. **Update if Compatible**: Update versions (compile issues are acceptable during transition)

## Compatibility Checking

### Spring Boot Compatibility

Spring Boot 3.5.9 supports:
- Java 17-25
- PostgreSQL 12+
- Redis 6+
- Kafka 2.8+

### Database Driver Compatibility

- PostgreSQL driver version should match PostgreSQL server version
- Check [PostgreSQL JDBC Driver](https://jdbc.postgresql.org/download/) for compatibility

### Testcontainers Compatibility

- Testcontainers 1.21.4 is compatible with Spring Boot 3.5.9
- Check [Testcontainers Compatibility](https://www.testcontainers.org/supported_docker_environment/) for Docker requirements

### OpenTelemetry Compatibility

- OpenTelemetry Spring Boot Starter should be compatible with Spring Boot version
- Check [OpenTelemetry Documentation](https://opentelemetry.io/docs/instrumentation/java/automatic/spring-boot/) for version matrix

## Mismatch Detection

### How to Identify Version Mismatches

1. **Maven Dependency Tree**:
   ```bash
   mvn dependency:tree
   ```

2. **Version Conflicts**:
   ```bash
   mvn dependency:tree -Dverbose
   ```

3. **Outdated Dependencies**:
   ```bash
   mvn versions:display-dependency-updates
   ```

### Common Compatibility Issues

- **Java Version**: Ensure Java version matches Spring Boot requirements
- **Database Driver**: Mismatch between driver and database server versions
- **Kafka Client**: Version mismatch between Kafka broker and client
- **Spring Boot Parent**: Transitive dependencies managed by parent POM

### Resolution Strategies

1. **Update Spring Boot Parent**: Often resolves multiple dependency conflicts
2. **Explicit Version Override**: Use `<version>` in dependency declaration
3. **Dependency Exclusions**: Exclude conflicting transitive dependencies
4. **BOM (Bill of Materials)**: Use BOMs for version management (e.g., Testcontainers BOM)

## Update Strategy

### Monthly Updates via Dependabot

- Dependabot creates PRs monthly for Maven dependencies
- Review and test each PR before merging
- Check for breaking changes in release notes

### Manual Version Comparison

1. Check current version in `pom.xml`
2. Visit official source (endoflife.date or project website)
3. Compare with latest version
4. Review release notes for breaking changes
5. Update version in `pom.xml`
6. Run tests: `mvn clean test`
7. Fix any compilation errors
8. Run integration tests: `mvn verify`

### Testing After Version Updates

1. **Unit Tests**: `mvn test`
2. **Integration Tests**: `mvn verify` (requires Docker)
3. **Manual Testing**: Start application and verify endpoints
4. **Check Logs**: Verify no deprecation warnings or errors

### Handling Compile Errors During Updates

**Note**: Compile issues during version updates are expected and acceptable. The goal is to use latest compatible versions, and temporary compilation errors during transition are fine.

1. Review error messages
2. Check migration guides in release notes
3. Update code to match new API
4. Test thoroughly after fixes

## Tools

### Maven Commands

```bash
# View dependency tree
mvn dependency:tree

# Check for updates
mvn versions:display-dependency-updates

# Check for plugin updates
mvn versions:display-plugin-updates

# Resolve conflicts
mvn dependency:analyze
```

### Version Conflict Detection

Maven will show version conflicts in the dependency tree. Look for:
- Multiple versions of the same dependency
- Warnings about version conflicts
- Exclusions in dependency declarations

### Compatibility Checking Commands

```bash
# Verify Java version
java -version

# Check Spring Boot version
mvn dependency:tree | grep spring-boot-starter-parent

# Verify database driver
mvn dependency:tree | grep postgresql
```

## Best Practices

1. **Keep Spring Boot Parent Updated**: Most dependencies are managed here
2. **Use BOMs**: For libraries like Testcontainers
3. **Review Release Notes**: Before major version updates
4. **Test Thoroughly**: After any version update
5. **Document Changes**: Update VERSION_TRACKING.md when versions change
