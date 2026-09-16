# Repository Guidelines

## Project Structure & Module Organization
`orbenox` is a multi-module repository. `web-api/` contains the Spring Boot backend, with Java sources in `src/main/java`, profile config and Flyway migrations in `src/main/resources`, and tests in `src/test/java`. `web-client/` contains the Vue 3 + Vite frontend, with app code in `src/`, static files in `public/`, and route/component organization grouped by feature such as `src/components/currency/`. Shared docs live in `docs/`, and helper scripts live in `scripts/`.

## Build, Test, and Development Commands
Use Java 21+, Maven 3.9+, Node 20+, and npm 10+.

```bash
mvn clean package                  # build Maven modules from repo root
cd web-api && mvn spring-boot:run # run backend on :8080 or SERVER_PORT
cd web-api && mvn test            # run backend tests
cd web-client && npm install      # install frontend dependencies
cd web-client && npm run dev      # start Vite dev server on :5173
cd web-client && npm run build    # production frontend build
git config core.hooksPath .githooks
```

## Coding Style & Naming Conventions
Follow `.editorconfig`: UTF-8, LF endings, final newline, and no trailing whitespace outside Markdown. Backend code uses standard Spring conventions: 4-space indentation, `PascalCase` classes, `camelCase` methods/fields, and feature packages under `com.orbenox.erp`. Frontend files use `PascalCase` for Vue SFCs like `CreateCurrency.vue`; keep route helpers and utilities in `camelCase` files such as `appRouter.js` and `tools.js`. No ESLint, Prettier, or Spotless config is checked in, so keep edits consistent with surrounding code.

## Testing Guidelines
Backend tests use JUnit 5, Spring Boot Test, AssertJ, and Testcontainers with PostgreSQL. Name tests `*Test.java`; prefer descriptive method names like `findByIdAndDeletedFalse_shouldNotReturnDeletedEntity`. Run `cd web-api && mvn test` before opening a PR. JaCoCo reports are generated in CI, but no coverage threshold is enforced in the repository. No frontend test runner is configured yet, so use `npm run build` as the minimum regression check for UI changes.

## Commit & Pull Request Guidelines
Recent history uses short capitalized subjects such as `Bugfix`, `Refactors`, and `Bugfix, document action policy`. Keep the first line concise, mention the affected area, and avoid one-word messages when a scoped summary is possible. PRs should describe the behavior change, list validation steps, link the related issue, and include screenshots for `web-client` UI changes.

## Security & Configuration Tips
Start local backend configuration from `web-api/.env.example`. Keep secrets and real database credentials out of git. Use the existing Spring profiles (`dev`, `test`, `prod`) and add schema changes through versioned Flyway files like `web-api/src/main/resources/db/migration/V4__feature.sql`.
