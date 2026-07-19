# Feature Specification: Authentication and Authorization

**Feature Branch**: `004-auth`  
**Created**: 2026-04-05  
**Status**: Draft  
**Input**: User description: "Add authentication and authorization"

## Scope Boundaries _(mandatory)_

- **In Scope**: Backend security layer — user registration, login, JWT issuance and validation, per-endpoint authorization; frontend login/register pages and authenticated API client
- **Out of Scope**: OAuth2 / SSO providers, multi-tenancy, advanced role management beyond the existing `FamilyRole` hierarchy
- **Backend Impact**: Explicitly authorized — user requests backend setup for auth

## User Scenarios & Testing _(mandatory)_

### User Story 1 - Register and Log In (Priority: P1)

A new user creates an account with an email address and password, then logs in to receive a token that grants access to protected API endpoints.

**Why this priority**: Without identity, no other auth behaviour is possible. All other stories depend on a valid token.

**Independent Test**: Register a new account via `POST /api/auth/register`, log in via `POST /api/auth/login`, use the returned token on a protected endpoint and confirm 200 is returned.

**Acceptance Scenarios**:

1. **Given** no account exists, **When** a user submits a valid email and password, **Then** an account is created and a JWT is returned
2. **Given** an account exists, **When** a user submits correct credentials, **Then** a valid JWT is returned
3. **Given** an account exists, **When** a user submits wrong credentials, **Then** a 401 response is returned
4. **Given** a duplicate email, **When** a user attempts to register, **Then** a 409 Conflict response is returned

---

### User Story 2 - Access Protected Resources (Priority: P1)

An authenticated user includes their JWT in API requests and receives the data they are authorised to see. Unauthenticated requests are rejected.

**Why this priority**: This is the core value of authorization — without it all data is public.

**Independent Test**: Call `GET /api/todos?familyId=1` without a token → 401; call with a valid token → 200.

**Acceptance Scenarios**:

1. **Given** a valid JWT, **When** a protected endpoint is called, **Then** the response is 200/201/204 as expected
2. **Given** no token, **When** a protected endpoint is called, **Then** a 401 response is returned
3. **Given** an expired or tampered token, **When** a protected endpoint is called, **Then** a 401 response is returned

---

### User Story 3 - Family Membership Authorization (Priority: P2)

A user can only access data belonging to families they are a member of. Requests for other families' data are rejected.

**Why this priority**: Data isolation is critical once multiple families use the system.

**Independent Test**: Authenticate as a member of family 1, request data for family 2 → 403.

**Acceptance Scenarios**:

1. **Given** a user is a member of family 1, **When** they request family 1 data, **Then** a 200 response is returned
2. **Given** a user is a member of family 1, **When** they request family 2 data, **Then** a 403 response is returned

---

### Edge Cases

- What happens when a JWT token is missing the `Bearer ` prefix?
- How does the system handle tokens issued before a password change?
- What happens if the signing secret changes on server restart?

## Requirements _(mandatory)_

### Functional Requirements

- **FR-001**: System MUST allow users to register with a unique email address and a password
- **FR-002**: System MUST hash passwords before storage — plaintext passwords MUST never be persisted
- **FR-003**: System MUST validate credentials on login and return a signed JWT on success
- **FR-004**: System MUST reject requests to protected endpoints that do not carry a valid JWT with 401
- **FR-005**: System MUST reject requests to family-scoped resources from users not belonging to that family with 403
- **FR-006**: All existing endpoints (`/api/todos`, `/api/activities`, `/api/family`) MUST require authentication
- **FR-007**: Auth endpoints (`/api/auth/register`, `/api/auth/login`) MUST be publicly accessible
- **FR-008**: JWT MUST carry the user's identity and family membership(s) as claims
- **FR-009**: JWT expiry MUST be configurable via application properties
- **FR-010**: System MUST return RFC 9457 `ProblemDetail` error responses for 401 and 403, consistent with existing error format
- **FR-011**: Frontend MUST store the JWT and include it in all API requests
- **FR-012**: Frontend MUST redirect unauthenticated users to the login page
- **FR-013**: Frontend MUST provide a logout action that clears the stored token

### Key Entities

- **User**: Owns credentials (email, hashed password); linked to one or more `FamilyMember` records
- **JWT Claim**: Carries `sub` (userId), `familyIds` (list), `roles`, `exp`

## Success Criteria _(mandatory)_

### Measurable Outcomes

- **SC-001**: Unauthenticated requests to all protected endpoints return 401 in under 100 ms
- **SC-002**: Registration and login round-trip completes in under 2 seconds under normal load
- **SC-003**: A user from family A cannot read or write data belonging to family B
- **SC-004**: Token expiry and rejection work correctly — expired tokens are never accepted

## Assumptions

- The existing `FamilyMember` entity will be linked to the new `User` entity; no breaking schema changes to existing tables
- JWT-based stateless authentication is preferred over server-side sessions
- A single signing secret (symmetric HS256) is sufficient for the current scale
- Frontend stores JWT in `localStorage` (can be revisited for security hardening in a later feature)
- Password requirements: minimum 8 characters (additional rules as decided during planning)
- The `FamilyPlanerApplicationTests` smoke test uses H2; the auth schema will be added via a new Flyway migration (V3)
