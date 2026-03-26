## Context
The project is a minimal Spring Boot 3.4.2 Hello World app with no authentication, no database, and no security dependencies. We need to add JWT-based authentication that's simple to implement, easy to swap backends later, and follows Spring Boot conventions.

## Goals / Non-Goals
- **Goals:**
  - Secure all existing and future endpoints behind JWT authentication
  - Provide register and login endpoints for user identity
  - Use in-memory H2 database for fast development iteration
  - Keep the implementation simple and idiomatic Spring Boot
- **Non-Goals:**
  - OAuth2 / OpenID Connect integration
  - Refresh tokens or token revocation
  - Role-based access control (RBAC) — all authenticated users are equal for now
  - Production-grade database setup (PostgreSQL migration is a future change)
  - Email verification or password reset flows

## Decisions

### JWT Library: jjwt 0.12.x
- **Decision:** Use `io.jsonwebtoken:jjwt` 0.12.x for JWT creation and parsing.
- **Why:** Simpler and more direct than `spring-boot-starter-oauth2-resource-server` for self-issued tokens. No authorization server needed. Well-maintained, widely adopted.
- **Alternatives considered:**
  - `spring-boot-starter-oauth2-resource-server` — heavier, designed for external auth servers, overkill for self-issued tokens
  - `com.auth0:java-jwt` — viable alternative, but jjwt has better builder API and Spring ecosystem familiarity
  - Nimbus JOSE+JWT — lower-level, more configuration needed

### Signing Algorithm: HMAC-SHA256
- **Decision:** Sign JWTs with HMAC-SHA256 using a shared secret.
- **Why:** Simplest symmetric signing for a single-service app. No key pair management needed.
- **Alternatives considered:**
  - RSA/ECDSA asymmetric signing — needed for multi-service token verification, not applicable here yet

### JWT Secret Management
- **Decision:** Read secret from `jwt.secret` property. Application fails to start if not configured.
- **Why:** Prevents accidentally running with a default/weak secret. Forces explicit configuration. Clear error at startup is better than silent insecurity.
- **Alternatives considered:**
  - Auto-generate secret on startup — tokens invalidated on every restart, confusing in development
  - Hardcoded default — security risk, easy to forget to change

### Password Hashing: BCrypt
- **Decision:** Use BCrypt via Spring Security's `BCryptPasswordEncoder`.
- **Why:** Spring Security default, well-tested, adaptive cost factor. No additional dependencies needed.

### Database: H2 In-Memory + Spring Data JPA
- **Decision:** Use H2 in-memory database with Spring Data JPA for user storage.
- **Why:** Zero setup for development. JPA abstraction means switching to PostgreSQL later requires only a config change and driver dependency — no code changes.
- **Alternatives considered:**
  - In-memory `Map` — no persistence story, no migration path, harder to test
  - PostgreSQL from day one — heavier setup, not needed for dev/prototype phase

### Session Management: Stateless
- **Decision:** Stateless session with CSRF disabled.
- **Why:** JWT tokens carry authentication state. No server-side session needed. CSRF protection is unnecessary for stateless token-based APIs (tokens aren't sent automatically by browsers like cookies).

### Package Structure
- **Decision:** New `com.example.helloworld.auth` package with sub-packages: `config`, `controller`, `dto`, `entity`, `filter`, `repository`, `service`.
- **Why:** Groups all auth concerns under one feature package. Sub-packages follow standard Spring Boot layering within the feature. Easy to extract into a separate module later if needed.

### Public Endpoints
- **Decision:** Permit without authentication: `/api/auth/**`, `/swagger-ui/**`, `/v3/api-docs/**`, `/h2-console/**`.
- **Why:**
  - `/api/auth/**` — must be accessible to register and log in
  - `/swagger-ui/**` and `/v3/api-docs/**` — API documentation should remain accessible for development
  - `/h2-console/**` — database inspection tool for development

### H2 Console
- **Decision:** Enable H2 console at `/h2-console` and add to public permit list.
- **Why:** Development convenience for inspecting user data. Frame options configured to allow console rendering.

## Risks / Trade-offs

- **H2 in-memory data loss** → Acceptable for dev. Mitigated by planned PostgreSQL migration in a future change.
- **No refresh tokens** → Users must re-authenticate after token expiry (24h default). Acceptable for initial implementation.
- **HMAC shared secret** → If the secret leaks, all tokens are compromised. Mitigated by fail-fast on missing secret and future rotation mechanism.
- **Breaking change on `GET /`** → Existing clients will get 401. Mitigated by clear documentation and being early in the project lifecycle.
- **No CSRF** → Safe for token-based API, but if cookies are ever used for auth, CSRF must be re-enabled.

## Migration Plan
Not applicable — greenfield addition. No existing auth to migrate from.

### Rollback
Remove the auth package, security/JPA/H2 dependencies from `pom.xml`, and auth-related properties from `application.properties`. `GET /` returns to open access.

## Open Questions
None — all questions resolved during planning.
