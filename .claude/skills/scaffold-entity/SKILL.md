# Scaffold Entity

Generates a JPA entity class with standard fields and configuration.

## Usage
`/scaffold-entity <EntityName> <field1:type> <field2:type> ...`

Example: `/scaffold-entity Product name:String price:BigDecimal description:String active:boolean`

## What it creates
Under `src/main/java/com/github/primavera/`:

1. **`<entity>/model/<EntityName>.java`** — JPA `@Entity` with:
   - `@Id` with `@GeneratedValue(strategy = GenerationType.IDENTITY)`
   - All specified fields with appropriate JPA annotations
   - `@Column` annotations where needed (nullable, length, unique)
   - `createdAt` (`@CreatedDate`) and `updatedAt` (`@LastModifiedDate`) audit fields
   - No-arg constructor (required by JPA) + all-args constructor
   - Getters and setters for all fields

## Conventions
- Table name: lowercase plural of entity name (e.g., `Product` → `products`)
- Use `@Table(name = "...")` explicitly
- String fields default to `@Column(nullable = false)`
- Add `@EntityListeners(AuditingEntityListener.class)` for audit fields
- Follow existing code style in the project
