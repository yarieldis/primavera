## ADDED Requirements

### Requirement: Lambda Deployment via SAM
The system SHALL be deployable to AWS Lambda behind API Gateway using the SAM CLI.

#### Scenario: SAM build produces deployable artifact
- **WHEN** `sam build` is executed from the project root
- **THEN** a deployable Lambda artifact SHALL be produced containing the application uber-JAR

#### Scenario: SAM deploy creates the AWS stack
- **WHEN** `sam deploy --guided` is executed with valid AWS credentials
- **THEN** a CloudFormation stack SHALL be created containing the Lambda function, API Gateway HTTP API, and IAM role
- **AND** the API Gateway endpoint SHALL proxy all HTTP requests to the Lambda function

### Requirement: Lambda Request Handling
The system SHALL handle API Gateway HTTP API proxy requests via a `StreamLambdaHandler` that delegates to the Spring Boot `DispatcherServlet`.

#### Scenario: API Gateway request reaches Spring controller
- **WHEN** an HTTP request arrives at the API Gateway endpoint
- **THEN** the `StreamLambdaHandler` SHALL translate the API Gateway event into a servlet request
- **AND** Spring's `DispatcherServlet` SHALL route it to the appropriate controller
- **AND** the response SHALL be translated back to an API Gateway response

#### Scenario: Authentication works on Lambda
- **WHEN** a request with a valid JWT Bearer token reaches the Lambda function
- **THEN** the Spring Security filter chain SHALL authenticate the request
- **AND** the protected endpoint SHALL return the expected response

### Requirement: Lambda SnapStart
The system SHALL enable Lambda SnapStart to reduce cold start latency.

#### Scenario: SnapStart is configured
- **WHEN** the SAM template is deployed
- **THEN** the Lambda function SHALL have `SnapStart.ApplyOn` set to `PublishedVersions`

### Requirement: Lambda Spring Profile
The system SHALL provide a `lambda` Spring profile for AWS Lambda-specific configuration.

#### Scenario: Lambda profile connects to RDS
- **WHEN** the application starts with the `lambda` profile
- **THEN** it SHALL connect to PostgreSQL using environment variables (`DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`)

### Requirement: Coexistence with Docker Compose
The Docker Compose deployment SHALL remain functional and unaffected by the Lambda deployment additions.

#### Scenario: Docker Compose still works
- **WHEN** `docker compose up --build` is executed
- **THEN** the application SHALL start with the `dev` profile and connect to the Docker PostgreSQL container
- **AND** all existing functionality SHALL be preserved
