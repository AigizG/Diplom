# Repository Guidelines

## Project Structure & Module Organization

This is a Spring Boot 3 backend using Maven. Source code lives under `src/main/java/com/example/activeleisure`, grouped by domain modules such as `auth`, `booking`, `payment`, `equipment`, `report`, plus shared `common`, `config`, `dto`, `exception`, `mapper`, and `security` packages.

Resources live in `src/main/resources`. Flyway migrations are in `src/main/resources/db/migration`. Tests live in `src/test/java/com/example/activeleisure`.

## Build, Test, and Development Commands

```powershell
mvn test
mvn package
mvn spring-boot:run
docker compose up --build
```

`mvn test` runs JUnit/Mockito tests. `mvn package` builds the executable jar. `mvn spring-boot:run` starts the API locally with configured PostgreSQL. Docker Compose starts PostgreSQL and the Spring Boot app.

## Coding Style & Naming Conventions

Use Java 17 conventions with 4-space indentation. Classes and records use `PascalCase`; methods, fields, and variables use `camelCase`; enum constants use `UPPER_SNAKE_CASE`. Keep controllers thin, put business rules in services, and expose data only through DTO records from `dto`.

## Testing Guidelines

Use JUnit 5 and Mockito. Name tests after the service under test, for example `BookingServiceTest`. Cover business rules such as booking capacity, mock payment status transitions, equipment assignment, and auth failures. Run `mvn test` before submitting changes.

## Commit & Pull Request Guidelines

Git history was not available in this environment, so use concise imperative commit messages such as `Add booking payment flow`. Pull requests should include a summary, tested commands, linked task or issue, and screenshots only when API documentation or UI output changes.

## Security & Configuration Tips

Never add real payment integrations. Payment behavior must remain internal and mock-only. Keep secrets in environment variables: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`, and `JWT_EXPIRATION_MS`. Do not commit generated build output, database volumes, or local IDE state.
