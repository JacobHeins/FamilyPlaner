---
description: "Task list for Family Dashboard UI implementation"
---

# Tasks: Family Dashboard UI

**Input**: Design documents from `/workspace/specs/001-family-dashboard/`
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/dashboard-spa-contract.md, quickstart.md

**Tests**: No explicit automated test work was requested in the feature specification. Validation tasks below use `npm run build` and quickstart-driven manual QA.

**Organization**: Tasks are grouped by user story to enable independent implementation and validation of each story.

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Prepare the frontend workspace for Redux Toolkit based implementation and route restructuring.

- [x] T001 Add Redux Toolkit and React Redux dependencies in `ui/package.json`
- [x] T002 Create application store scaffolding in `ui/src/app/store/store.ts` and `ui/src/app/store/hooks.ts`
- [x] T003 [P] Wrap the SPA with the Redux provider in `ui/src/main.tsx`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Establish shared API, routing, and shell foundations required by all user stories.

**⚠️ CRITICAL**: No user story work should begin until this phase is complete.

- [ ] T004 Create a shared RTK Query base API in `ui/src/api/baseApi.ts`
- [ ] T005 [P] Refactor family endpoints to match the current backend contract in `ui/src/api/familyApi.ts`
- [ ] T006 [P] Create todo RTK Query endpoints aligned to the current backend contract in `ui/src/api/todoApi.ts`
- [ ] T007 Define shared frontend types and selectors for family and todo data in `ui/src/app/store/types.ts`
- [ ] T008 Update route composition for the dashboard, family, and todo surfaces in `ui/src/App.tsx`
- [ ] T009 [P] Restructure the shared shell for responsive navigation in `ui/src/components/Layout.tsx`, `ui/src/components/Sidebar.tsx`, `ui/src/components/Layout.css`, and `ui/src/components/Sidebar.css`
- [ ] T010 Establish shared loading, empty, and error-state styling tokens in `ui/src/index.css` and `ui/src/App.css`

**Checkpoint**: Foundation ready. User stories can now be implemented independently.

---

## Phase 3: User Story 1 - Review the Week at a Glance (Priority: P1) 🎯 MVP

**Goal**: Deliver a responsive dashboard that summarizes the family and current-week todos using live backend data.

**Independent Test**: Open the dashboard on desktop and mobile widths and verify that family summary data and current-week todo information load correctly, with clear empty and error states when data is missing or unavailable.

### Implementation for User Story 1

- [ ] T011 [P] [US1] Restructure the dashboard page layout for summary and weekly todo sections in `ui/src/pages/Dashboard.tsx`
- [ ] T012 [P] [US1] Redesign the dashboard styling for responsive cards and weekly todo presentation in `ui/src/pages/Dashboard.css`
- [ ] T013 [US1] Wire family and todo queries into the dashboard in `ui/src/pages/Dashboard.tsx`
- [ ] T014 [US1] Implement current-week todo grouping and summary derivation in `ui/src/pages/Dashboard.tsx`
- [ ] T015 [US1] Add loading, empty, and error states for the dashboard in `ui/src/pages/Dashboard.tsx` and `ui/src/pages/Dashboard.css`
- [ ] T016 [US1] Align dashboard navigation entry points with the new layout in `ui/src/components/Sidebar.tsx` and `ui/src/pages/Dashboard.tsx`
- [ ] T017 [US1] Validate the dashboard MVP with `npm run build` in `ui/package.json`

**Checkpoint**: User Story 1 is complete when the dashboard alone provides a usable weekly overview.

---

## Phase 4: User Story 2 - Manage Family Details (Priority: P2)

**Goal**: Provide an API-backed family management page for viewing families, renaming families, creating families, and adding members.

**Independent Test**: Open the family page, verify existing families and members render, rename a family, create a new family if needed, and add a member using only the supported backend routes.

### Implementation for User Story 2

- [ ] T018 [P] [US2] Restructure the family management page for viewing, renaming, and member creation workflows in `ui/src/pages/FamilyMembers.tsx`
- [ ] T019 [P] [US2] Update family management styling to preserve the current visual language while improving responsiveness in `ui/src/pages/FamilyMembers.css`
- [ ] T020 [US2] Implement family create and rename mutations with the current backend request shapes in `ui/src/api/familyApi.ts` and `ui/src/pages/FamilyMembers.tsx`
- [ ] T021 [US2] Implement add-member flow with backend-backed role selection in `ui/src/api/familyApi.ts` and `ui/src/pages/FamilyMembers.tsx`
- [ ] T022 [US2] Present member roles as view-only data and remove unsupported role-edit affordances in `ui/src/pages/FamilyMembers.tsx`
- [ ] T023 [US2] Add loading, validation, empty, and error handling for family workflows in `ui/src/pages/FamilyMembers.tsx` and `ui/src/pages/FamilyMembers.css`
- [ ] T024 [US2] Validate family management workflows with `npm run build` in `ui/package.json`

**Checkpoint**: User Stories 1 and 2 are complete when the dashboard and family management flows both work independently.

---

## Phase 5: User Story 3 - Review and Filter Family Todos (Priority: P3)

**Goal**: Deliver a detailed todo page with assignee filtering and supported create, update, and delete workflows.

**Independent Test**: Open the detailed todo page, verify all todos load for the active family, filter by a specific family member, and confirm create, update, and delete flows use the existing backend contract.

### Implementation for User Story 3

- [ ] T025 [P] [US3] Restructure the detailed todo page for filtering and CRUD ergonomics in `ui/src/pages/TasksEvents.tsx`
- [ ] T026 [P] [US3] Update the todo page styling for responsive detail and filter layout in `ui/src/pages/TasksEvents.css`
- [ ] T027 [US3] Implement todo list and assignee filter queries in `ui/src/api/todoApi.ts` and `ui/src/pages/TasksEvents.tsx`
- [ ] T028 [US3] Implement todo create, update, and delete flows with the current backend payload shape in `ui/src/api/todoApi.ts` and `ui/src/pages/TasksEvents.tsx`
- [ ] T029 [US3] Add loading, empty, filtered-empty, validation, and error handling for todo workflows in `ui/src/pages/TasksEvents.tsx` and `ui/src/pages/TasksEvents.css`
- [ ] T030 [US3] Align dashboard-to-todo navigation and shared member filter behavior in `ui/src/pages/Dashboard.tsx`, `ui/src/pages/TasksEvents.tsx`, and `ui/src/components/Sidebar.tsx`
- [ ] T031 [US3] Validate detailed todo workflows with `npm run build` in `ui/package.json`

**Checkpoint**: All user stories are complete when dashboard, family management, and detailed todo flows each work independently.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Finalize shared UX quality, responsive behavior, and feature documentation alignment.

- [ ] T032 [P] Remove or clearly de-emphasize deferred planner surface entry points in `ui/src/App.tsx` and `ui/src/components/Sidebar.tsx`
- [ ] T033 [P] Harmonize shared responsive spacing and dark-theme styling across `ui/src/index.css`, `ui/src/components/Layout.css`, and `ui/src/components/Sidebar.css`
- [ ] T034 Review route and page ergonomics across `ui/src/pages/Dashboard.tsx`, `ui/src/pages/FamilyMembers.tsx`, and `ui/src/pages/TasksEvents.tsx`
- [ ] T035 Run quickstart validation against `specs/001-family-dashboard/quickstart.md`

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies and can start immediately.
- **Foundational (Phase 2)**: Depends on Setup completion and blocks all user stories.
- **User Stories (Phases 3-5)**: Depend on Foundational completion.
- **Polish (Phase 6)**: Depends on all desired user stories being complete.

### User Story Dependencies

- **User Story 1 (P1)**: Starts after Foundational completion and forms the MVP.
- **User Story 2 (P2)**: Starts after Foundational completion and reuses shared family/store infrastructure from P1.
- **User Story 3 (P3)**: Starts after Foundational completion and reuses shared store, shell, and family-member data from P1/P2.

### Within Each User Story

- Shared data and query wiring before page-specific interaction details.
- Page structure before styling refinement.
- Core user flow before validation and state-feedback polish.
- Build validation before moving to the next checkpoint.

### Parallel Opportunities

- `T003` can run in parallel with `T002` once dependency installation is ready.
- `T005`, `T006`, and `T009` can run in parallel in the Foundational phase.
- Within US1, `T011` and `T012` can run in parallel.
- Within US2, `T018` and `T019` can run in parallel.
- Within US3, `T025` and `T026` can run in parallel.
- In Phase 6, `T032` and `T033` can run in parallel.

---

## Parallel Example: User Story 2

```bash
# Launch parallel implementation tasks for User Story 2:
Task: "Restructure the family management page for viewing, renaming, and member creation workflows in ui/src/pages/FamilyMembers.tsx"
Task: "Update family management styling to preserve the current visual language while improving responsiveness in ui/src/pages/FamilyMembers.css"
```

---

## Parallel Example: User Story 3

```bash
# Launch parallel implementation tasks for User Story 3:
Task: "Restructure the detailed todo page for filtering and CRUD ergonomics in ui/src/pages/TasksEvents.tsx"
Task: "Update the todo page styling for responsive detail and filter layout in ui/src/pages/TasksEvents.css"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup.
2. Complete Phase 2: Foundational.
3. Complete Phase 3: User Story 1.
4. Validate the dashboard independently.
5. Demo or review before expanding scope.

### Incremental Delivery

1. Finish Setup and Foundational work.
2. Deliver User Story 1 as the dashboard MVP.
3. Add User Story 2 for API-backed family management.
4. Add User Story 3 for detailed todo management and filtering.
5. Finish with polish and quickstart validation.

### Parallel Team Strategy

1. Complete Setup and Foundational tasks together.
2. Assign one developer to dashboard work, one to family management, and one to detailed todos once foundational work is complete.
3. Merge story slices only after each one passes its independent validation checkpoint.

---

## Notes

- `[P]` tasks work on separate files or separable concerns.
- User story tasks are scoped to frontend-only implementation in `ui/`.
- The task list intentionally excludes backend changes, automated test authoring, and activity/planner work.
- The frontend may be restructured for usability and responsiveness, but must preserve the current visual language.
