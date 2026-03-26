## ADDED Requirements

### Requirement: User Registration
The system SHALL allow new users to register by providing a username, email, and password via `POST /api/auth/register`. On success, the system SHALL return a signed JWT token in the response body.

#### Scenario: Successful registration
- **WHEN** a valid `RegisterRequest` is submitted with a unique username, valid email, and password meeting size constraints
- **THEN** the system creates the user with a BCrypt-hashed password, persists it, and returns 200 with an `AuthResponse` containing a valid JWT

#### Scenario: Duplicate username
- **WHEN** a `RegisterRequest` is submitted with a username that already exists
- **THEN** the system returns 409 Conflict with a JSON error body indicating the username is taken

#### Scenario: Duplicate email
- **WHEN** a `RegisterRequest` is submitted with an email that already exists
- **THEN** the system returns 409 Conflict with a JSON error body indicating the email is taken

#### Scenario: Invalid input
- **WHEN** a `RegisterRequest` is submitted with missing or invalid fields (blank username, invalid email format, password too short)
- **THEN** the system returns 400 Bad Request with validation error details

### Requirement: User Login
The system SHALL allow registered users to authenticate via `POST /api/auth/login` with username and password. On success, the system SHALL return a signed JWT token.

#### Scenario: Successful login
- **WHEN** valid credentials (existing username and correct password) are provided in a `LoginRequest`
- **THEN** the system returns 200 with an `AuthResponse` containing a valid JWT

#### Scenario: Invalid credentials
- **WHEN** a `LoginRequest` is submitted with a non-existent username or incorrect password
- **THEN** the system returns 401 Unauthorized with a JSON error body

#### Scenario: Missing fields
- **WHEN** a `LoginRequest` is submitted with blank username or password
- **THEN** the system returns 400 Bad Request with validation error details

### Requirement: JWT Token Generation
The system SHALL generate JWT tokens containing the username as subject, issued-at and expiration timestamps, signed with HMAC-SHA256.

#### Scenario: Token contains correct claims
- **WHEN** a JWT is generated for a user
- **THEN** the token subject equals the username, `iat` is the current time, and `exp` is current time plus the configured `jwt.expiration-ms`

#### Scenario: Token is signed with configured secret
- **WHEN** a JWT is generated
- **THEN** the token is signed using HMAC-SHA256 with the Base64-decoded value of the `jwt.secret` property

### Requirement: JWT Token Validation
The system SHALL validate JWT tokens on every authenticated request by checking signature, expiration, and subject.

#### Scenario: Valid token
- **WHEN** a request includes a Bearer token with a valid signature, non-expired timestamp, and existing username
- **THEN** the system authenticates the request and sets the security context

#### Scenario: Expired token
- **WHEN** a request includes a Bearer token that has passed its expiration time
- **THEN** the system rejects the request with 401 Unauthorized

#### Scenario: Tampered token
- **WHEN** a request includes a Bearer token with an invalid signature
- **THEN** the system rejects the request with 401 Unauthorized

#### Scenario: Missing token
- **WHEN** a request to a protected endpoint does not include an Authorization header
- **THEN** the system returns 401 Unauthorized

### Requirement: Password Security
The system SHALL hash all user passwords using BCrypt before storage and SHALL verify passwords against the stored hash during login.

#### Scenario: Password stored as BCrypt hash
- **WHEN** a user registers
- **THEN** the password stored in the database is a BCrypt hash, not plaintext

#### Scenario: Password verified via BCrypt
- **WHEN** a user logs in with correct credentials
- **THEN** the system verifies the provided password against the stored BCrypt hash

### Requirement: Security Filter Chain
The system SHALL configure a Spring Security filter chain that permits public endpoints without authentication and requires JWT authentication for all other endpoints.

#### Scenario: Auth endpoints are public
- **WHEN** a request is made to `/api/auth/register` or `/api/auth/login`
- **THEN** the request is processed without requiring authentication

#### Scenario: Swagger endpoints are public
- **WHEN** a request is made to `/swagger-ui.html`, `/swagger-ui/**`, or `/v3/api-docs/**`
- **THEN** the request is processed without requiring authentication

#### Scenario: All other endpoints require authentication
- **WHEN** a request is made to any endpoint not in the public permit list
- **THEN** the system requires a valid JWT Bearer token in the Authorization header

### Requirement: Auth Error Response Format
The system SHALL return consistent JSON error responses for authentication and registration failures.

#### Scenario: Unauthorized error format
- **WHEN** a request fails authentication (missing, expired, or invalid token)
- **THEN** the system returns 401 with a JSON body containing an error message

#### Scenario: Conflict error format
- **WHEN** a registration fails due to duplicate username or email
- **THEN** the system returns 409 with a JSON body containing an error message describing the conflict

### Requirement: User Data Storage
The system SHALL persist user data (id, username, email, hashed password, created timestamp) in an H2 in-memory database via Spring Data JPA.

#### Scenario: User persisted after registration
- **WHEN** a user successfully registers
- **THEN** the user record is persisted in the database with all fields populated

#### Scenario: Username uniqueness enforced
- **WHEN** a registration attempt uses an existing username
- **THEN** the system rejects the request before persisting (enforced at both application and database level)

#### Scenario: Email uniqueness enforced
- **WHEN** a registration attempt uses an existing email
- **THEN** the system rejects the request before persisting (enforced at both application and database level)
