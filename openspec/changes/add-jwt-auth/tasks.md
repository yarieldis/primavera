## 1. Maven Dependencies
- [x] 1.1 Add `spring-boot-starter-security` dependency
- [x] 1.2 Add `spring-boot-starter-data-jpa` dependency
- [x] 1.3 Add `spring-boot-starter-validation` dependency
- [x] 1.4 Add `com.h2database:h2` runtime dependency
- [x] 1.5 Add `io.jsonwebtoken:jjwt-api`, `jjwt-impl`, `jjwt-jackson` 0.12.x dependencies

## 2. User Entity and Repository
- [x] 2.1 Create `User` JPA entity in `auth.entity` with id, username, email, password, createdAt
- [x] 2.2 Create `UserRepository` in `auth.repository` with `findByUsername`, `findByEmail`, `existsByUsername`, `existsByEmail`

## 3. Auth DTOs
- [x] 3.1 Create `RegisterRequest` DTO with Jakarta validation annotations (`@NotBlank`, `@Email`, `@Size`)
- [x] 3.2 Create `LoginRequest` DTO with `@NotBlank` on username and password
- [x] 3.3 Create `AuthResponse` DTO with token field

## 4. JWT Service
- [x] 4.1 Create `JwtService` in `auth.service` with `generateToken(UserDetails)`, `validateToken(String, UserDetails)`, `extractUsername(String)`
- [x] 4.2 Read signing secret from `jwt.secret` property; fail startup if missing
- [x] 4.3 Read expiration from `jwt.expiration-ms` property (default 86400000 = 24h)
- [x] 4.4 Sign tokens with HMAC-SHA256

## 5. UserDetailsService Implementation
- [x] 5.1 Create `CustomUserDetailsService` in `auth.service` implementing `UserDetailsService`
- [x] 5.2 Load user by username from `UserRepository`

## 6. Auth Service
- [x] 6.1 Create `AuthService` in `auth.service` with `register(RegisterRequest)` and `login(LoginRequest)`
- [x] 6.2 Registration: validate uniqueness of username and email, hash password with BCrypt, save user, return JWT
- [x] 6.3 Login: authenticate credentials via `AuthenticationManager`, return JWT

## 7. Security Filter Chain Configuration
- [x] 7.1 Create `SecurityConfig` in `auth.config` with `SecurityFilterChain` bean
- [x] 7.2 Disable CSRF (stateless API)
- [x] 7.3 Set session management to STATELESS
- [x] 7.4 Permit `/api/auth/**`, `/swagger-ui/**`, `/v3/api-docs/**`, `/h2-console/**`
- [x] 7.5 Require authentication for all other endpoints
- [x] 7.6 Register JWT filter before `UsernamePasswordAuthenticationFilter`
- [x] 7.7 Configure frame options for H2 console access

## 8. JWT Authentication Filter
- [x] 8.1 Create `JwtAuthenticationFilter` in `auth.filter` extending `OncePerRequestFilter`
- [x] 8.2 Extract Bearer token from `Authorization` header
- [x] 8.3 Validate token and set `SecurityContextHolder` authentication

## 9. Auth Controller
- [x] 9.1 Create `AuthController` in `auth.controller` with `@RestController` and `@RequestMapping("/api/auth")`
- [x] 9.2 Implement `POST /api/auth/register` accepting `@Valid @RequestBody RegisterRequest`
- [x] 9.3 Implement `POST /api/auth/login` accepting `@Valid @RequestBody LoginRequest`
- [x] 9.4 Return `AuthResponse` with JWT token on success
- [x] 9.5 Return 409 Conflict JSON on duplicate username/email
- [x] 9.6 Return 401 Unauthorized JSON on invalid credentials

## 10. Verify Hello Endpoint Protection
- [x] 10.1 Confirm `GET /` returns 401 without token (no code change to `HelloController`)
- [x] 10.2 Confirm `GET /` returns 200 with valid Bearer token

## 11. Tests
- [x] 11.1 Unit tests for `JwtService` (generate, validate, extract, expired, tampered)
- [x] 11.2 Integration tests for `POST /api/auth/register` (success, duplicate username, duplicate email, invalid input)
- [x] 11.3 Integration tests for `POST /api/auth/login` (success, bad credentials, missing fields)
- [x] 11.4 Integration tests for `GET /` (authenticated 200, unauthenticated 401, invalid token 401)
- [x] 11.5 Update existing `PrimaveraApplicationTests` to work with security context

## 12. Configuration and Documentation
- [x] 12.1 Add `jwt.secret` and `jwt.expiration-ms` to `application.properties`
- [x] 12.2 Add H2 datasource and JPA config to `application.properties`
- [x] 12.3 Enable H2 console at `/h2-console`
- [x] 12.4 Update `CLAUDE.md` with new endpoints, dependencies, and auth instructions
- [x] 12.5 Add Swagger `@Operation` and `@Tag` annotations to `AuthController`
