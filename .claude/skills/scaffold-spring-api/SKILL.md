# Scaffold Spring API

Generates a complete REST API layer for a given domain entity.

## Usage
`/scaffold-spring-api <EntityName>` (e.g., `/scaffold-spring-api User`)

## What it creates
Given an entity name, generate these files under `src/main/java/com/github/primavera/`:

1. **`<entity>/model/<EntityName>.java`** — JPA `@Entity` class with `@Id`, `@GeneratedValue`, and common fields (`id`, `createdAt`, `updatedAt`)
2. **`<entity>/repository/<EntityName>Repository.java`** — Spring Data JPA `@Repository` interface extending `JpaRepository`
3. **`<entity>/service/<EntityName>Service.java`** — `@Service` class with constructor injection of the repository, implementing CRUD methods
4. **`<entity>/controller/<EntityName>Controller.java`** — `@RestController` with `@RequestMapping("/api/<entities>")` exposing GET (list + by ID), POST, PUT, DELETE
5. **`<entity>/dto/<EntityName>Request.java`** — Request DTO with Jakarta validation annotations
6. **`<entity>/dto/<EntityName>Response.java`** — Response DTO

And under `src/test/java/com/github/primavera/`:
7. **`<entity>/controller/<EntityName>ControllerTest.java`** — `@WebMvcTest` with MockMvc testing all endpoints

## Conventions
- Use constructor injection (no `@Autowired` on fields)
- Use `ResponseEntity<>` return types in controllers
- Use `@Valid` on request body parameters
- Pluralize the entity name for the REST path (e.g., `User` → `/api/users`)
- Follow existing code style in the project
- Add appropriate HTTP status codes (201 for create, 204 for delete, 404 for not found)
