# ms-users — API

Endpoints and error codes. Envelope shape: parent `.ai/references/APP_STRUCTURE.md` — not repeated here.

## Endpoint Ownership Split

- **Auth endpoints (`/api/v1/auth/**`)**: Owned by ms-users, called directly by browser through gateway, **JWT-exempt**.
- **User profile & settings (`/api/v1/users/me/**`)**: Require `X-User-Id` header injected by gateway after JWT verification.

## Endpoints

| Method | Path | Purpose | Error codes |
|---|---|---|---|
| POST | `/api/v1/auth/register` | Register new user account & issue session cookies | `duplicate_email`, `weak_password` |
| POST | `/api/v1/auth/login` | Authenticate user & issue session cookies | `invalid_credentials` |
| POST | `/api/v1/auth/refresh` | Rotate refresh token & reissue access cookies | `invalid_token`, `session_expired`, `session_not_found` |
| POST | `/api/v1/auth/logout` | Revoke session & clear auth cookies | — |
| GET | `/api/v1/users/me/sessions` | List active sessions for authenticated user | — |
| DELETE | `/api/v1/users/me/sessions/{id}` | Revoke specific session by ID | `session_not_found` |
| GET | `/api/v1/users/me/preferences` | Get user preferences (idle limits, currency, formatting) | `user_not_found` |
| PUT | `/api/v1/users/me/preferences` | Update user preferences | `user_not_found` |
| GET | `/api/v1/users/me/currency-rates` | List custom manual FX rates | — |
| PUT | `/api/v1/users/me/currency-rates/{currency}` | Set/upsert manual FX rate for currency | `invalid_currency` |
| DELETE | `/api/v1/users/me/currency-rates/{currency}` | Delete manual FX rate override | — |
| PUT | `/api/v1/users/me/profile` | Update user profile (firstName, lastName) | `user_not_found` |
| PUT | `/api/v1/users/me/password` | Change password & revoke other active sessions | `wrong_current_password`, `weak_password` |

## DomainError catalog

| Slug | HTTP status | When it is thrown |
|---|---|---|
| `invalid_credentials` | 401 | Email not found or password hash mismatch |
| `invalid_token` | 401 | Refresh token signature invalid or claims malformed |
| `session_expired` | 401 | Idle duration exceeded user's `maxIdleMinutes` |
| `session_not_found` | 404 | Session lookup by `jti` or ID returned no match |
| `user_not_found` | 404 | User ID lookup returned no record |
| `duplicate_email` | 409 | Registration attempted with an already registered email |
| `weak_password` | 400 | New password fails complexity criteria (< 8 chars) |
| `wrong_current_password` | 400 | Password change supplied incorrect current password |
| `invalid_currency` | 400 | Currency code invalid or attempted for ARS/USD |
| `internal_error` | 500 | Unmapped failure |
