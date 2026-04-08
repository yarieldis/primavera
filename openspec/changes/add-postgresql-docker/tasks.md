## 1. Dependencies & Configuration
- [x] 1.1 Add `postgresql` driver dependency to `pom.xml`
- [x] 1.2 Change `h2` dependency scope to `test`
- [x] 1.3 Create `application-dev.properties` with PostgreSQL config (host=`db`, port=`5432`, db=`primavera`, user/pass)
- [x] 1.4 Create `application-prod.properties` with env-var-driven PostgreSQL config (`DB_HOST`, `DB_PORT`, etc.)
- [x] 1.5 Create `application-test.properties` with current H2 config (copy from existing `application.properties`)
- [x] 1.6 Update `application.properties` to set `spring.profiles.active=dev` and remove H2-specific config
- [x] 1.7 Remove H2 console config (`spring.h2.console.*`) from non-test profiles

## 2. Docker Setup
- [x] 2.1 Create `Dockerfile` — multi-stage: Maven build (`eclipse-temurin:20-jdk`) + runtime (`eclipse-temurin:20-jre`)
- [x] 2.2 Create `.dockerignore` — exclude `.git`, `target`, `.idea`, `.claude`, `openspec`, `*.md`
- [x] 2.3 Create `docker-compose.yml` with:
  - `db` service: `postgres:16-alpine`, named volume `pgdata`, env vars for db/user/password
  - `app` service: build from Dockerfile, depends_on `db`, port mapping `8080:8080`, `dev` profile active
  - Custom bridge network `app-network`
- [ ] 2.4 Verify `docker compose up --build` starts both containers and the app connects to PostgreSQL

## 3. Tests
- [x] 3.1 Add `@ActiveProfiles("test")` to `AuthIntegrationTest` and `PrimaveraApplicationTests`
- [x] 3.2 Run `mvnw.cmd test` — confirm all existing tests pass with H2 (no Docker required)

## 4. Documentation
- [x] 4.1 Update `CLAUDE.md` — new build/run commands (`docker compose up`), updated project structure, profile descriptions
- [x] 4.2 Update `openspec/project.md` — reflect PostgreSQL + Docker in tech stack and constraints
