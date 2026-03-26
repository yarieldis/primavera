# Change: Add AWS API Gateway + Lambda deployment

## Why
The project currently runs only as a Docker Compose stack (app + PostgreSQL). Adding an AWS Lambda deployment option enables serverless hosting with automatic scaling, pay-per-request pricing, and zero infrastructure management — ideal for low-to-moderate traffic APIs.

## What Changes
- Add `aws-serverless-java-container-springboot3` dependency to wrap the existing Spring Boot app for Lambda
- Create a `StreamLambdaHandler` class (~20 lines) that bridges API Gateway events to Spring's `DispatcherServlet`
- Add a SAM template (`template.yaml`) defining the Lambda function + API Gateway (HTTP API) + VPC config for RDS access
- Add a new Spring profile `lambda` for Lambda-specific config (RDS endpoint, SnapStart settings)
- Enable **Lambda SnapStart** to reduce cold starts from ~8s to ~200ms
- Add `samconfig.toml` with default deployment parameters
- All existing code (controllers, services, security, JWT) stays **unchanged**

## Impact
- Affected specs: (new) `aws-deployment` capability
- Affected code:
  - `pom.xml` — add `aws-serverless-java-container-springboot3` dependency
  - `StreamLambdaHandler.java` — new class in base package, Lambda entry point
  - `application-lambda.properties` — new profile for Lambda/RDS config
  - `template.yaml` — SAM template (API Gateway + Lambda + IAM)
  - `samconfig.toml` — SAM CLI deployment defaults
  - `CLAUDE.md` — updated with SAM deploy commands
- Unaffected code:
  - All controllers, services, security config, entities, repositories — zero changes
  - Docker Compose setup — remains the local development path
  - Test suite — no changes needed
