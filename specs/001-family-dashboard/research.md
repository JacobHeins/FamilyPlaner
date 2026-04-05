# Research: Family Dashboard UI

## Decision 1: Keep the existing React SPA shell and evolve it instead of rebuilding the frontend

- **Decision**: Use the existing React 19 + Vite + React Router application in `ui/` as the implementation base and refactor current pages into a stronger feature-oriented SPA structure.
- **Rationale**: The application already has a usable shell, sidebar navigation, shared visual tokens, and route-level pages. Reusing that shell keeps the work within the constitution's dashboard SPA principle and avoids replacing working frontend infrastructure just to satisfy architectural preferences.
- **Alternatives considered**:
  - Rebuild the frontend from scratch in a new app: rejected because it would duplicate an existing SPA shell and create unnecessary migration risk.
  - Keep the current structure without architectural changes: rejected because the current pages duplicate fetching logic, rely on page-local state, and use sample data in critical views.

## Decision 2: Adopt Redux Toolkit with RTK Query for client state and backend data access

- **Decision**: Use Redux Toolkit as the global state foundation and RTK Query for server data fetching, caching, mutation handling, and invalidation.
- **Rationale**: The current implementation mixes direct `fetch` helpers, page-local effects, and local component state. RTK Query centralizes API behavior, request status, and cache invalidation, while Redux Toolkit provides a clear place for shared UI state such as selected week, sidebar behavior, and todo filters. This matches the user request directly and removes the current duplication.
- **Alternatives considered**:
  - React Context plus custom hooks: rejected because it would still require custom async status, caching, and invalidation logic across multiple pages.
  - TanStack Query plus a separate state library: rejected because the user explicitly requested Redux Toolkit for both global state and backend queries.

## Decision 2a: Bind the frontend contract strictly to the currently exposed backend API

- **Decision**: Limit frontend implementation to the exact operations exposed by the current backend controllers and DTOs.
- **Rationale**: The backend currently exposes these operations only:
  - `GET /api/families`
  - `POST /api/families`
  - `PUT /api/families`
  - `POST /api/families/members`
  - `GET /api/todos?familyId={id}&assigneeId={optional}`
  - `POST /api/todos`
  - `PUT /api/todos`
  - `DELETE /api/todos/{id}`
    Family renaming is available, while member-role changes are not. Planning unsupported role editing into the frontend would violate the frontend-only scope and create dead UI.
- **Alternatives considered**:
  - Add placeholder UI for unsupported operations: rejected because it creates misleading affordances.
  - Assume backend expansion later within this increment: rejected because the user explicitly asked to use only what is currently provided.

## Decision 3: Plan for a no-login single-household experience in this increment

- **Decision**: Treat the current application state as a no-login household dashboard with a single active family context and no authentication gate.
- **Rationale**: The user explicitly deferred login. Planning around a single active family keeps the UX simple and avoids baking incomplete auth assumptions into the route or data model. The store and route contracts can remain auth-ready later without blocking the current dashboard work.
- **Alternatives considered**:
  - Introduce placeholder auth or protected routes now: rejected because it adds scope without current user value.
  - Model multiple independently switchable households in this increment: rejected because the spec only requires one active family context for the dashboard experience.

## Decision 4: Preserve the current dark visual identity while improving hierarchy and ergonomics

- **Decision**: Keep the existing dark theme, elevated cards, accent-color system, and sidebar-based personality, while improving spacing, information hierarchy, small-screen navigation, and action placement. Frontend layout and component structure may be reorganized where that improves usability and responsiveness.
- **Rationale**: The current UI already expresses a recognizable visual direction through dark surfaces, colored status accents, and compact dashboard cards. A similar styling language satisfies the user's desire for continuity, while targeted ergonomic changes improve usability on tablets and phones.
- **Alternatives considered**:
  - Full visual redesign with a new theme system: rejected because it would discard the current app's recognizable look and inflate scope.
  - Keep the current appearance unchanged: rejected because the present layout is desktop-biased and several actions are embedded in page headers or inline forms in ways that do not scale well to smaller screens.

## Decision 5: Prioritize a focused ergonomics review based on the current implementation state

- **Decision**: Include the current implementation review as a first-class planning output and address the most visible UX/architecture issues during feature delivery.
- **Rationale**: The review surfaced several concrete issues that should guide the implementation order:
  - Dashboard and tasks pages contain hardcoded sample data instead of a shared data pipeline.
  - Data loading is repeated through page-local `useEffect` calls and direct API helpers.
  - The current sidebar and page header patterns are serviceable on desktop but need a mobile-friendly navigation and action strategy.
  - Inline forms and dense two-column sections work visually, but the layout needs stronger hierarchy and clearer empty/error/loading states.
  - Some current page and component boundaries should be treated as implementation details rather than fixed product constraints so the frontend can be restructured for better usability.
- **Alternatives considered**:
  - Defer the ergonomics review until after feature implementation: rejected because the user explicitly requested that the current state be reviewed and improved as part of this work.
  - Limit changes to cosmetics only: rejected because several look-and-feel problems are caused by structural state and layout decisions, not just colors or spacing.

## Decision 6: Defer activities and weekly planner behavior to a later increment

- **Decision**: Remove activity and planner-specific behavior from the current feature plan and focus this increment on todo-driven dashboard, family management, and detailed todo experiences.
- **Rationale**: The latest scope update explicitly states that activities are not implemented yet. Keeping them in the plan would force speculative UI contracts, placeholder data models, and ergonomics work for a feature area that the backend does not currently support.
- **Alternatives considered**:
  - Keep planner/activity placeholders in the implementation plan: rejected because it would create stale requirements and distract from the todo-focused deliverable.
  - Replace todo detail work with a planner page: rejected because it conflicts with the revised specification priority and current backend reality.

## Decision 7: Adjust family management to match current backend support

- **Decision**: Implement family management around viewing families, creating a family, renaming a family, and adding a member, while excluding member role editing from the current frontend.
- **Rationale**: The backend returns family names, members, and roles in `FamilyResponse`, and now exposes mutations for family creation, family rename, and member creation. There is still no controller operation for updating a member role.
- **Alternatives considered**:
  - Keep role edit actions disabled in the interface: rejected because disabled controls still imply support and add avoidable UX noise.
  - Simulate edits locally: rejected because it would diverge from persisted backend state.
