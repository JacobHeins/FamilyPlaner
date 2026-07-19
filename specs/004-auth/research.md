# Research: Authentication and Authorization

## Decisions

### Authentication identity

- **Decision**: Retain the existing family-account model. Credentials are a unique
  `familySlug` and password for one `FamilyAccount` associated with one `Family`.
- **Rationale**: The current API, persistence schema, JWT subject, and security filter all
  already implement this model. It supports the requested family data isolation without a
  speculative migration to individual identities.
- **Alternatives considered**: Individual email-based user accounts linked to multiple
  `FamilyMember` records. Rejected for this feature because it requires a new data model,
  migration, endpoint contracts, and a changed authorization model.

### Backend integration baseline

- **Decision**: Reuse the existing `/api/auth/login` and `/api/auth/register` endpoints and
  JWT filter. Limit backend work to uniform RFC 9457 `ProblemDetail` responses for 403
  authorization failures and tests that cover the stated behavior.
- **Rationale**: Spring Security already permits auth endpoints, protects other endpoints,
  validates bearer JWTs, and uses BCrypt hashes and HS256 signatures.
- **Alternatives considered**: Rebuild the authentication backend. Rejected because it would
  duplicate working security infrastructure and expand feature risk without a product need.

### Authorization model

- **Decision**: Family membership is the authorization boundary. `FamilyRole` values remain
  metadata and do not grant different permissions in this feature.
- **Rationale**: The selected family-account identity maps to one family and existing endpoint
  checks enforce that family boundary. No role-to-action policy has been specified.
- **Alternatives considered**: Role-based permissions for DAD, MOM, and CHILD. Deferred until
  permissions and ownership rules are explicitly defined.

### Token lifecycle and browser storage

- **Decision**: Store the JWT in `localStorage`; attach it as `Authorization: Bearer <token>`;
  use the configured 24-hour expiry; clear it on logout and on authentication failure.
- **Rationale**: This matches the feature assumption and current `jwt.expiration-ms` setting
  while keeping the SPA integration small and stateless.
- **Alternatives considered**: Session storage, HTTP-only cookies, and refresh-token rotation.
  Deferred because they change persistence or require new server endpoints and CSRF policy.

### Error handling

- **Decision**: Render backend RFC 9457 `ProblemDetail` messages in German UI context and make
  backend 401 and 403 responses consistently use the existing `ProblemDetail` convention.
- **Rationale**: This is explicitly required and makes auth failures usable in the SPA.
- **Alternatives considered**: Empty status responses or client-generated generic errors.
  Rejected because they lose actionable API error detail and violate the feature requirement.
