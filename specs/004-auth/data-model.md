# Data Model: Authentication and Authorization

## FamilyAccount

The existing credential-owning entity. It represents a single family login, rather than an
individual user account.

| Field                 | Type                | Rules                                                         |
| --------------------- | ------------------- | ------------------------------------------------------------- |
| `id`                  | database identifier | Primary key                                                   |
| `publicSlug`          | string              | Required, unique, 3-50 characters; used as the login identity |
| `passwordHash`        | string              | Required BCrypt hash; plaintext is never stored               |
| `family`              | `Family`            | Required one-to-one relationship                              |
| `lastLoginAt`         | timestamp           | Updated after successful login                                |
| `failedLoginAttempts` | integer             | Tracks failed credentials                                     |
| `locked`              | boolean             | Prevents login when true                                      |

## Family

The data-isolation boundary. A `FamilyAccount` owns exactly one `Family`; all protected
resources must belong to the family in the authenticated account.

## FamilyMember

Existing member record associated with a `Family` and a `FamilyRole` (`DAD`, `MOM`, or
`CHILD`). Roles do not alter authorization in this feature.

## JWT Claims

| Claim      | Value                        | Rules                                                               |
| ---------- | ---------------------------- | ------------------------------------------------------------------- |
| `sub`      | `FamilyAccount.publicSlug`   | Identifies the authenticated family account                         |
| `familyId` | associated family identifier | Used to authorize family-scoped resources                           |
| `iat`      | issue timestamp              | Created when the token is issued                                    |
| `exp`      | expiry timestamp             | Calculated from configurable `jwt.expiration-ms`, normally 24 hours |

## State Transitions

```text
Unregistered family --register--> Active account --login--> JWT issued
Active account --wrong credentials--> Active account (failed attempt recorded)
JWT issued --expiry/logout/invalid token--> Unauthenticated browser session
```

## Browser Auth State

| Field              | Storage             | Rules                                                           |
| ------------------ | ------------------- | --------------------------------------------------------------- |
| `token`            | `localStorage`      | JWT string; present only for an authenticated browser session   |
| derived auth state | React state/context | Derived from a non-empty stored token; cleared on logout or 401 |
