# Hello World API

A production-ready Spring Boot REST API template with **JWT authentication**, **Docker containerization**, and **serverless deployment** options. Built as a foundational starter for microservices development.

**Status:** ✅ Core features complete | 🔄 AWS Lambda deployment in progress

## 🎯 What This Project Provides

This is **not** a simple "Hello World" — it's a fully-featured API skeleton with:
- Stateless JWT authentication (HMAC-SHA256) with user registration & login
- Protected REST endpoints requiring Bearer tokens
- Spring Security configuration with BCrypt password hashing
- PostgreSQL support (local & production) with Spring Data JPA
- H2 in-memory database for testing
- Complete test coverage (unit + integration tests)
- Auto-generated API documentation (Swagger UI)
- Docker Compose setup for local development
- AWS Lambda + API Gateway deployment ready
- Code scaffolding tools for rapid domain-specific feature development

Perfect as a **starting point** for building APIs with modern Spring Boot patterns.

## 🚀 Quick Start (2 minutes)

### Prerequisites
- Java 20 (Eclipse Adoptium JDK) — [download](https://adoptium.net/installation/)
- Docker & Docker Compose (optional, for PostgreSQL)
- Maven Wrapper (included — no global Maven install needed)

### Option 1: Run Locally with H2 (No Database Setup)
```bash
# Compile and start
mvnw.cmd spring-boot:run

# App runs at http://localhost:8080
```

### Option 2: Run with Docker (PostgreSQL)
```bash
# Start app + PostgreSQL container
docker compose up --build

# Stop containers (data persists in pgdata volume)
docker compose down

# Stop and delete data
docker compose down -v
```

The API starts on `http://localhost:8080` — same either way.

## 📚 First Steps

1. **Register a user:**
   ```bash
   curl -X POST http://localhost:8080/api/auth/register \
     -H "Content-Type: application/json" \
     -d '{"username":"alice","email":"alice@example.com","password":"secret123"}'
   ```
   Response: `{"token":"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9..."}`

2. **Call the protected endpoint:**
   ```bash
   curl -X GET http://localhost:8080/ \
     -H "Authorization: Bearer <token-from-above>"
   ```
   Response: `Hello, World!`

3. **Explore API docs:**
   Open [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) in your browser.

## 🔐 Features

| Feature | Details | Status |
|---------|---------|--------|
| **REST API** | GET `/` (protected), Swagger UI | ✅ Complete |
| **JWT Authentication** | Stateless HMAC-SHA256 tokens, 1-hour expiry (configurable) | ✅ Complete |
| **User Management** | POST `/api/auth/register`, `/api/auth/login` | ✅ Complete |
| **Spring Security** | BCrypt password hashing, role-based access control ready | ✅ Complete |
| **Database Layer** | Spring Data JPA + H2 (test) / PostgreSQL (dev/prod) | ✅ Complete |
| **API Documentation** | Swagger UI at `/swagger-ui.html`, OpenAPI spec at `/v3/api-docs` | ✅ Complete |
| **Testing** | 15+ integration & unit tests, 100% auth coverage | ✅ Complete |
| **Static Analysis** | SpotBugs + Find Security Bugs | ✅ Complete |
| **Docker Support** | Multi-stage Dockerfile + docker-compose.yml | ✅ Complete |
| **AWS Lambda Ready** | SAM template + Lambda handler (untracked, ready to deploy) | 🔄 In Progress |
| **Code Scaffolding** | `/scaffold-entity` and `/scaffold-spring-api` skills | ✅ Complete |

## 📡 API Endpoints

| Method | Path | Auth Required | Description |
|--------|------|---|-------------|
| **POST** | `/api/auth/register` | No | Register new user, receive JWT token |
| **POST** | `/api/auth/login` | No | Login with credentials, receive JWT token |
| **GET** | `/` | **Yes** | Protected endpoint returning "Hello, World!" |
| **GET** | `/swagger-ui.html` | No | Interactive API documentation |
| **GET** | `/v3/api-docs` | No | OpenAPI 3.0 JSON specification |
| **GET** | `/h2-console` | No | H2 database console (dev/test only) |

### Authentication
All endpoints marked "Yes" under Auth Required need a Bearer token in the `Authorization` header:
```
Authorization: Bearer <your-jwt-token>
```

## 🛠️ Build & Development Commands

```bash
# Compile sources
mvnw.cmd compile

# Run all tests (uses H2 — no Docker needed)
mvnw.cmd test

# Build JAR file
mvnw.cmd package
# Output: target/helloworld-0.0.1-SNAPSHOT.jar

# Run the app locally
mvnw.cmd spring-boot:run

# Security analysis (SpotBugs + Find Security Bugs)
mvnw.cmd spotbugs:check

# Clean build artifacts
mvnw.cmd clean
```

## 🏗️ Project Structure

```
src/main/java/com/example/helloworld/
├── HelloWorldApplication.java             # @SpringBootApplication entry point
├── HelloController.java                   # Protected GET / endpoint
├── StreamLambdaHandler.java              # AWS Lambda adapter (not committed)
└── auth/                                  # Complete authentication module
    ├── config/
    │   └── SecurityConfig.java           # Spring Security + JWT chain
    ├── controller/
    │   └── AuthController.java           # POST /register, /login
    ├── dto/
    │   ├── RegisterRequest.java          # Validated input DTO
    │   ├── LoginRequest.java             # Validated input DTO
    │   └── AuthResponse.java             # Token response DTO
    ├── entity/
    │   └── User.java                     # JPA entity (id, username, email, password, createdAt)
    ├── filter/
    │   └── JwtAuthenticationFilter.java   # Bearer token extraction & validation
    ├── repository/
    │   └── UserRepository.java           # Spring Data JPA (find by username/email)
    └── service/
        ├── AuthService.java              # Registration & login business logic
        ├── JwtService.java               # Token generation, validation, parsing
        └── CustomUserDetailsService.java # Spring Security UserDetailsService

src/main/resources/
├── application.properties                 # Shared config (JWT secret, port, etc.)
├── application-dev.properties             # Docker PostgreSQL profile
├── application-prod.properties            # Production env-var PostgreSQL profile
├── application-lambda.properties          # AWS Lambda/RDS profile (not committed)
└── application-test.properties            # H2 in-memory test profile

src/test/java/com/example/helloworld/
├── HelloWorldApplicationTests.java        # Spring Boot context load test
└── auth/
    ├── AuthIntegrationTest.java          # Full auth workflow tests (register → login → protected)
    └── JwtServiceTest.java               # JWT token operations unit tests
```

**Base Package:** `com.example.helloworld`

## 💾 Database Options

### H2 In-Memory (Default for Testing)
- Used automatically when running `mvnw.cmd test`
- Data is **lost on restart** (by design)
- H2 Console available at `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:authdb`
  - User: `sa`
  - Password: (leave blank)

### PostgreSQL (Local Development with Docker)
```bash
docker compose up --build
```
- Runs PostgreSQL 16 in a container
- Data persists in named volume `pgdata`
- Accessible from Spring app at `localhost:5432`
- Spring profile: `dev` (auto-selected by Docker)

### PostgreSQL (Production / Cloud)
- Configure environment variables or `.env` file
- Spring profile: `prod` (loads from env vars)
- Can connect to managed databases (AWS RDS, Google Cloud SQL, etc.)

### AWS RDS (Lambda Deployment)
- Spring profile: `lambda` (auto-selected when running on Lambda)
- Requires VPC configuration for Lambda → RDS access
- Connection details via environment variables (set by SAM template)

## 🚀 Deployment Options

### Option 1: Docker Compose (Local & Simple Deployments)
```bash
docker compose up --build        # Start app + PostgreSQL
docker compose down              # Stop (data persists)
docker compose down -v           # Stop + delete data
```

### Option 2: Maven (Development)
```bash
mvnw.cmd spring-boot:run        # Start on port 8080
# Requires PostgreSQL running separately, or uses test profile
```

### Option 3: AWS Lambda + API Gateway (Serverless) — 🔄 In Progress
```bash
# First time (guided setup)
sam build
sam deploy --guided

# Subsequent deployments
sam deploy

# Local testing
sam local invoke

# Cleanup
sam delete
```

**Requirements for Lambda:**
- AWS CLI configured with credentials
- SAM CLI installed
- RDS PostgreSQL pre-provisioned
- VPC subnet/security group for Lambda → RDS connectivity

## 📖 Tech Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| Framework | Spring Boot | 3.4.2 |
| Language | Java (Eclipse Adoptium) | 20.0.2 |
| Build Tool | Maven | 3.9.x (via wrapper) |
| Web Server | Tomcat (embedded) | 10.x |
| Security | Spring Security + JWT (jjwt) | 0.12.6 |
| Database (Prod) | PostgreSQL | 16 |
| Database (Test) | H2 | In-memory |
| ORM | Spring Data JPA | Latest |
| API Docs | SpringDoc-OpenAPI | 2.8.4 |
| Testing | JUnit 5 + Spring Boot Test | Latest |
| Code Quality | SpotBugs + Find Security Bugs | Latest |
| Containerization | Docker + Docker Compose | Latest |
| Serverless | AWS Lambda + SAM | Latest |

## 🔒 Security Architecture

- **Authentication:** Stateless JWT (no sessions, scales horizontally)
- **Password Storage:** BCrypt (Spring Security default)
- **Token Signing:** HMAC-SHA256
- **Token Expiration:** 1 hour (configurable in `application.properties`)
- **Input Validation:** Jakarta validation constraints on all DTOs
- **Spring Security:** Configured to protect all endpoints except `/api/auth/*` and Swagger UI
- **HTTPS:** Ready (configure in production via API Gateway, load balancer, or reverse proxy)

## 🧪 Testing

**Run all tests:**
```bash
mvnw.cmd test
```

**Test Coverage:**
- 3 test classes, 15+ test scenarios
- Auth module: 100% coverage (registration, login, token validation, protected access)
- Uses H2 in-memory database — no Docker required for tests
- Integration tests use `@SpringBootTest` with `MockMvc`

**Test Profiles:**
- `test` profile activates automatically during `mvnw test`
- Disables Hibernate lazy loading, uses H2, shorter token expiry for speed

## 📝 Configuration

### Key Properties
```properties
# JWT Configuration (application.properties)
jwt.secret=<base64-encoded-secret>     # REQUIRED — app won't start without it
jwt.expiration=3600000                 # 1 hour in milliseconds
jwt.refresh.expiration=604800000       # 7 days (for refresh tokens, if added)

# Server Port
server.port=8080                       # Configurable

# Database (switches by profile)
spring.profiles.active=dev             # dev (Docker) | prod (env vars) | lambda (RDS)
```

### Environment-Specific Profiles
- **`dev`** — Uses Docker PostgreSQL (localhost:5432)
- **`prod`** — Uses env vars for database connection (cloud deployments)
- **`lambda`** — AWS RDS via environment variables (SAM template sets these)
- **`test`** — H2 in-memory (auto-selected during `mvnw test`)

## 🎓 Development Workflow

### Code Scaffolding Tools
Rapidly generate boilerplate for new features:

```bash
# Generate a new JPA entity with fields
/scaffold-entity Post id:Long title:String content:String authorId:Long

# Generate a complete REST API for an entity (controller, service, repo, DTOs, tests)
/scaffold-spring-api Post
```

### Code Conventions
- **Package Structure:** `com.example.helloworld` → group by feature as project grows
- **Injection:** Constructor injection preferred over field injection
- **Controllers:** Keep thin, delegate business logic to `@Service` classes
- **Configuration:** Use `application.properties` (not YAML)
- **Return Types:** Use `ResponseEntity<>` for REST endpoints
- **Validation:** Use `@Valid` on request body parameters
- **Tests:** Mirror package structure under `src/test/java`, use JUnit 5

### Git Workflow
- **Main Branch:** `main` (always production-ready)
- **Commit Prefixes:**
  - `feat:` New feature
  - `fix:` Bug fix
  - `docs:` Documentation
  - `test:` Test additions
  - `chore:` Maintenance
  - `ai-tooling:` AI/automation setup
- **Pre-commit Hook:** Automatically compiles code before each commit
- **Policy:** Always get user approval before committing

## 🗺️ What's Next

### Immediate (Ready to Implement)
1. **AWS Lambda Deployment** — SAM template & handler files ready, awaiting approval
2. **CORS Support** — For frontend integration
3. **Rate Limiting** — Protect API from abuse

### Medium-term
1. **Domain-specific Endpoints** — Use scaffolding tools to build business models
2. **API Versioning** — Setup `/v1/`, `/v2/` routes
3. **Monitoring & Logging** — Micrometer metrics, structured logging
4. **Database Migrations** — Flyway or Liquibase integration

### Long-term
1. **Caching Layer** — Redis integration
2. **Event-Driven Features** — Message queues (RabbitMQ, Kafka)
3. **Multi-tenancy** — Data isolation & tenant routing
4. **GraphQL Option** — Supplement REST API

## 📖 Documentation

- **[CLAUDE.md](CLAUDE.md)** — Complete developer guide (conventions, architecture, detailed commands)
- **[openspec/project.md](openspec/project.md)** — Project specifications & constraints
- **Swagger UI** — Interactive API docs at `/swagger-ui.html`
- **OpenAPI Spec** — JSON at `/v3/api-docs`

## ⚙️ System Requirements

- **Java:** 20+ (Eclipse Adoptium JDK 20.0.2+)
- **Maven:** 3.9+ (provided via Maven Wrapper, no install needed)
- **Docker:** Optional (for PostgreSQL), use H2 in-memory for testing without Docker
- **AWS CLI + SAM CLI:** Only needed for Lambda deployments

## 📄 License

MIT

---

**Questions?** Check [CLAUDE.md](CLAUDE.md) for detailed architecture and development guidelines.
