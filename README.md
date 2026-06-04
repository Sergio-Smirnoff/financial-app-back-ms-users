# ms-users — Auth & Users Service

Auth service for the financial-app platform. Handles registration, login, token refresh, and logout. All tokens live in HttpOnly cookies. Downstream services receive the authenticated user's identity via the `X-User-Id` header injected by the gateway.

- **Port:** 8081
- **DB schema:** `users`
- **Tech stack:** Java 21, Spring Boot 3.4.2, Spring MVC, Spring Security (stateless), JWT (JJWT / HMAC-SHA), BCrypt, Flyway, Kafka
- **Kafka topic published:** `user.registered`

> Full design: `docs/specs/services/ms-users.md` (parent workspace).

---

## Endpoints

All routes under `/api/v1/auth`. Exempt from JWT validation at the gateway.

| Method | Path | Request body | Success response | Status |
|--------|------|-------------|-----------------|--------|
| `POST` | `/api/v1/auth/register` | `{ email, password (≥8), firstName, lastName }` | `ApiResponse<AuthResponse>` + 3 cookies set | `201 Created` |
| `POST` | `/api/v1/auth/login` | `{ email, password }` | `ApiResponse<AuthResponse>` + 3 cookies set | `200 OK` |
| `POST` | `/api/v1/auth/refresh` | — (reads `refresh_token` cookie) | `ApiResponse<AuthResponse>` + 3 cookies refreshed | `200 OK` |
| `POST` | `/api/v1/auth/logout` | — | `ApiResponse<Void>` + 3 cookies zeroed | `200 OK` |

`AuthResponse` fields: `userId`, `email`, `firstName`, `lastName`.

### Error responses

| Scenario | Status |
|----------|--------|
| Email already exists | `409 Conflict` |
| Wrong email or password | `401 Unauthorized` |
| Invalid / expired JWT | `401 Unauthorized` |
| Missing `refresh_token` cookie | `401 Unauthorized` |
| User not found during refresh | `404 Not Found` |
| Bean validation failure | `400 Bad Request` |

---

## Cookies

All cookies use `SameSite=Lax`. `Secure` is driven by the `app.cookie.secure` env var (false in local dev, true in production).

| Cookie | HttpOnly | Path | Max-Age | Value |
|--------|----------|------|---------|-------|
| `access_token` | Yes | `/api` | 24 h | Signed JWT (access) |
| `refresh_token` | Yes | `/api/v1/auth/refresh` | 7 d | Signed JWT (refresh) |
| `user_info` | No | `/` | 24 h | `id\|email\|firstName+lastName` URL-encoded |
| `XSRF-TOKEN` | No | `/` | session | CSRF token set by Spring Security |

`user_info` is the only cookie readable by JavaScript; the Next.js middleware uses it to gate dashboard routes. On logout all three application cookies are reissued with `maxAge=0`.

---

## File distribution

```
back/ms-users/src/main/java/com/financialapp/users/
├── UsersApplication.java
├── application/
│   ├── AuthenticateUserUseCaseImp.java
│   ├── RegisterUserUseCaseImpl.java
│   └── RefreshSessionUseCaseImpl.java
├── domain/
│   ├── event/
│   │   ├── DomainEvent.java
│   │   ├── DomainEventPublisher.java
│   │   └── UserRegisteredEvent.java
│   ├── exception/
│   │   ├── DuplicateEmailException.java
│   │   ├── InvalidCredentialsException.java
│   │   └── UserNotFoundException.java
│   ├── gateway/
│   │   ├── AuthenticationProviderGateway.java
│   │   └── PasswordHashGateway.java
│   ├── model/
│   │   ├── Session.java
│   │   ├── User.java
│   │   └── valueObject/
│   │       └── UserId.java
│   ├── repository/
│   │   └── UserRepository.java
│   └── usecase/
│       ├── AuthenticateUserUseCase.java
│       ├── RefreshSessionUseCase.java
│       ├── RegisterUserUseCase.java
│       └── command/
│           ├── AuthenticateUserCommand.java
│           ├── RefreshSessionCommand.java
│           └── RegisterUserCommand.java
├── infrastructure/
│   ├── config/
│   │   ├── CsrfCookieFilter.java
│   │   ├── InternalAuthFilter.java
│   │   ├── JwtProperties.java
│   │   ├── KafkaConfig.java
│   │   └── SecurityConfig.java
│   ├── gateway/
│   │   ├── AuthenticationProviderGatewayImpl.java
│   │   └── PasswordHashGatewayImpl.java
│   ├── messaging/
│   │   ├── DomainEventPublisherImpl.java
│   │   ├── TransactionalKafkaEvent.java
│   │   ├── TransactionalKafkaListener.java
│   │   └── payload/
│   │       └── UserRegisteredPayload.java
│   └── persistence/
│       ├── entity/
│       │   └── UserJpaEntity.java
│       ├── jpa/
│       │   └── UserJpaRepository.java
│       ├── mapper/
│       │   └── UserPersistenceMapper.java
│       └── repository/
│           └── UserRepositoryImpl.java
└── web/
    ├── CookieService.java
    ├── controller/
    │   └── AuthController.java
    ├── dto/
    │   ├── request/
    │   │   ├── LoginRequest.java
    │   │   └── RegisterRequest.java
    │   └── response/
    │       ├── ApiResponse.java
    │       └── AuthResponse.java
    └── error/
        └── GlobalExceptionHandler.java
```

---

## Run

```bash
# Via dev script (infra + service, hot-reload)
./scripts/dev.sh local service-users

# Direct Maven
cd back/ms-users
mvn spring-boot:run

# Tests
mvn test
mvn test -Dtest=SomeSpecificTest
```

Swagger UI: http://localhost:8081/swagger-ui.html

---

## Required environment variables

| Variable | Purpose |
|---|---|
| `SPRING_DATASOURCE_URL` | PostgreSQL JDBC URL — e.g. `jdbc:postgresql://postgres:5432/financialapp?currentSchema=users` |
| `SPRING_DATASOURCE_USERNAME` | Database user |
| `SPRING_DATASOURCE_PASSWORD` | Database password |
| `KAFKA_BOOTSTRAP_SERVERS` | Kafka broker — e.g. `kafka:9092` |
| `JWT_SECRET` | Base64-encoded HMAC-SHA secret used to sign access and refresh tokens |
| `JWT_EXPIRATION` | Access token TTL in milliseconds (default 86400000 = 24 h) |
| `JWT_REFRESH_EXPIRATION` | Refresh token TTL in milliseconds (default 604800000 = 7 d) |
| `INTERNAL_AUTH_TOKEN` | Shared secret for `X-Internal-Token` header; service hard-fails at startup without it |
| `COOKIE_SECURE` | Set `true` in production to add the `Secure` flag to all auth cookies |

Copy `.env.example` (workspace root) to `.env` in this directory and fill in the values.
