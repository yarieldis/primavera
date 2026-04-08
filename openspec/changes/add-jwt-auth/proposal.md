# Change: Add JWT Authentication with Register/Login

## Why
The application currently has no authentication or authorization. Any client can access all endpoints without restriction. JWT-based auth is needed to secure the API before adding user-facing features, and register/login endpoints are the foundation for user identity.

## What Changes
- Add Spring Security with a stateless JWT filter chain
- Add user registration endpoint (`POST /api/auth/register`) with username, email, password
- Add user login endpoint (`POST /api/auth/login`) returning a signed JWT
- Add H2 in-memory database with Spring Data JPA for user storage
- Add jjwt library for JWT generation and validation (HMAC-SHA256)
- Add BCrypt password hashing
- **BREAKING**: `GET /` now returns 401 Unauthorized without a valid JWT Bearer token
- Public endpoints: `/api/auth/**`, `/swagger-ui.html`, `/swagger-ui/**`, `/v3/api-docs/**`, `/h2-console/**`
- App fails to start if `jwt.secret` is not configured in `application.properties`

## Impact
- Affected specs: `user-auth` (new), `hello-api` (new)
- Affected code:
  - `pom.xml` — new dependencies (spring-boot-starter-security, spring-boot-starter-data-jpa, spring-boot-starter-validation, h2, jjwt)
  - `application.properties` — JWT config, H2 datasource, H2 console
  - New package `com.github.primavera.auth` with sub-packages: config, controller, dto, entity, filter, repository, service
  - `HelloController.java` — added `@SecurityRequirement` Swagger annotation; behavior changes (requires auth)
  - Test updates for authenticated requests
