# ms-users — Auth & Users Service

Auth service for the financial-app platform. Handles registration, login, token refresh, server-side logout, session management, inactivity policy enforcement, user preferences, manual currency conversion rates, profile editing, and password change. All tokens live in HttpOnly cookies. Downstream services receive the authenticated user's identity via the `X-User-Id` header injected by the gateway.

- **Port:** 8081
- **DB schema:** `users`
- **Tech stack:** Java 21, Spring Boot 3.4.2, Spring MVC, Spring Security (stateless), JWT (JJWT / HMAC-SHA), BCrypt, Flyway, Kafka
- **Kafka topic published:** `users.user.registered` (CloudEvents 1.0, binary mode; via transactional outbox)

> Full design: `docs/specs/services/ms-users.md` (parent workspace).

---

## Endpoints

All public auth routes under `/api/v1/auth`. User settings, sessions, profile, preferences, and currency rates live under `/api/v1/users/me/**`.

All responses use the shared envelope `{ status, title, code, message, data }` from `commons-core` (`com.financialapp.commons.core.response.ApiResponse`).

### Auth Endpoints

| Method | Path | Request body | Success response | Status |
|--------|------|-------------|-----------------|--------|
| `POST` | `/api/v1/auth/register` | `{ email, password (≥8), firstName, lastName, rememberMe? }` | `ApiResponse<AuthResponse>` + 3 cookies set | `201 Created` |
| `POST` | `/api/v1/auth/login` | `{ email, password, rememberMe? }` | `ApiResponse<AuthResponse>` + 3 cookies set | `200 OK` |
| `POST` | `/api/v1/auth/refresh` | — (reads `refresh_token` cookie) | `ApiResponse<AuthResponse>` + 3 cookies refreshed | `200 OK` |
| `POST` | `/api/v1/auth/logout` | — (reads `refresh_token` cookie) | `ApiResponse<Void>` + 3 cookies zeroed | `200 OK` |

`AuthResponse` fields: `userId`, `email`, `firstName`, `lastName`.

### User Settings & Session Endpoints (`/api/v1/users/me`)

| Method | Path | Headers / Request body | Success response | Status |
|--------|------|-----------------------|-----------------|--------|
| `GET` | `/api/v1/users/me/sessions` | `X-User-Id` | `ApiResponse<List<SessionResponse>>` | `200 OK` |
| `DELETE` | `/api/v1/users/me/sessions/{id}` | `X-User-Id` | `ApiResponse<Void>` | `200 OK` |
| `GET` | `/api/v1/users/me/preferences` | `X-User-Id` | `ApiResponse<UserPreferencesResponse>` | `200 OK` |
| `PUT` | `/api/v1/users/me/preferences` | `X-User-Id`, `{ maxIdleMinutes, timezone, primaryCurrency, secondaryCurrency, numberFormat, decimals, colorForAmounts }` | `ApiResponse<UserPreferencesResponse>` | `200 OK` |
| `GET` | `/api/v1/users/me/currency-rates` | `X-User-Id` | `ApiResponse<List<ManualCurrencyRateResponse>>` | `200 OK` |
| `PUT` | `/api/v1/users/me/currency-rates/{currency}` | `X-User-Id`, `{ ratePerArs }` | `ApiResponse<ManualCurrencyRateResponse>` | `200 OK` |
| `DELETE` | `/api/v1/users/me/currency-rates/{currency}` | `X-User-Id` | `ApiResponse<Void>` | `200 OK` |
| `PUT` | `/api/v1/users/me/profile` | `X-User-Id`, `{ firstName, lastName }` | `ApiResponse<UserProfileResponse>` | `200 OK` |
| `PUT` | `/api/v1/users/me/password` | `X-User-Id`, `{ currentPassword, newPassword (≥8) }` | `ApiResponse<Void>` | `200 OK` |

---

## Cookies

All cookies use `SameSite=Lax`. `Secure` is driven by the `app.cookie.secure` env var (false in local dev, true in production).

| Cookie | HttpOnly | Path | Max-Age | Value |
|--------|----------|------|---------|-------|
| `access_token` | Yes | `/api` | 24 h | Signed JWT (access) |
| `refresh_token` | Yes | `/api/v1/auth/refresh` | 7 d (30 d if `rememberMe`) | Signed JWT (refresh) |
| `user_info` | No | `/` | 24 h | `id\|email\|firstName+lastName` URL-encoded |
| `XSRF-TOKEN` | No | `/` | session | CSRF token set by Spring Security |

`user_info` is readable by JavaScript for client-side routing. On logout all three application cookies are reissued with `maxAge=0`.

---

## File distribution

```
back/ms-users/src/main/java/com/financialapp/users/
├── UsersApplication.java
├── application/
│   ├── AuthenticateUserUseCaseImp.java
│   ├── GetUserPreferencesUseCaseImpl.java
│   ├── ListManualCurrencyRatesUseCaseImpl.java
│   ├── ListUserSessionsUseCaseImpl.java
│   ├── RefreshSessionUseCaseImpl.java
│   ├── RegisterUserUseCaseImpl.java
│   ├── RevokeUserSessionUseCaseImpl.java
│   ├── SetManualCurrencyRateUseCaseImpl.java
│   ├── DeleteManualCurrencyRateUseCaseImpl.java
│   ├── UpdateUserPasswordUseCaseImpl.java
│   ├── UpdateUserProfileUseCaseImpl.java
│   └── UpdateUserPreferencesUseCaseImpl.java
├── domain/
│   ├── event/
│   │   ├── DomainEvent.java
│   │   ├── DomainEventPublisher.java
│   │   └── UserRegisteredEvent.java
│   ├── exception/
│   │   ├── DomainError.java
│   │   ├── DuplicateEmailException.java
│   │   ├── InvalidCredentialsException.java
│   │   ├── InvalidTokenException.java
│   │   ├── SessionExpiredException.java
│   │   ├── SessionNotFoundException.java
│   │   ├── UserNotFoundException.java
│   │   ├── WeakPasswordException.java
│   │   └── WrongCurrentPasswordException.java
│   ├── gateway/
│   │   ├── AuthenticationProviderGateway.java
│   │   └── PasswordHashGateway.java
│   ├── model/
│   │   ├── ManualCurrencyRate.java
│   │   ├── Session.java
│   │   ├── User.java
│   │   ├── UserPreferences.java
│   │   ├── UserSession.java
│   │   └── valueObject/
│   │       ├── DeviceLabel.java
│   │       ├── InactivityPolicy.java
│   │       ├── RefreshTokenClaims.java
│   │       ├── RefreshTokenId.java
│   │       ├── SessionId.java
│   │       └── UserId.java
│   ├── repository/
│   │   ├── ManualCurrencyRateRepository.java
│   │   ├── UserPreferencesRepository.java
│   │   ├── UserRepository.java
│   │   └── UserSessionRepository.java
│   └── usecase/
│       ├── AuthenticateUserUseCase.java
│       ├── DeleteManualCurrencyRateUseCase.java
│       ├── GetUserPreferencesUseCase.java
│       ├── ListManualCurrencyRatesUseCase.java
│       ├── ListUserSessionsUseCase.java
│       ├── RefreshSessionUseCase.java
│       ├── RegisterUserUseCase.java
│       ├── RevokeUserSessionUseCase.java
│       ├── SetManualCurrencyRateUseCase.java
│       ├── UpdateUserPasswordUseCase.java
│       ├── UpdateUserProfileUseCase.java
│       ├── UpdateUserPreferencesUseCase.java
│       └── command/
│           ├── AuthenticateUserCommand.java
│           ├── DeleteManualCurrencyRateCommand.java
│           ├── RefreshSessionCommand.java
│           ├── RegisterUserCommand.java
│           ├── SetManualCurrencyRateCommand.java
│           ├── UpdateUserPasswordCommand.java
│           ├── UpdateUserProfileCommand.java
│           └── UpdateUserPreferencesCommand.java
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
│   │   ├── mapper/UserRegisteredEventMapper.java
│   │   └── payload/
│   │       └── UserRegisteredData.java
│   └── persistence/
│       ├── entity/
│       │   ├── ManualCurrencyRateJpaEntity.java
│       │   ├── UserJpaEntity.java
│       │   ├── UserPreferencesJpaEntity.java
│       │   └── UserSessionJpaEntity.java
│       ├── jpa/
│       │   ├── ManualCurrencyRateJpaRepository.java
│       │   ├── UserJpaRepository.java
│       │   ├── UserPreferencesJpaRepository.java
│       │   └── UserSessionJpaRepository.java
│       ├── mapper/
│       │   ├── ManualCurrencyRatePersistenceMapper.java
│       │   ├── UserPersistenceMapper.java
│       │   ├── UserPreferencesPersistenceMapper.java
│       │   └── UserSessionPersistenceMapper.java
│       └── repository/
│           ├── ManualCurrencyRateRepositoryImpl.java
│           ├── UserRepositoryImpl.java
│           ├── UserPreferencesRepositoryImpl.java
│           └── UserSessionRepositoryImpl.java
└── web/
    ├── CookieService.java
    ├── controller/
    │   ├── AuthController.java
    │   ├── CurrencyRateController.java
    │   ├── PreferenceController.java
    │   ├── ProfileController.java
    │   └── SessionController.java
    ├── dto/
    │   ├── request/
    │   │   ├── LoginRequest.java
    │   │   ├── RegisterRequest.java
    │   │   ├── SetManualCurrencyRateRequest.java
    │   │   ├── UpdateUserPasswordRequest.java
    │   │   ├── UpdateUserProfileRequest.java
    │   │   └── UpdateUserPreferencesRequest.java
    │   └── response/
    │       ├── AuthResponse.java
    │       ├── ManualCurrencyRateResponse.java
    │       ├── SessionResponse.java
    │       ├── UserProfileResponse.java
    │       └── UserPreferencesResponse.java
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
| `INTERNAL_AUTH_TOKEN` | Shared secret for `X-Internal-Token` header |
| `COOKIE_SECURE` | Set `true` in production to add the `Secure` flag to all auth cookies |

---

## CI/CD

| Workflow | Trigger | Does |
|---|---|---|
| `ci.yml` | PRs; push to develop/master | tests + docker build via shared `backend-ci.yml` |
| `docker-publish.yml` | push to master; `v*` tags | GHCR publish: `latest`, `sha-*`, semver on tags |
| `release.yml` | manual (bump dropdown) | next `vX.Y.Z` tag + Release + versioned publish |

Reusable workflows live in the root repo `Sergio-Smirnoff/financial-app`.
