# Implementation Plan: Authentication and Authorization

**Branch**: `004-auth` | **Date**: 2026-07-18 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/004-auth/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/plan-template.md` for the execution workflow.

## Summary

Complete the family-account authentication experience using the existing Spring Security,
BCrypt, and HS256 JWT backend. The SPA will register and log in a family by `familySlug`,
persist the returned 24-hour JWT in `localStorage`, attach it to API requests, protect
authenticated routes, and provide logout. Backend follow-up is limited to making every
authorization denial return RFC 9457 `ProblemDetail` consistently. This plan resolves the
draft spec's email/per-user language in favor of the confirmed existing family-account
contract; individual user accounts are out of scope for this feature.

## Technical Context

<!--
  ACTION REQUIRED: Replace the content in this section with the technical details
  for the project. The structure here is presented in advisory capacity to guide
  the iteration process.
-->

**Language/Version**: Java 25; TypeScript 5.7; React 19  
**Primary Dependencies**: Spring Boot 4.0.4, Spring Security, JJWT 0.12.6, React Router 7, Redux Toolkit RTK Query 2.6  
**Storage**: PostgreSQL with Flyway; `localStorage` for the browser JWT  
**Testing**: JUnit, Spring Security Test, Maven; TypeScript compiler and Vite build  
**Target Platform**: Spring Boot web service and modern browser SPA
**Project Type**: Web application (Spring Boot API plus React dashboard SPA)  
**Performance Goals**: Reject unauthenticated API requests in under 100 ms; complete registration/login in under 2 seconds under normal load  
**Constraints**: Stateless HS256 JWT; 24-hour configurable expiry; bearer token sent on all authenticated API requests; German UI text; RFC 9457 errors  
**Scale/Scope**: One family account authenticates one family; membership grants equal resource access; no individual accounts, refresh tokens, OAuth/SSO, or role-specific permissions

## Constitution Check

_GATE: Must pass before Phase 0 research. Re-check after Phase 1 design._

- PASS: The spec explicitly authorizes backend security work. Backend changes are limited to
  existing security/error behavior; all new user journeys are in `ui/`.
- PASS: The solution extends the existing React + TypeScript + Vite dashboard SPA.
- PASS: Existing `/api/auth/login` and `/api/auth/register` contracts are reused. The resolved
  identity contract is `familySlug`, not email.
- PASS: Login and registration specify loading, validation, success, and RFC 9457 error states;
  protected-route redirection and expired-token logout are defined.
- PASS: All new user-facing UI strings and accessible labels will be German.
- PASS: Verification includes `npm run build` in `ui/`, focused backend auth/security tests,
  and request-level checks for 401, 403, and valid bearer authentication.

## Project Structure

### Documentation (this feature)

```text
specs/[###-feature]/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (/speckit.plan command)
├── data-model.md        # Phase 1 output (/speckit.plan command)
├── quickstart.md        # Phase 1 output (/speckit.plan command)
├── contracts/           # Phase 1 output (/speckit.plan command)
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)

```text
src/
├── main/
│   ├── java/com/heins/familyplanner/
│   └── resources/
└── test/
    └── java/com/heins/familyplanner/

ui/
├── src/
│   ├── api/
│   ├── components/
│   ├── context/
│   └── pages/
├── index.html
├── package.json
└── tsconfig.json
```

**Structure Decision**: Use the existing monorepo layout: Spring Boot owns JWT issuance,
validation, and authorization in `src/`; the React SPA owns credential forms, auth state,
route protection, request token injection, and logout in `ui/`. The spec authorizes the
small backend consistency work described above.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

| Violation                  | Why Needed         | Simpler Alternative Rejected Because |
| -------------------------- | ------------------ | ------------------------------------ |
| [e.g., 4th project]        | [current need]     | [why 3 projects insufficient]        |
| [e.g., Repository pattern] | [specific problem] | [why direct DB access insufficient]  |
