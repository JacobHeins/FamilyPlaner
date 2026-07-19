# Tasks: Authentication and Authorization

**Input**: Design documents from `/specs/004-auth/`
**Prerequisites**: [plan.md](./plan.md), [spec.md](./spec.md), [research.md](./research.md), [data-model.md](./data-model.md), [contracts/auth-api.md](./contracts/auth-api.md), [quickstart.md](./quickstart.md)

**Tests**: Backend auth, security, and authorization tests are included because the feature
specification has explicit API acceptance scenarios for registration, authentication, invalid
tokens, and cross-family access. Frontend verification is performed through TypeScript/Vite
build and the quickstart browser flow because this UI project has no configured test runner.

**Scope**: The feature explicitly authorizes targeted backend security work. Authentication
identity is an existing family account using `familySlug` and password, not an individual
email-based user account. All new SPA text and accessible labels must be German.

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Confirm existing auth foundations and identify the exact frontend integration
surface before implementation.

- [x] T001 Review the resolved family-account identity and API payloads in `specs/004-auth/contracts/auth-api.md`
- [x] T002 Review the existing JWT filter, security rules, and configurable expiry in `src/main/java/com/heins/familyplanner/security/JwtAuthFilter.java`, `src/main/java/com/heins/familyplanner/security/JwtService.java`, `src/main/java/com/heins/familyplanner/configuration/SecurityConfig.java`, and `src/main/resources/application-dev.properties`
- [x] T003 [P] Review the SPA routing, Redux store, and RTK Query integration points in `ui/src/App.tsx`, `ui/src/app/store/store.ts`, and `ui/src/api/baseApi.ts`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Establish the shared browser authentication state, bearer-token transport, and
route boundary required by every authenticated SPA journey.

**⚠️ CRITICAL**: Complete this phase before implementing user-story pages.

- [x] T004 [P] Define family-account auth request, response, and RFC 9457 problem types in `ui/src/app/store/types.ts`
- [x] T005 Create persistent token state, initialization from `localStorage`, and clear-token actions in `ui/src/app/store/authSlice.ts`
- [x] T006 Register the auth reducer in `ui/src/app/store/store.ts`
- [x] T007 Configure `prepareHeaders` to attach the persisted JWT as `Authorization: Bearer <token>` in `ui/src/api/baseApi.ts`
- [x] T008 Create an authenticated route outlet that redirects unauthenticated visitors to `/login` in `ui/src/components/ProtectedRoute.tsx`
- [x] T009 Add public login/register routes and nest existing dashboard routes behind the protected outlet in `ui/src/App.tsx`

**Checkpoint**: Shared auth state, bearer transport, and protected routing are ready for
story-specific pages.

---

## Phase 3: User Story 1 - Register and Log In (Priority: P1) 🎯 MVP

**Goal**: A household can register or sign in with its family slug and password, receive a JWT,
and enter the protected dashboard.

**Independent Test**: Register through `POST /api/auth/register`, log in through
`POST /api/auth/login`, confirm each response returns a token, and use that token to access a
protected endpoint. In the SPA, submit valid credentials and confirm navigation to the
dashboard and token persistence after reload.

### Tests for User Story 1

- [x] T010 [P] [US1] Extend registration and login success, invalid-credential 401, duplicate-slug 409, and RFC 9457 response assertions in `src/test/java/com/heins/familyplanner/auth/AuthControllerTest.java`
- [x] T011 [P] [US1] Extend password-hashing, duplicate-account, and JWT-issuance service coverage in `src/test/java/com/heins/familyplanner/auth/AuthServiceTest.java`

### Implementation for User Story 1

- [x] T012 [P] [US1] Add RTK Query login and register mutations matching the approved family-slug contract in `ui/src/api/authApi.ts`
- [x] T013 [P] [US1] Build the German login form with family-slug and password validation, loading, 401 error, and success handling in `ui/src/pages/Login.tsx` and `ui/src/pages/Login.css`
- [x] T014 [P] [US1] Build the German registration form with family name, family slug, password validation, loading, 409 error, and success handling in `ui/src/pages/Register.tsx` and `ui/src/pages/Register.css`
- [x] T015 [US1] Dispatch the persistent token action and navigate to the dashboard after successful login or registration in `ui/src/pages/Login.tsx` and `ui/src/pages/Register.tsx`
- [x] T016 [US1] Add login/register page imports and auth-page layout rules without disturbing dashboard styles in `ui/src/App.tsx` and `ui/src/App.css`
- [x] T017 [US1] Run the focused auth tests from `src/test/java/com/heins/familyplanner/auth/AuthControllerTest.java` and `src/test/java/com/heins/familyplanner/auth/AuthServiceTest.java`, then verify the registration/login browser flow in `specs/004-auth/quickstart.md`

**Checkpoint**: User Story 1 is independently functional: a family account can register or
sign in and reach the dashboard with a persisted token.

---

## Phase 4: User Story 2 - Access Protected Resources (Priority: P1)

**Goal**: Only valid bearer-token requests may access existing API resources; the SPA routes
unauthenticated visitors to login, clears invalid sessions, and lets authenticated requests
continue normally.

**Independent Test**: Request `GET /api/todos` without a token and receive 401; repeat with a
valid token and receive 200. In the SPA, visit a protected route without a token and confirm
redirect to `/login`; make an API request with an invalid token and confirm it returns to login.

### Tests for User Story 2

- [x] T018 [P] [US2] Add missing-Bearer-prefix, expired-token, tampered-token, and valid-token assertions in `src/test/java/com/heins/familyplanner/security/JwtServiceTest.java`
- [x] T019 [P] [US2] Add protected todo request tests for missing and valid bearer tokens in `src/test/java/com/heins/familyplanner/todos/TodoControllerTest.java`

### Implementation for User Story 2

- [x] T020 [US2] Add a shared RTK Query authentication-error handler that clears stored auth state on 401 responses in `ui/src/api/baseApi.ts` and `ui/src/app/store/authSlice.ts`
- [x] T021 [US2] Redirect after a cleared 401 session and preserve the intended protected destination in `ui/src/components/ProtectedRoute.tsx` and `ui/src/App.tsx`
- [x] T022 [US2] Add a German expired-or-invalid-session error presentation to the login flow in `ui/src/pages/Login.tsx` and `ui/src/pages/Login.css`
- [x] T023 [US2] Add a German logout command that clears the local token and returns to login in `ui/src/components/Layout.tsx`
- [x] T024 [US2] Run JWT and protected-controller tests in `src/test/java/com/heins/familyplanner/security/JwtServiceTest.java` and `src/test/java/com/heins/familyplanner/todos/TodoControllerTest.java`, then verify the no-token, invalid-token, reload, and logout flows in `specs/004-auth/quickstart.md`

**Checkpoint**: User Story 2 is independently functional: requests require valid JWTs, the
SPA injects them automatically, and invalid or ended sessions return users to login.

---

## Phase 5: User Story 3 - Family Membership Authorization (Priority: P2)

**Goal**: A valid family account may access only its own family resources, and every
cross-family denial returns a RFC 9457 `ProblemDetail` response with status 403.

**Independent Test**: Authenticate as a family 1 account and request or mutate family 2 data;
confirm 403 with a RFC 9457 `ProblemDetail`. Confirm equivalent family 1 requests still return
their expected success statuses.

### Tests for User Story 3

- [x] T025 [P] [US3] Add `FORBIDDEN` Result-to-RFC-9457 mapping assertions in `src/test/java/com/heins/familyplanner/exceptions/ResultTest.java`
- [x] T026 [P] [US3] Assert foreign-family todo read and create requests return a 403 `ProblemDetail` body in `src/test/java/com/heins/familyplanner/todos/TodoControllerTest.java`
- [x] T027 [P] [US3] Add same-family success and foreign-family 403 `ProblemDetail` coverage in `src/test/java/com/heins/familyplanner/activities/ActivitiesControllerTest.java` and `src/test/java/com/heins/familyplanner/family/FamilyControllerTest.java`

### Implementation for User Story 3

- [x] T028 [US3] Add the `FORBIDDEN` error type, 403 status mapping, and `Result.forbidden` factory in `src/main/java/com/heins/familyplanner/exceptions/Result.java`
- [x] T029 [US3] Replace bare cross-family todo responses with `Result.forbidden(...).toProblemDetail()` responses in `src/main/java/com/heins/familyplanner/todos/TodoController.java`
- [x] T030 [US3] Replace bare cross-family activity responses with `Result.forbidden(...).toProblemDetail()` responses in `src/main/java/com/heins/familyplanner/activities/ActivitiesController.java`
- [x] T031 [US3] Replace bare cross-family family-resource responses with `Result.forbidden(...).toProblemDetail()` responses in `src/main/java/com/heins/familyplanner/family/FamilyController.java`
- [x] T032 [US3] Render the RFC 9457 403 detail as a German permission error without clearing a valid session in `ui/src/api/baseApi.ts` and `ui/src/App.tsx`
- [x] T033 [US3] Run the cross-family controller and Result tests in `src/test/java/com/heins/familyplanner/exceptions/ResultTest.java`, `src/test/java/com/heins/familyplanner/todos/TodoControllerTest.java`, `src/test/java/com/heins/familyplanner/activities/ActivitiesControllerTest.java`, and `src/test/java/com/heins/familyplanner/family/FamilyControllerTest.java`

**Checkpoint**: User Story 3 is independently functional: a family account cannot read or
write another family’s data, and all 403 responses follow the documented API error contract.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Confirm the full auth journey is production-consistent, accessible, and buildable.

- [x] T034 [P] Verify a stable production JWT secret and configurable expiry are documented in `src/main/resources/application-prod.properties` and `src/main/resources/application-dev.properties`
- [x] T035 [P] Review all new auth-form labels, buttons, validation, error states, and logout controls for German language and keyboard accessibility in `ui/src/pages/Login.tsx`, `ui/src/pages/Register.tsx`, and `ui/src/components/Layout.tsx`
- [x] T036 Run `./mvnw test -Dtest=AuthControllerTest,AuthServiceTest,JwtServiceTest,TodoControllerTest,ActivitiesControllerTest,FamilyControllerTest,ResultTest` using `pom.xml`
- [x] T037 Run `npm run build` from `ui/`
- [x] T038 Execute the complete backend and SPA verification sequence in `specs/004-auth/quickstart.md`

---

## Dependencies & Execution Order

### Phase Dependencies

- **Phase 1: Setup**: No dependencies; establishes verified ownership points.
- **Phase 2: Foundational**: Depends on Phase 1 and blocks all SPA user-story work.
- **Phase 3: US1**: Depends on Phase 2; establishes account entry and persisted authentication.
- **Phase 4: US2**: Depends on Phase 2 and integrates with US1’s credentials pages for recovery
  messaging; it may begin once shared auth state is ready.
- **Phase 5: US3**: Depends on the existing backend security baseline and can begin after Phase
  2; validate it after US2 because it uses the same bearer-token flow.
- **Phase 6: Polish**: Depends on all selected user stories.

### User Story Completion Order

```text
Setup -> Foundational -> US1 (MVP) -> US2 -> US3 -> Polish
                         \--------------> US3 backend tests can run in parallel with US2
```

### Within Each User Story

- Add or extend tests before changing backend behavior.
- Create API/state contracts before wiring page interaction.
- Complete all German loading, validation, success, and error states before declaring the story
  done.
- Run the story’s focused verification at its checkpoint.

## Parallel Execution Examples

### User Story 1

```text
Task: T010 Extend controller auth contract tests in src/test/java/com/heins/familyplanner/auth/AuthControllerTest.java
Task: T011 Extend service auth tests in src/test/java/com/heins/familyplanner/auth/AuthServiceTest.java
Task: T012 Create RTK Query auth API in ui/src/api/authApi.ts
Task: T013 Create login page and styles in ui/src/pages/Login.tsx and ui/src/pages/Login.css
Task: T014 Create registration page and styles in ui/src/pages/Register.tsx and ui/src/pages/Register.css
```

### User Story 2

```text
Task: T018 Extend JWT validation tests in src/test/java/com/heins/familyplanner/security/JwtServiceTest.java
Task: T019 Add protected todo request tests in src/test/java/com/heins/familyplanner/todos/TodoControllerTest.java
```

### User Story 3

```text
Task: T025 Add Result forbidden mapping tests in src/test/java/com/heins/familyplanner/exceptions/ResultTest.java
Task: T026 Add todo cross-family authorization tests in src/test/java/com/heins/familyplanner/todos/TodoControllerTest.java
Task: T027 Add activity and family cross-family authorization tests in src/test/java/com/heins/familyplanner/activities/ActivitiesControllerTest.java and src/test/java/com/heins/familyplanner/family/FamilyControllerTest.java
```

## Implementation Strategy

### MVP First

1. Complete Setup and Foundational phases.
2. Complete User Story 1 and its focused tests.
3. Demonstrate a household registering or logging in and reaching the dashboard with a
   persisted token.

### Incremental Delivery

1. Deliver US1 for account entry and JWT persistence.
2. Add US2 to secure all browser navigation and request transport.
3. Add US3 to make cross-family authorization errors consistent and observable.
4. Finish with full backend tests, UI build, and the quickstart sequence.

## Format Validation

All 38 tasks use the required checklist format: checkbox, sequential task ID, `[P]` only for
independent work, `[US#]` for every user-story task, and one or more explicit file paths.
