# Change: Migrate from H2 to PostgreSQL with Docker Compose

## Why
The current H2 in-memory database loses all data on every restart, making it unsuitable for any real development or deployment scenario. Migrating to PostgreSQL provides durable storage, production-realistic behavior during development, and prepares the project for future deployment targets (e.g., AWS Lambda + RDS).

## What Changes
- **BREAKING** — Remove H2 in-memory database, replace with PostgreSQL
- Add multi-stage `Dockerfile` to containerize the Spring Boot application
- Add `docker-compose.yml` with two services: `app` (Spring Boot) and `db` (PostgreSQL), connected via a bridge network
- Introduce Spring profiles (`dev`, `prod`) to separate configuration
- Replace H2 dialect/driver with PostgreSQL dialect/driver in dependencies and config
- Switch JPA `ddl-auto` from `create-drop` to `update` (with note to migrate to Flyway later)
- Keep H2 as a test-scoped dependency so existing tests continue to pass without requiring a running PostgreSQL instance
- Add `.dockerignore` to keep the image lean
- Update `CLAUDE.md` with new run/build commands

## Impact
- Affected specs: (new) `database` capability
- Affected code:
  - `pom.xml` — add `postgresql` driver, keep `h2` as test-scoped
  - `application.properties` — becomes the `dev` profile base
  - `application-dev.properties` — PostgreSQL connection via Docker Compose service name
  - `application-prod.properties` — PostgreSQL connection via environment variables
  - `application-test.properties` — H2 config for fast tests
  - `Dockerfile` — multi-stage build (Maven build + JRE runtime)
  - `docker-compose.yml` — two services, bridge network, volume for data persistence
  - `.dockerignore` — exclude unnecessary files from Docker context
  - `CLAUDE.md` — updated build/run instructions
