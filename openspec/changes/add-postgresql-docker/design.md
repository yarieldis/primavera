## Context
The project currently uses H2 in-memory which resets on every restart. We need PostgreSQL for durability and production-readiness. Both the app and database should run as Docker containers orchestrated by Docker Compose, connected over a bridge network.

## Goals / Non-Goals
- Goals:
  - Run the Spring Boot app and PostgreSQL in Docker containers
  - Use Docker Compose with a bridge network so the app resolves the DB by service name
  - Persist PostgreSQL data across container restarts via a named volume
  - Keep tests fast by using H2 in-memory for the test profile
  - Introduce Spring profiles to cleanly separate dev/prod/test config
- Non-Goals:
  - Database migration tooling (Flyway/Liquibase) — deferred to a future change
  - Production-grade Docker hardening (health checks, resource limits, secrets management)
  - CI/CD pipeline changes

## Decisions

### 1. Multi-stage Dockerfile
- **Decision:** Use a two-stage Dockerfile — stage 1 builds the JAR with Maven, stage 2 copies it into a slim JRE image.
- **Why:** Keeps the final image small (~300MB vs ~800MB). The build stage uses `eclipse-temurin:20-jdk` and the runtime stage uses `eclipse-temurin:20-jre`.
- **Alternatives considered:**
  - Build JAR locally, COPY into single-stage image — simpler but requires local JDK/Maven, not portable
  - Jib (Google) — no Dockerfile needed, but adds build plugin complexity for a small project

### 2. Docker Compose with bridge network
- **Decision:** Define a custom bridge network `app-network` in `docker-compose.yml`. The app service uses the db service name (`db`) as the JDBC hostname.
- **Why:** Custom bridge provides DNS resolution by service name and isolates the two containers from other Docker workloads.
- **Alternatives considered:**
  - Default bridge — no automatic DNS resolution between containers
  - Host networking — no isolation, port conflicts likely on dev machines

### 3. Spring profiles (dev / prod / test)
- **Decision:** Three profiles:
  - `dev` (default when running via Docker Compose): PostgreSQL on `db:5432`
  - `prod`: PostgreSQL via environment variables (`DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`)
  - `test` (activated by `@SpringBootTest`): H2 in-memory, same as today
- **Why:** Clean separation. Tests stay fast (no Docker needed). Dev "just works" with `docker compose up`. Prod is configurable for any environment.
- **Alternatives considered:**
  - Testcontainers for integration tests — better fidelity but adds complexity, Docker dependency in CI, and slower test runs for this project's size

### 4. H2 retained for tests
- **Decision:** Keep `h2` dependency with `<scope>test</scope>`. Add `application-test.properties` with the current H2 config. `@SpringBootTest` activates the `test` profile.
- **Why:** All existing tests pass without changes and without requiring a running PostgreSQL instance. Zero friction for developers running `mvnw.cmd test`.

### 5. JPA ddl-auto strategy
- **Decision:** Use `update` for dev and prod profiles. Use `create-drop` for test profile.
- **Why:** `update` evolves the schema incrementally. `create-drop` keeps tests hermetic. Flyway migration is a natural follow-up change.
- **Risk:** `update` can't handle column drops or renames — acceptable until Flyway is introduced.

### 6. PostgreSQL version
- **Decision:** Use `postgres:16-alpine` image.
- **Why:** PostgreSQL 16 is the latest stable release. Alpine variant keeps image small (~80MB).

## Risks / Trade-offs
- **Cold builds are slow** (~2-3 min for Maven in Docker) → Mitigated by Docker layer caching of dependencies
- **`ddl-auto=update` is not production-safe long-term** → Acceptable for now; Flyway migration planned as follow-up
- **Docker Desktop required on Windows** → Already a common dev tool; no additional licensing concern for individual use

## Open Questions
- Should we pin a specific PostgreSQL minor version (e.g., `16.2-alpine`) for reproducibility?
- Should we add a `docker-compose.override.yml` for local port mapping customization?
