# Project Context

## Purpose
Spring Boot Hello World application. Minimal REST API serving as a starting point for further development. The project provides a foundational template for building Spring Boot microservices.

## Tech Stack
- **Framework:** Spring Boot 3.4.2
- **Language:** Java 20 (Eclipse Adoptium JDK 20.0.2)
- **Build Tool:** Maven (via Maven Wrapper `mvnw.cmd`, no global install needed)
- **Embedded Server:** Tomcat (via spring-boot-starter-web)
- **Database:** PostgreSQL 16 (Docker) + Spring Data JPA (H2 for tests)
- **Containerization:** Docker + Docker Compose
- **Serverless:** AWS Lambda + API Gateway (via aws-serverless-java-container + SAM)
- **Testing:** JUnit 5 + Spring Boot Test
- **API Docs:** SpringDoc-OpenAPI (Swagger UI at `/swagger-ui.html`)
- **Static Analysis:** SpotBugs + Find Security Bugs

## Project Conventions

### Code Style
- Follow standard Spring Boot patterns
- Base package: `com.example.helloworld`
- Use constructor injection over field injection
- Keep controllers thin — delegate logic to service classes
- Use `ResponseEntity<>` return types in controllers
- Use `@Valid` on request body parameters with Jakarta validation
- Use `application.properties` for configuration (not YAML)

### Architecture Patterns
- `@RestController` for REST endpoints
- `@Service` for business logic
- `@Repository` for data access
- Package structure under `com.example.helloworld` — group by feature as the project grows
- Single-file implementations until proven insufficient
- Default to <100 lines of new code per change

### Testing Strategy
- Tests go in the mirror package under `src/test/java`
- Use JUnit 5 with Spring Boot Test support
- Write tests for existing behavior before modifying it
- Integration tests for API endpoints using `@SpringBootTest`

### Git Workflow
- Main branch: `main`
- Feature branches for new work
- Pre-commit hook runs `mvnw.cmd compile -q` to ensure code compiles
- **Never** commit without user approval
- Commit message prefixes:
  - `ai-tooling:` AI agents, automation commands, workflows, or other AI-enabled developer tooling
  - `feat:` New feature
  - `fix:` Bug fix
  - `docs:` Documentation
  - `style:` Formatting
  - `refactor:` Code restructuring
  - `test:` Adding tests
  - `chore:` Maintenance tasks
- **Never** mention AI/Claude authorship in commit messages

## Domain Context
This is a starter/template project. No specific business domain is implemented yet. The current API exposes a single `GET /` endpoint returning "Hello, World!" in plain text.

## Important Constraints
- JAVA_HOME must point to `C:\Program Files\Eclipse Adoptium\jdk-20.0.2.9-hotspot`
- App runs on port **8080** by default (configurable in `application.properties`)
- Docker Desktop required to run the app (`docker compose up --build`)
- Spring profiles: `dev` (Docker PostgreSQL), `prod` (env-var PostgreSQL), `lambda` (RDS via SAM), `test` (H2 in-memory)
- Lambda deployment requires AWS CLI + SAM CLI + pre-provisioned RDS PostgreSQL
- Tests run with H2 — no Docker needed for `mvnw.cmd test`

## External Dependencies
- **SpringDoc-OpenAPI:** Auto-generates API documentation at `/swagger-ui.html` and `/v3/api-docs`
- **SpotBugs + Find Security Bugs:** Static analysis for code quality and security scanning
- **GitHub MCP Server:** PR management, issue tracking, workflow triggers (requires personal access token)
- **Maven Tools MCP:** Dependency version lookup, conflict resolution via Maven Central
