# Backend

Spring Boot backend for the Anglican church management platform.

## Stack

- Java 17
- Spring Boot 3.2.x
- Spring Web
- Spring Security
- Spring Data JPA
- PostgreSQL
- Flyway
- JWT auth
- Bucket4j rate limiting
- H2 for tests

## What It Does

The backend currently covers:

- authentication and JWT session management
- church management
- people and memberships
- app roles and permissions
- church-scoped app users
- executive and office-bearing role assignment
- groups
- elections and voting
- collections and counting sessions
- achievements
- cross-church sharing

## Project Layout

- `backend/src/main/java` : application code
- `backend/src/main/resources/application.properties` : default runtime configuration
- `backend/src/main/resources/db/migration` : Flyway migrations
- `backend/src/test/java` : tests

## Development Setup

### 1. Prerequisites

Install:

- Java 17
- PostgreSQL 14+ recommended
- Maven wrapper is included, so a system Maven install is optional

### 2. Create the database

Example local PostgreSQL setup:

```sql
CREATE DATABASE church_app_db;
CREATE USER church_user WITH PASSWORD 'church_password';
GRANT ALL PRIVILEGES ON DATABASE church_app_db TO church_user;
ALTER DATABASE church_app_db OWNER TO church_user;
```

The default local config in `/Users/yormen/Documents/Personal/Church Anglican/church-app/backend/src/main/resources/application.properties` expects:

- database: `church_app_db`
- username: `church_user`
- password: `church_password`
- port: `5432`

### 3. Review application properties

Default backend port:

- `8081`

Important properties in `/Users/yormen/Documents/Personal/Church Anglican/church-app/backend/src/main/resources/application.properties`:

- `server.port=8081`
- `spring.datasource.url=jdbc:postgresql://localhost:5432/church_app_db`
- `spring.datasource.username=church_user`
- `spring.datasource.password=church_password`
- `app.security.jwt.secret=...`
- `app.security.jwt.access-ttl-minutes=15`
- `app.security.jwt.refresh-ttl-days=7`
- `app.security.bootstrap.enabled=false`

For any non-local environment, change the JWT secret before starting the app.

### 4. Run the backend

From `/Users/yormen/Documents/Personal/Church Anglican/church-app/backend`:

```bash
./mvnw spring-boot:run
```

Or build and run the jar:

```bash
./mvnw clean package
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

## Database and Migrations

Flyway runs automatically on startup.

Current migration directory:

- `/Users/yormen/Documents/Personal/Church Anglican/church-app/backend/src/main/resources/db/migration`

Current schema includes the access identifier changes and seeded access model.

Important note:

- if you change entities, controllers, DTOs, or security rules, you must restart the backend for the running instance to pick them up
- if you change schema, add a new Flyway migration rather than editing an old one

## Seeded Access and Bootstrap Behavior

At startup, the backend provisions:

- access permissions
- default role catalog for each church
- backoffice administration church if missing
- a default backoffice admin user if missing, or updates it if present

Default seeded backoffice account:

- username: `yormen1@gmail.com`
- password: `password`

This is suitable for development only.

Do not keep this hardcoded credential behavior in production without moving it behind environment-specific configuration.

## Access Model

The system now distinguishes roles with the `identifier` field:

- `BACKOFFICE`
- `CHURCH`

Examples:

- backoffice users manage church enrollment and central administration
- church users operate only within church-scoped workflows

Authorities are derived from:

- role names
- role identifiers as `IDENTIFIER_BACKOFFICE` or `IDENTIFIER_CHURCH`
- permission names such as `PEOPLE_MANAGE`, `ROLE_ASSIGN`, `FINANCE_MANAGE`

## Main Development Commands

From `/Users/yormen/Documents/Personal/Church Anglican/church-app/backend`:

Run tests:

```bash
./mvnw test
```

Run the app:

```bash
./mvnw spring-boot:run
```

Build a jar:

```bash
./mvnw clean package
```

## API and Docs

When the backend is running locally:

- API base: [http://localhost:8081/api/v1](http://localhost:8081/api/v1)
- Swagger UI: [http://localhost:8081/swagger-ui/index.html](http://localhost:8081/swagger-ui/index.html)
- OpenAPI docs: [http://localhost:8081/v3/api-docs](http://localhost:8081/v3/api-docs)

## Local Development Workflow

Recommended workflow:

1. Start PostgreSQL.
2. Verify `application.properties` points to the correct local database.
3. Run `./mvnw test`.
4. Start the backend with `./mvnw spring-boot:run`.
5. Use Swagger or the frontend apps to exercise flows.
6. When making API or DTO changes, restart the backend.
7. When making schema changes, add and run a new Flyway migration.

## Frontend Integration Notes

Current frontend assumptions:

- backoffice frontend talks to `http://localhost:8081/api/v1`
- church portal frontend talks to `http://localhost:8081/api/v1`
- login is via `/auth/login`
- refresh is via `/auth/refresh`

Current verified onboarding flow:

1. Backoffice user logs in.
2. Backoffice creates a church.
3. Backoffice creates the first `CHURCH_ADMIN` app user for that church.
4. Church admin logs in via the portal.
5. Church admin creates executive users and church structure such as groups.

## Deployment Process

### Minimum production requirements

- Java 17 runtime
- PostgreSQL database
- stable JWT secret set via configuration
- environment-specific datasource credentials
- reverse proxy or gateway in front of the app if exposed publicly

### Recommended deployment steps

1. Provision PostgreSQL.
2. Create production database and user.
3. Set production application properties or environment-backed config.
4. Build the artifact:

```bash
./mvnw clean package
```

5. Deploy the generated jar from `backend/target/`.
6. Start the application.
7. Verify Flyway migration success in logs.
8. Verify `/swagger-ui/index.html` and a login request.
9. Disable or harden any dev-only bootstrap behavior.

### Production configuration checklist

Before production, change at minimum:

- datasource URL
- datasource username/password
- JWT secret
- seeded admin credential strategy
- CORS policy if exposing browser clients across domains
- rate-limit settings if default values are too permissive

## Security Notes

- JWT secret in local config is placeholder quality and must be replaced outside development.
- The seeded `yormen1@gmail.com` / `password` account is for development convenience.
- Church-scoped users should only be able to manage their own church resources.
- Backoffice users can enroll churches and provision the first church admin.

## Testing

Current test command:

```bash
./mvnw test
```

The test suite uses H2 and validates application startup plus key election flows.

## Known Operational Notes

- If a newly added endpoint appears missing while the backend is "already running", the process likely needs a restart.
- If a controller returns Hibernate proxy serialization errors, use DTO responses rather than returning entities directly.
- Keep Flyway migrations append-only.

## Suggested Next Improvements

- move dev seed credentials to profile-based or environment-based config
- add more integration tests for church onboarding and church-admin flows
- tighten CORS for deployed environments
- add list/search endpoints for churches, people, and roles where needed
