# Orbenox Multi-Module Project

This repository contains two modules:

- `web-api`: Spring Boot backend (Maven)
- `web-client`: Vue 3 frontend (Vite + npm)

## Prerequisites

- Java 21+
- Maven 3.9+
- Node.js 20+
- npm 10+

## Project Structure

```text
orbenox/
|- pom.xml               # parent/aggregator POM
|- web-api/              # backend module
|  |- pom.xml
|  |- src/
|- web-client/           # frontend module
   |- pom.xml
   |- package.json
   |- src/
```

## Running the Backend

```bash
cd web-api
mvn spring-boot:run
```

Backend default URL: `http://localhost:8080`

## Running the Frontend

```bash
cd web-client
npm install
npm run dev
```

Frontend default URL: `http://localhost:5173`

## Building

Build all Maven modules from repository root:

```bash
mvn clean package
```

Build frontend assets:

```bash
cd web-client
npm run build
```

## Notes

- `web-client` is currently included in Maven reactor for module consistency, but frontend build is managed with npm.
- If you run a module directly (for example from an IDE), child modules now resolve the root parent via `../pom.xml`.

## Git hooks

To enable the encoding guard hook:
```bash
git config core.hooksPath .githooks
```
