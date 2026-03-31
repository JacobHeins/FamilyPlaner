# Implementation Plan: Family Dashboard UI

**Branch**: `001-family-dashboard` | **Date**: 2026-03-31 | **Spec**: `/workspace/specs/001-family-dashboard/spec.md`
**Input**: Feature specification from `/workspace/specs/001-family-dashboard/spec.md` plus planning constraints: React SPA, no login for now, Redux Toolkit for global state and backend queries, and a review of the current UI look and feel with ergonomic improvements while preserving the existing visual language.

## Summary

Deliver a responsive family dashboard SPA in the existing `ui/` application by consolidating dashboard, family management, and detailed todo flows into a Redux Toolkit based frontend architecture. The design preserves the current dark card-based styling and navigation personality, while allowing frontend restructuring where needed for better usability and responsive behavior, and replacing duplicated local state, hardcoded sample data, and page-level fetch logic with RTK Query backed data access, strictly limited to the currently exposed family and todo API operations.

## Technical Context

**Language/Version**: TypeScript 5.x with React 19  
**Primary Dependencies**: React Router 7, Redux Toolkit, RTK Query, lucide-react, Vite 6  
**Storage**: Existing Spring Boot backend APIs for server data plus in-memory Redux store and RTK Query cache on the client  
**Testing**: `npm run build` for type/build verification, manual responsive QA across dashboard/family/todo flows, and targeted UI tests if added during implementation  
**Target Platform**: Modern desktop and mobile browsers for a single-page web application
**Project Type**: Frontend web application integrated into an existing monorepo  
**Performance Goals**: Primary routes render navigable shell content immediately and show meaningful dashboard content or explicit state feedback within 2 seconds on a normal broadband connection  
**Constraints**: Frontend-only scope in `ui/`; no login flow in this feature; preserve a similar dark visual identity; avoid speculative backend changes; support desktop, tablet, and smartphone layouts; use only the currently exposed family and todo endpoints; frontend restructuring is allowed when it materially improves usability or responsive behavior without changing the visual identity  
**Scale/Scope**: Three main user-facing surfaces in this increment: dashboard, family management, and detailed todos

## Constitution Check

_GATE: Must pass before Phase 0 research. Re-check after Phase 1 design._

### Pre-Research Gate

- **Frontend-only scope**: PASS. All planned implementation work is constrained to `ui/`, with backend dependencies documented instead of modified.
- **Dashboard SPA architecture**: PASS. The feature remains within the existing React + TypeScript + Vite single-page application and extends the current route structure.
- **Backend contract fidelity**: PASS. The current backend exposes list/create/update family, add family member, and list/create/update/delete todo operations with optional assignee filtering. Unsupported operations such as member role changes are removed from implementation scope.
- **Type-safe UX quality gates**: PASS. The design includes explicit loading, empty, success, and error states for dashboard, family management, and detailed todo flows.
- **Verification path**: PASS. Validation will include `npm run build` in `ui/` and route-level manual QA across responsive breakpoints.

### Post-Design Gate

- **Frontend-only scope**: PASS. Proposed structure adds Redux Toolkit, RTK Query, and feature-specific UI modules only under `ui/`.
- **Dashboard SPA architecture**: PASS. The plan keeps a single shell, shared navigation, and route-driven feature surfaces for dashboard, family management, and todos.
- **Backend contract fidelity**: PASS. Contracts document only the currently exposed server operations and treat anything else as out of scope for this increment.
- **Type-safe UX quality gates**: PASS. Data models, route contracts, and quickstart checks include query-state handling and invalid-filter recovery.
- **Verification path**: PASS. Quickstart includes install, run, build, and responsive review steps.

## Project Structure

### Documentation (this feature)

```text
/workspace/specs/001-family-dashboard/
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
├── contracts/
│   └── dashboard-spa-contract.md
└── tasks.md
```

### Source Code (repository root)

```text
/workspace/src/
├── main/
│   ├── java/com/heins/familyplaner/
│   └── resources/
└── test/
    └── java/com/heins/familyplaner/

/workspace/ui/
├── src/
│   ├── app/
│   │   ├── router/
│   │   └── store/
│   ├── api/
│   ├── components/
│   ├── features/
│   │   ├── dashboard/
│   │   ├── family/
│   │   └── todos/
│   ├── pages/
│   └── styles/
├── package.json
└── tsconfig.json
```

**Structure Decision**: Keep the current monorepo structure with the Java backend in `/workspace/src/` and all implementation work in `/workspace/ui/`. Introduce an `app/store` entry point for Redux Toolkit, feature-focused modules for dashboard, family, and todos, and retain shared components/styles so the UI redesign stays cohesive instead of page-fragmented. Within `ui/`, route structure, page composition, and component boundaries may be reorganized if that produces better usability and responsiveness while preserving the current styling direction. Family management implementation is limited to listing families, creating a family, renaming a family, and adding members because those are the exposed family mutations.

## Complexity Tracking

No constitution violations or justified exceptions are required for this plan.
