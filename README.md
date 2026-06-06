# financial-app-users

Users microservice — authentication, JWT issuance, refresh tokens, and user profile management.

## Port: 8081

## Database Schema: `users`

## Endpoints
```
POST   /api/v1/auth/login
POST   /api/v1/auth/refresh
POST   /api/v1/auth/logout
GET    /api/v1/users/{id}
PUT    /api/v1/users/{id}
PUT    /api/v1/users/{id}/password
GET    /api/v1/users/{id}/profile
PUT    /api/v1/users/{id}/profile
```

## Environment Variables
See `.env.example`.

## Local Development

```bash
# Install parent POM first (only once)
cd ../financial-app-parent && mvn install -N

# Run
cd ../financial-app-users
cp .env.example .env
mvn spring-boot:run
```

## Build
```bash
mvn clean package -DskipTests
```

## Swagger
`http://localhost:8081/swagger-ui.html`

## CI/CD

| Workflow | Trigger | Does |
|---|---|---|
| `ci.yml` | PRs; push to develop/master | tests + docker build via shared `backend-ci.yml` |
| `docker-publish.yml` | push to master; `v*` tags | GHCR publish: `latest`, `sha-*`, semver on tags |
| `release.yml` | manual (bump dropdown) | next `vX.Y.Z` tag + Release + versioned publish |

Reusable workflows live in the root repo `Sergio-Smirnoff/financial-app`.
