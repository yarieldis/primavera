## ADDED Requirements

### Requirement: PostgreSQL Database Backend
The system SHALL use PostgreSQL as its primary database for all persistent data storage.

#### Scenario: Application connects to PostgreSQL on startup
- **WHEN** the application starts with the `dev` or `prod` profile
- **THEN** it SHALL establish a JDBC connection to a PostgreSQL database
- **AND** JPA entities SHALL be mapped to PostgreSQL tables

#### Scenario: Data persists across application restarts
- **WHEN** a user registers and the application is restarted
- **THEN** the registered user's data SHALL still be present in the database

### Requirement: Docker Compose Deployment
The system SHALL be deployable as a set of Docker containers orchestrated by Docker Compose.

#### Scenario: Application starts via Docker Compose
- **WHEN** `docker compose up --build` is executed from the project root
- **THEN** a PostgreSQL container (`db`) and a Spring Boot container (`app`) SHALL start
- **AND** the `app` container SHALL connect to the `db` container over a bridge network using the service name `db` as hostname

#### Scenario: PostgreSQL data survives container recreation
- **WHEN** `docker compose down` followed by `docker compose up` is executed
- **THEN** PostgreSQL data SHALL be retained via a named Docker volume

### Requirement: Spring Profile Configuration
The system SHALL support `dev`, `prod`, and `test` Spring profiles for environment-specific configuration.

#### Scenario: Dev profile uses Docker PostgreSQL
- **WHEN** the application runs with the `dev` profile
- **THEN** it SHALL connect to PostgreSQL at `db:5432/primavera`

#### Scenario: Prod profile uses environment variables
- **WHEN** the application runs with the `prod` profile
- **THEN** it SHALL read database connection parameters from environment variables (`DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`)

#### Scenario: Test profile uses H2 in-memory
- **WHEN** tests run with the `test` profile
- **THEN** the application SHALL use an H2 in-memory database
- **AND** no running PostgreSQL instance SHALL be required

### Requirement: Containerized Application Image
The application SHALL be packaged as a Docker image using a multi-stage build.

#### Scenario: Multi-stage build produces lean image
- **WHEN** the Docker image is built
- **THEN** the build stage SHALL compile the application with Maven
- **AND** the runtime stage SHALL contain only the JRE and the application JAR
