# Orbenox ERP

Orbenox is a Spring Boot + Vue microservice platform for ERP and inventory workflows. The repository contains several independently runnable services plus a Vue frontend.

## Current architecture

- `web-api`: main backend application with Spring Security, JPA, Redis, RabbitMQ, Auth0 client-credentials flow, and OpenAPI.
- `api-gateway`: Spring Cloud Gateway that fronts the API services.
- `eureka-service`: Netflix Eureka service discovery.
- `stock-service`: stock domain service with its own PostgreSQL database and JWT resource-server validation.
- `notification-service`: background service consuming messaging events.
- `messaging-common`: shared messaging models and AMQP payloads used by the services.
- `web-client`: Vue 3 + Vite frontend for the user interface.

## Repository layout

```text
orbenox/
├── api-gateway/             # Spring Cloud Gateway
├── eureka-service/          # Service discovery
├── messaging-common/        # Shared AMQP contracts
├── notification-service/     # Notification worker
├── stock-service/           # Stock microservice
├── web-api/                 # Main backend
├── web-client/              # Vue frontend
├── docker-compose.yaml      # Local infrastructure stack
├── .env.example             # Environment defaults for local services
├── .githooks/               # Git hooks
├── .editorconfig
├── .gitignore
├── README.md
├── AGENTS.md
└── logs/
```

Note: this repo does not currently use a root Maven aggregator `pom.xml`; each service is built independently with its own `pom.xml`.

## Prerequisites

- Java 25
- Maven 3.9+
- Node.js 20+
- npm 10+
- Docker and Docker Compose v2

## Quick start with Docker Compose

The repository includes a local development stack for PostgreSQL, Redis, RabbitMQ, Eureka, the gateway, the main API, stock service, notification service, and the frontend:

```bash
cp .env.example .env
docker compose up --build
```

The stack exposes the following local endpoints:

- Eureka: `http://localhost:8761`
- API Gateway: `http://localhost:8080` by default (`API_GATEWAY_PORT` in `.env` can change this)
- Main API: `http://localhost:8081`
- Stock Service: `http://localhost:8082`
- Frontend: `http://localhost:5173`
- RabbitMQ management: `http://localhost:15673`
- PostgreSQL (main DB): `localhost:5435`
- PostgreSQL (stock DB): `localhost:5436`
- Redis: `localhost:6379`

When started directly without Docker, the gateway defaults to `http://localhost:8083` from its `application.yaml`.

## Running services individually

### Eureka

```bash
cd eureka-service
mvn spring-boot:run
```

### API gateway

```bash
cd api-gateway
mvn spring-boot:run
```

### Main backend

```bash
cd web-api
mvn spring-boot:run
```

### Stock service

```bash
cd stock-service
mvn spring-boot:run
```

### Notification service

```bash
cd notification-service
mvn spring-boot:run
```

### Frontend

```bash
cd web-client
npm install
npm run dev
```

## Build and test commands

Build a Java service:

```bash
cd <service-directory>
mvn clean package
```

Run Java tests for a service:

```bash
cd <service-directory>
mvn test
```

Build the Vue frontend bundle:

```bash
cd web-client
npm run build
```

## Local environment configuration

Copy `.env.example` to a local `.env` file and adjust values as needed before starting the stack. The example includes:

- PostgreSQL connection settings
- JWT secret fields
- Spring profile selection
- Eureka ports
- API gateway port
- Auth0 credentials for machine-to-machine and stock-service audience

## Notes

- The project expects PostgreSQL, Redis, and RabbitMQ to be available locally for direct service startup.
- `web-api` and `stock-service` are configured for Spring Boot development values that match the Docker Compose setup.
- The frontend is served by Vite on port `5173`. Its API helper (`web-client/src/api.js`) currently sends requests to the hard-coded base URL `http://localhost:8080`; this matches the Docker Compose gateway's default port. When running the gateway directly, its default is `http://localhost:8083`, so the frontend will not reach it at that port unless the gateway is configured to use `8080` or the frontend API base URL is changed.

## Git hooks

To enable the repository's Git hook setup:

```bash
git config core.hooksPath .githooks
```
