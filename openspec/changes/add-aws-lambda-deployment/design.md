## Context
The app is a Spring Boot 3.4.2 REST API with Spring Security + JWT, backed by PostgreSQL. It currently deploys via Docker Compose. The goal is to add a serverless deployment path using AWS API Gateway + Lambda, keeping the existing codebase unchanged.

## Goals / Non-Goals
- Goals:
  - Deploy the existing Spring Boot app to AWS Lambda behind API Gateway
  - Use AWS RDS (PostgreSQL) as the database backend in AWS
  - Minimize cold start latency with Lambda SnapStart
  - Provide a SAM template for repeatable deployments (`sam build && sam deploy`)
  - Keep Docker Compose as the local development path — both deployment models coexist
- Non-Goals:
  - Rewriting controllers as functional beans (Spring Cloud Function approach)
  - Custom domain name, WAF, or CloudFront setup
  - CI/CD pipeline (GitHub Actions for SAM deploy) — future change
  - RDS provisioning via SAM/CloudFormation (manual or separate IaC for now)
  - GraalVM native image compilation — future optimization

## Decisions

### 1. AWS Serverless Java Container adapter
- **Decision:** Use `com.amazonaws.serverless:aws-serverless-java-container-springboot3` to wrap the app.
- **Why:** This is AWS's official adapter for running Spring Boot 3.x on Lambda. It translates API Gateway proxy events into `HttpServletRequest`/`HttpServletResponse` pairs and routes them through Spring's `DispatcherServlet`. Zero changes to controllers, security, or services.
- **Alternatives considered:**
  - Spring Cloud Function + AWS adapter — requires rewriting all endpoints as `Function<>` beans; too invasive
  - Lambda Web Adapter (container-based) — runs the full Tomcat inside Lambda; heavier, slower cold starts
  - Refactor to plain Lambda handlers — loses Spring ecosystem entirely

### 2. API Gateway type: HTTP API (v2)
- **Decision:** Use API Gateway HTTP API (not REST API v1).
- **Why:** HTTP API is cheaper (~70% lower cost), faster (~60% lower latency), and supports JWT authorizers natively. REST API v1 features (usage plans, API keys, request validation) aren't needed for this project.
- **SAM resource:** `AWS::Serverless::HttpApi`

### 3. Lambda SnapStart
- **Decision:** Enable SnapStart on the Lambda function.
- **Why:** Spring Boot cold starts on Lambda are 6-10s without SnapStart. With SnapStart, the JVM snapshot is restored in ~200ms. This is the single biggest improvement for user experience.
- **SAM config:** `SnapStart: ApplyOn: PublishedVersions`
- **Trade-off:** SnapStart requires `java17` or later runtime and `arm64`/`x86_64` architecture. Java 20 is supported via `provided.al2023` custom runtime or the `java21` managed runtime (closest to Java 20).

### 4. Lambda runtime
- **Decision:** Use the `java21` managed runtime.
- **Why:** The project uses Java 20, but Lambda doesn't offer a `java20` runtime. `java21` is the closest managed runtime and is backward-compatible for our code (no Java 21-specific APIs are used). This avoids the complexity of a custom runtime.
- **Trade-off:** Minor version mismatch (20 vs 21). The alternative — building a custom runtime with `provided.al2023` + a Java 20 layer — adds significant complexity for no practical benefit.

### 5. Database: AWS RDS PostgreSQL
- **Decision:** The Lambda function connects to an existing RDS PostgreSQL instance via the `lambda` Spring profile.
- **Why:** Lambda is stateless — H2 in-memory and Docker PostgreSQL aren't options. RDS is the natural managed PostgreSQL service.
- **Config:** Connection parameters via environment variables in `template.yaml` (`DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`), matching the `prod` profile pattern.
- **VPC:** Lambda must be in the same VPC as RDS. The SAM template accepts VPC subnet and security group IDs as parameters.

### 6. Project structure
- **Decision:** Add a single new class `StreamLambdaHandler.java` in the base package. Add `template.yaml` and `samconfig.toml` at the project root.
- **Why:** Minimal footprint. The handler is the only Java code that knows about Lambda — everything else is standard Spring Boot.

```
src/main/java/com/example/helloworld/
├── StreamLambdaHandler.java          # NEW — Lambda entry point
├── HelloWorldApplication.java
├── HelloController.java
└── auth/...
src/main/resources/
├── application-lambda.properties     # NEW — RDS config for Lambda
template.yaml                         # NEW — SAM template
samconfig.toml                        # NEW — SAM deployment defaults
```

## Risks / Trade-offs
- **Cold starts** — SnapStart mitigates but doesn't eliminate. First request after a deploy or scale-up event may still take ~1-2s. Provisioned Concurrency is an option but adds cost.
- **Java 21 vs 20 mismatch** — No practical risk for this codebase, but worth noting. Could update the project to Java 21 in a future change.
- **RDS in VPC** — Lambda needs VPC access, which adds ~1s to cold starts for ENI attachment. SnapStart helps here too.
- **JAR size** — Spring Boot uber-JAR will be ~40-60MB. Lambda allows up to 250MB (unzipped), so well within limits.
- **Timeout** — API Gateway HTTP API has a 30s timeout. Spring Boot init + first request must complete within this window. SnapStart makes this a non-issue.

## Open Questions
- Should we provision RDS via the SAM template (adds complexity) or assume it's pre-existing?
- Should we add a `/health` endpoint (unauthenticated) for Lambda health checks?
- Should we update the project Java version from 20 to 21 to match the Lambda runtime?
