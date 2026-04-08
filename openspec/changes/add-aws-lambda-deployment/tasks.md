## 1. Dependencies
- [x] 1.1 Add `aws-serverless-java-container-springboot3` dependency to `pom.xml`
- [x] 1.2 Add `maven-shade-plugin` to build an uber-JAR suitable for Lambda deployment

## 2. Lambda Handler
- [x] 2.1 Create `StreamLambdaHandler.java` in `com.github.primavera` — implements `RequestStreamHandler`, initializes `SpringBootLambdaContainerHandler`

## 3. Spring Profile for Lambda
- [x] 3.1 Create `application-lambda.properties` — PostgreSQL via env vars (`DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`), `ddl-auto=update`, no H2 console

## 4. SAM Template
- [x] 4.1 Create `template.yaml` with:
  - `AWS::Serverless::Function` — Java 21 runtime, `StreamLambdaHandler::handleRequest`, 512MB memory, 30s timeout, SnapStart enabled
  - `AWS::Serverless::HttpApi` — catch-all proxy route `/{proxy+}` to the Lambda
  - Parameters for VPC subnet IDs, security group IDs, and DB connection details
  - Environment variables mapping to Spring profile `lambda`
- [x] 4.2 Create `samconfig.toml` with default stack name, region, and deployment parameters

## 5. Tests
- [x] 5.1 Run `mvnw.cmd test` — confirm existing tests still pass (no changes to test code expected)
- [ ] 5.2 Run `sam build` — confirm the SAM build succeeds
- [ ] 5.3 Run `sam local invoke` (optional) — smoke test locally with SAM CLI

## 6. Documentation
- [x] 6.1 Update `CLAUDE.md` — add SAM build/deploy commands, Lambda architecture notes
- [x] 6.2 Update `openspec/project.md` — add AWS Lambda + API Gateway to tech stack
