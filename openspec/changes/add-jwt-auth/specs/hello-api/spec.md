## ADDED Requirements

### Requirement: Hello Endpoint Authentication
The existing `GET /` endpoint SHALL require a valid JWT Bearer token. Authenticated requests receive the original "Hello, World!" response. Unauthenticated or invalidly authenticated requests receive 401 Unauthorized.

#### Scenario: Authenticated request succeeds
- **WHEN** a request to `GET /` includes a valid JWT Bearer token in the Authorization header
- **THEN** the system returns 200 with body "Hello, World!"

#### Scenario: Unauthenticated request rejected
- **WHEN** a request to `GET /` does not include an Authorization header
- **THEN** the system returns 401 Unauthorized

#### Scenario: Invalid token rejected
- **WHEN** a request to `GET /` includes an expired or tampered JWT Bearer token
- **THEN** the system returns 401 Unauthorized
