# Authentication API Contract

Base path: `/api/auth`

## POST `/login`

Authenticates an existing family account. Public endpoint.

Request:

```json
{
  "familySlug": "heins-family",
  "password": "correct-horse-battery-staple"
}
```

Success response: `200 OK`

```json
{
  "token": "<signed-jwt>"
}
```

Failures: `400 Bad Request` for validation errors, `401 Unauthorized` for invalid credentials,
and RFC 9457 `ProblemDetail` bodies.

## POST `/register`

Creates a family, its family account, and an initial token. Public endpoint.

Request:

```json
{
  "familySlug": "heins-family",
  "familyName": "Familie Heins",
  "password": "correct-horse-battery-staple"
}
```

Success response: `201 Created`

```json
{
  "token": "<signed-jwt>"
}
```

Failures: `400 Bad Request` for validation errors, `409 Conflict` for a duplicate family slug,
and RFC 9457 `ProblemDetail` bodies.

## Authenticated API Requests

Every non-public `/api/**` request sends:

```http
Authorization: Bearer <signed-jwt>
```

Missing, malformed, expired, or tampered tokens return `401 Unauthorized` with a RFC 9457
`ProblemDetail`. A valid token requesting another family's resource returns `403 Forbidden`
with a RFC 9457 `ProblemDetail`.
