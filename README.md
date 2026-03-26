# Hello World API

A minimal Spring Boot REST API with JWT authentication, built as a starting point for further development.

## Quick Start

### Prerequisites
- Java 20 (Eclipse Adoptium JDK)
- Maven Wrapper (included)

### Get Up and Running

```bash
# Compile and run
mvnw.cmd spring-boot:run
```

The API starts on `http://localhost:8080`

## Features

- **REST API** — Simple Hello endpoint with authentication
- **JWT Authentication** — Stateless token-based auth (HMAC-SHA256)
- **User Management** — Register and login endpoints
- **Database** — H2 in-memory database with Spring Data JPA
- **API Documentation** — Swagger UI auto-generated from code
- **Tests** — Unit and integration tests with 100% auth coverage
- **Static Analysis** — SpotBugs security scanning

## API Endpoints

| Method | Path | Auth | Response |
|--------|------|------|----------|
| **POST** | `/api/auth/register` | Public | Register a new user, get JWT token |
| **POST** | `/api/auth/login` | Public | Login, get JWT token |
| **GET** | `/` | Required | `Hello, World!` |
| **GET** | `/swagger-ui.html` | Public | Interactive API docs |

### Example: Register & Login

```bash
# Register
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"user1","email":"user@example.com","password":"pass123"}'

# Response: {"token":"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9..."}

# Use token to call protected endpoint
curl -X GET http://localhost:8080/ \
  -H "Authorization: Bearer <token>"
```

## Build & Test

```bash
mvnw.cmd compile     # Compile sources
mvnw.cmd test        # Run all tests
mvnw.cmd package     # Build JAR (target/helloworld-0.0.1-SNAPSHOT.jar)
mvnw.cmd spotbugs:check  # Security analysis
```

## Project Structure

```
src/main/java/com/example/helloworld/
├── HelloController.java          # Protected Hello endpoint
├── auth/
│   ├── config/SecurityConfig.java       # Spring Security + JWT setup
│   ├── controller/AuthController.java   # /register, /login
│   ├── service/                         # Business logic & JWT
│   └── entity/User.java                 # JPA entity
└── ...
```

See [CLAUDE.md](CLAUDE.md) for detailed architecture and development guidelines.

## Development

### Local Database

H2 console available at `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:authdb`
- User: `sa`
- Password: (none)

*Note: H2 is in-memory — data resets on restart.*

### Next Steps

- Migrate to PostgreSQL for persistent storage
- Add domain-specific endpoints using `/scaffold-spring-api` skill
- Set up dev/prod profiles in `application.properties`

See [CLAUDE.md](CLAUDE.md) for complete development docs, code conventions, and project guidance.

## License

MIT
