# Orbenox – ERP System

[![CI Pipeline](https://github.com/username/orbenox/actions/workflows/ci.yml/badge.svg)](https://github.com/username/orbenox/actions)
[![Java Version](https://img.shields.io/badge/Java-21-blue.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.2-green.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

A modern, production-ready ERP system built with Spring Boot 4.0.2, featuring resource-action-permission based
authorization, comprehensive API documentation, and containerized deployment.

---

## Table of Contents

- [Features](#features)
- [Quick Start](#quick-start)
- [Architecture](#architecture)
- [Development](#development)
- [Deployment](#deployment)
- [API Documentation](#api-documentation)
- [Security](#security)
- [Testing](#testing)
- [Monitoring](#monitoring)
- [Contributing](#contributing)

---

## Features

### Core Features

- ✅ **Resource-Action-Permission Authorization** - Fine-grained access control
- ✅ **Soft Delete Strategy** - Data preservation and recovery
- ✅ **JWT Authentication** - Secure token-based auth
- ✅ **API Versioning** - URI-based versioning (`/api/v1/...`)
- ✅ **Comprehensive Validation** - Custom validators for business rules
- ✅ **OpenAPI/Swagger Documentation** - Interactive API docs
- ✅ **Caching with Caffeine** - High-performance caching
- ✅ **Database Migrations** - Flyway for schema management
- ✅ **Audit Logging** - Automatic created/updated tracking
- ✅ **Localization Support** - i18n with message bundles

### Technical Features

- ✅ **Docker Support** - Containerized deployment
- ✅ **CI/CD Pipeline** - GitHub Actions workflow
- ✅ **Metrics & Monitoring** - Prometheus + Actuator
- ✅ **Structured Logging** - JSON logging for production
- ✅ **Connection Pooling** - HikariCP optimization
- ✅ **Security Headers** - CSP, HSTS, X-Frame-Options
- ✅ **Test Coverage** - JUnit 5 + Mockito + Testcontainers

---

## Quick Start

### Prerequisites

- **Java 21** or higher
- **Maven 3.9+**
- **PostgreSQL 16** (or use Docker Compose)
- **Docker** (optional, for containerized deployment)

### Clone Repository

```bash
git clone https://github.com/username/orbenox.git
cd orbenox
```

### Environment Setup

1. Copy the example environment file:

```bash
cp .env.example .env
```

2. Edit `.env` and configure your database:

```properties
DB_URL=jdbc:postgresql://localhost:5432/orbenox
DB_USERNAME=postgres
DB_PASSWORD=your_password
JWT_SECRET=your_jwt_secret_minimum_256_bits
```

### Run with Docker Compose (Recommended)

```bash
docker-compose up -d
```

The application will be available at `http://localhost:8080`

### Run Locally

```bash
# Start PostgreSQL (if not using Docker)
# Then run the application
mvn spring-boot:run
```

### Access API Documentation

- **Swagger UI**: http://localhost:8080/swagger
- **API Docs**: http://localhost:8080/v3/api-docs

---

## Architecture

### Technology Stack

**Backend:**

- Spring Boot 4.0.2
- Spring Security 6
- Spring Data JPA
- PostgreSQL 16
- Flyway (migrations)
- MapStruct (mapping)
- Caffeine (caching)

**Observability:**

- Spring Boot Actuator
- Micrometer + Prometheus
- Logback with JSON output

**Testing:**

- JUnit 5
- Mockito
- Testcontainers
- Spring Security Test

### Core Concepts

#### 1. Resource-Action-Permission Model

```
User → Role → Permission (Resource + Action)
```

**Example:**

- User: `john.doe`
- Role: `MANAGER`
- Permission: `PRODUCT:READ`, `PRODUCT:CREATE`, `PRODUCT:UPDATE`

See [docs/permissions.md](web-api/docs/permissions.md) for complete permission matrix.

#### 2. Soft Delete Strategy

All entities use soft deletion:
```java
@Column(nullable = false)
private final boolean deleted = false;
```

Benefits:

- Data recovery
- Audit trail
- Referential integrity

#### 3. DTO Separation

- `CreateDto` - For creating entities
- `UpdateDto` - For updates
- `Item/ResponseDto` - For responses

#### 4. Centralized API Response

```json
{
  "success": true,
  "code": 0,
  "message": "Operation successful",
  "messageKey": "operation.success",
  "data": {  }
}
```

---

## Development

### Project Structure

```
orbenox/
├── src/main/java/com/orbenox/erp/
│   ├── common/          # Shared utilities
│   ├── config/          # Spring configurations
│   ├── domain/          # Business domains
│   │   ├── product/
│   │   ├── currency/
│   │   └── ...
│   ├── security/        # Authentication & authorization
│   ├── exception/       # Global exception handling
│   └── validation/      # Custom validators
├── src/main/resources/
│   ├── db/migration/    # Flyway migrations
│   ├── i18n/            # Localization files
│   └── application-*.properties
└── src/test/            # Tests
```

### Building

```bash
# Build without tests
mvn clean package -DskipTests

# Build with tests
mvn clean verify

# Generate test coverage report
mvn clean verify jacoco:report
```

### Running Tests

```bash
# All tests
mvn test

# Specific test class
mvn test -Dtest=ProductServiceTest

# Integration tests
mvn verify
```

---

## Deployment

### Docker Deployment

#### Build Image

```bash
docker build -t orbenox:latest .
```

#### Run Container

```bash
docker run -d \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_URL=jdbc:postgresql://db-host:5432/orbenox \
  -e DB_USERNAME=postgres \
  -e DB_PASSWORD=secret \
  orbenox:latest
```

#### Docker Compose

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f app

# Stop services
docker-compose down
```

### Production Configuration

Use `application-prod.properties`:

```properties
spring.profiles.active=prod
spring.jpa.show-sql=false
logging.level.root=INFO
management.endpoints.web.exposure.include=health,metrics,prometheus
```

### Environment Variables

| Variable                 | Description       | Default                                    |
|--------------------------|-------------------|--------------------------------------------|
| `DB_URL`                 | Database JDBC URL | `jdbc:postgresql://localhost:5432/orbenox` |
| `DB_USERNAME`            | Database username | `postgres`                                 |
| `DB_PASSWORD`            | Database password | -                                          |
| `JWT_SECRET`             | JWT signing key   | -                                          |
| `SERVER_PORT`            | Application port  | `8080`                                     |
| `SPRING_PROFILES_ACTIVE` | Active profile    | `dev`                                      |

---

## API Documentation

### Swagger UI

Interactive API documentation available at:

- **Dev**: http://localhost:8080/swagger
- **Disabled in production** for security

### API Versioning

All endpoints use URI-based versioning:

```
/api/v1/products
/api/v1/brands
/api/v1/users
```

### Example Requests

#### Get All Products

```bash
curl -X GET http://localhost:8080/api/v1/products \
  -H "Authorization: Bearer <token>"
```

#### Create Product

```bash
curl -X POST http://localhost:8080/api/v1/products \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "enabled": true,
    "code": "PROD001",
    "name": "Product Name",
    "brand": {"id": 1},
    "defaultBarcode": "1234567890"
  }'
```

---

## Security

### Authentication

JWT-based authentication with token expiration.

**Login:**

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "password"}'
```

**Response:**

```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 86400000
  }
}
```

### Authorization

Controllers use `@PreAuthorize`:

```java

@PreAuthorize("hasPermission('PRODUCT', 'READ')")
@GetMapping
public ResponseEntity<Response<List<ProductItem>>> getAll() { }
```

### Security Headers

- `Content-Security-Policy`
- `X-Frame-Options: DENY`
- `Strict-Transport-Security`
- `X-Content-Type-Options`

---

## Testing

### Test Coverage

Run tests with coverage:

```bash
mvn clean verify jacoco:report
```

View report: `target/site/jacoco/index.html`

### Test Types

**Unit Tests:**

```bash
mvn test
```

**Integration Tests:**

```bash
mvn verify
```

**Security Tests:**

```java
@WithMockUser(authorities = {"PRODUCT:READ"})
@Test
void testGetProducts() {  }
```

---

## Monitoring

### Actuator Endpoints

- `/actuator/health` - Health check
- `/actuator/health/liveness` - Liveness probe
- `/actuator/health/readiness` - Readiness probe
- `/actuator/metrics` - Application metrics
- `/actuator/prometheus` - Prometheus metrics
- `/actuator/info` - Application info

### Metrics

Custom business metrics:

- `orbenox.product.created`
- `orbenox.user.login`
- `orbenox.document.posted`
- `orbenox.cache.hit`

### Logging

**Development:**

- Console output with colors
- File logging in `logs/`

**Production:**

- JSON format for log aggregation
- Correlation IDs for request tracking

---

## Database Migrations

### Flyway

Migrations located in `src/main/resources/db/migration/`

**Naming convention:**

- `V1__init_schema.sql`
- `V2__seed.sql`
- `V3__add_indexes.sql`

**Run migrations:**

```bash
mvn flyway:migrate
```

**Migration info:**

```bash
mvn flyway:info
```

---

## Performance Optimizations

### Caching

- **Caffeine** cache with 30-minute TTL
- Cached: Products, Brands, Units, Lookups
- Cache metrics available via Actuator

### Database

- **HikariCP** connection pooling
- Database indexes on foreign keys
- Partial indexes on `deleted` column
- Batch insert/update optimization

### JPA

- `open-in-view: false` to prevent lazy loading issues
- Batch size: 20
- Order inserts and updates

---

## CI/CD

GitHub Actions workflow (`.github/workflows/ci.yml`):

- ✅ Build with Maven
- ✅ Run tests
- ✅ Generate coverage reports
- ✅ Build Docker image
- ✅ Upload artifacts

---

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

---

## Additional Documentation

- [Permissions Matrix](web-api/docs/permissions.md) - Complete permission documentation
- [Architecture Guide](README.md#architecture) - Architectural decisions
- [API Guide](http://localhost:8080/swagger) - Interactive API docs

---

## Philosophy

> Prefer clarity over cleverness.
> Prefer explicit design over hidden magic.
> Abstract only when patterns stabilize.

---

## License

MIT License - see [LICENSE](LICENSE) for details.

---

## Support

For issues and questions:

- GitHub Issues: https://github.com/username/orbenox/issues
- Documentation: [README.md](README.md)

---

_Last updated: 2026-02-12_