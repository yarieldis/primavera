<!-- OPENSPEC:START -->
# OpenSpec Instructions

These instructions are for AI assistants working in this project.

Always open `@/openspec/AGENTS.md` when the request:
- Mentions planning or proposals (words like proposal, spec, change, plan)
- Introduces new capabilities, breaking changes, architecture shifts, or big performance/security work
- Sounds ambiguous and you need the authoritative spec before coding

Use `@/openspec/AGENTS.md` to learn:
- How to create and apply change proposals
- Spec format and conventions
- Project structure and guidelines

Keep this managed block so 'openspec update' can refresh the instructions.

<!-- OPENSPEC:END -->

# CLAUDE.md - Project Guide

## Project Overview

Spring Boot Primavera application. Minimal REST API serving as a starting point for further development.

## Tech Stack

- **Framework:** Spring Boot 3.4.2
- **Language:** Java 20 (Eclipse Adoptium JDK 20.0.2)
- **Build Tool:** Maven (via Maven Wrapper, no global install needed)
- **Embedded Server:** Tomcat (via spring-boot-starter-web)
- **Security:** Spring Security + JWT (jjwt 0.12.x) — stateless, HMAC-SHA256
- **Database:** PostgreSQL 16 (Docker) + Spring Data JPA (H2 for tests)
- **Containerization:** Docker + Docker Compose
- **Serverless:** AWS Lambda + API Gateway (via aws-serverless-java-container + SAM)
- **Testing:** JUnit 5 + Spring Boot Test + Spring Security Test
- **API Docs:** SpringDoc-OpenAPI (Swagger UI at `/swagger-ui.html`)
- **Static Analysis:** SpotBugs + Find Security Bugs

## Project Structure

```
src/main/java/com/github/primavera/
├── PrimaveraApplication.java     # @SpringBootApplication entry point
├── StreamLambdaHandler.java      # AWS Lambda entry point (API Gateway → Spring)
├── HelloController.java          # @RestController with GET / (requires auth)
└── auth/
    ├── config/
    │   └── SecurityConfig.java       # SecurityFilterChain, BCrypt, AuthenticationManager
    ├── controller/
    │   └── AuthController.java       # POST /api/auth/register, /api/auth/login
    ├── dto/
    │   ├── RegisterRequest.java      # Username, email, password with validation
    │   ├── LoginRequest.java         # Username, password with validation
    │   └── AuthResponse.java         # JWT token response
    ├── entity/
    │   └── User.java                 # JPA entity (id, username, email, password, createdAt)
    ├── filter/
    │   └── JwtAuthenticationFilter.java  # Bearer token extraction and validation
    ├── repository/
    │   └── UserRepository.java       # Spring Data JPA repository
    └── service/
        ├── JwtService.java           # Token generation, validation, parsing
        ├── CustomUserDetailsService.java  # UserDetailsService implementation
        └── AuthService.java          # Register and login business logic
src/main/resources/
├── application.properties            # Shared config (JWT, port, active profile)
├── application-dev.properties        # PostgreSQL via Docker Compose
├── application-prod.properties       # PostgreSQL via environment variables
├── application-lambda.properties     # PostgreSQL via RDS (Lambda deployment)
src/test/resources/
└── application-test.properties       # H2 in-memory for tests
src/test/java/com/github/primavera/
├── PrimaveraApplicationTests.java
└── auth/
    ├── JwtServiceTest.java           # Unit tests for JWT operations
    └── AuthIntegrationTest.java      # Integration tests for auth + hello endpoint
```

Base package: `com.github.primavera`

## Build & Run Commands

### Docker (preferred)

```cmd
docker compose up --build     # Build & start app + PostgreSQL
docker compose down           # Stop containers (data persists in pgdata volume)
docker compose down -v        # Stop containers and delete data
```

### Maven (local development)

All commands use the Maven Wrapper (`mvnw.cmd`) from the project root:

```cmd
mvnw.cmd compile              # Compile sources
mvnw.cmd test                 # Run tests (uses H2, no Docker needed)
mvnw.cmd package              # Build JAR → target/primavera-0.0.1-SNAPSHOT.jar
mvnw.cmd spring-boot:run      # Run the app (needs a PostgreSQL instance)
mvnw.cmd clean                # Clean build artifacts
mvnw.cmd spotbugs:check       # Run SpotBugs static analysis + security scan
```

JAVA_HOME must point to `C:\Program Files\Eclipse Adoptium\jdk-20.0.2.9-hotspot` (set at machine level).

### AWS Lambda (serverless deployment)

```cmd
sam build                         # Build Lambda artifact from pom.xml
sam deploy --guided               # Deploy to AWS (first time, interactive)
sam deploy                        # Deploy to AWS (subsequent, uses samconfig.toml)
sam local invoke                  # Smoke test locally with SAM CLI
sam delete                        # Tear down the CloudFormation stack
```

Requires: AWS CLI configured, SAM CLI installed, RDS PostgreSQL instance pre-provisioned.

## API Endpoints

| Method | Path | Auth | Response |
|--------|------|------|----------|
| GET    | `/`  | Required | `Hello, World!` (plain text) |
| POST   | `/api/auth/register` | Public | `{"token": "..."}` — register new user |
| POST   | `/api/auth/login` | Public | `{"token": "..."}` — login existing user |
| GET    | `/swagger-ui.html` | Public | Swagger UI (auto-generated API docs) |
| GET    | `/v3/api-docs` | Public | OpenAPI 3.0 JSON spec |
| GET    | `/h2-console` | Public | H2 database console (test profile only) |

### Authentication

All endpoints except those marked "Public" require a JWT Bearer token in the `Authorization` header:
```
Authorization: Bearer <token>
```

Obtain a token by registering or logging in via `/api/auth/register` or `/api/auth/login`.

## Claude Code Skills

Custom skills are defined in `.claude/skills/`:

- **`/scaffold-spring-api <EntityName>`** — Generates a full REST API layer (controller, service, repository, DTOs, entity, tests) for a domain entity
- **`/scaffold-entity <EntityName> <field:type> ...`** — Generates a JPA entity class with specified fields and audit columns

## Claude Code Hooks

- **PreCommit** — Runs `mvnw.cmd compile -q` before every commit to ensure code compiles

## MCP Servers

Configured in `.claude/settings.json`:

- **GitHub** (`@modelcontextprotocol/server-github`) — PR management, issue tracking, workflow triggers from CLI. Requires `GITHUB_PERSONAL_ACCESS_TOKEN` in settings.
- **Maven Tools** (`maven-tools-mcp`) — Look up latest dependency versions, resolve conflicts, check Maven Central in real-time.

## Version Control Guidelines
 
- **NEVER** commit changes without user approval. Ask systematically for approval before committing.
- Commit messages should be clear and follow convention:
  - ai-tooling: AI agents, automation commands, workflows, or other AI-enabled developer tooling
  - feat: New feature
  - fix: Bug fix
  - docs: Documentation
  - style: Formatting
  - refactor: Code restructuring
  - test: Adding tests
  - chore: Maintenance tasks
- **NEVER** mention AI/Claude authorship in commit messages (no "Generated with Claude Code", "AI-assisted", etc.)

## Code Conventions

- Follow standard Spring Boot patterns: `@RestController` for REST endpoints, `@Service` for business logic, `@Repository` for data access
- Package structure under `com.github.primavera` — group by feature as the project grows
- Use constructor injection over field injection
- Keep controllers thin — delegate logic to service classes
- Tests go in the mirror package under `src/test/java`
- Use `application.properties` for configuration (not YAML)
- Use `ResponseEntity<>` return types in controllers
- Use `@Valid` on request body parameters with Jakarta validation

## Development Notes

- The app runs on port **8080** by default (configurable in `application.properties`)
- Swagger UI available at `http://localhost:8080/swagger-ui.html` when running
- **JWT secret** is configured in `application.properties` via `jwt.secret` (Base64-encoded). App fails to start if missing.
- **PostgreSQL** runs in Docker via `docker compose up`. Data persists in a named volume `pgdata`.
- **Spring Profiles:** `dev` (default, Docker PostgreSQL), `prod` (env-var PostgreSQL), `lambda` (RDS via SAM env vars), `test` (H2 in-memory)
- Tests use the `test` profile with H2 — no Docker needed to run tests
- GitHub MCP server needs a personal access token — replace `<your-github-token-here>` in `.claude/settings.json`
