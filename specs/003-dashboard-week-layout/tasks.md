# Tasks: Dashboard Week-Focused Layout

**Input**: Design documents from `/specs/003-dashboard-week-layout/`
**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, contracts/

**Tests**: No separate automated test tasks are generated because the specification does not explicitly request TDD or new automated tests. Each story includes required build and manual verification tasks.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

**Scope Rule**: Unless the feature spec explicitly authorizes backend work, tasks MUST stay in
`ui/` and related frontend documentation only.

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Confirm scope and load the exact frontend files that the dashboard redesign will touch

- [x] T001 Review scope and affected modules in `specs/003-dashboard-week-layout/spec.md` and `specs/003-dashboard-week-layout/plan.md`
- [x] T002 Inspect the current dashboard, weekly planner, activities, and todo surfaces in `ui/src/pages/Dashboard.tsx`, `ui/src/pages/Dashboard.css`, `ui/src/pages/WeeklyPlanner.tsx`, `ui/src/pages/WeeklyPlanner.css`, `ui/src/pages/Activities.tsx`, and `ui/src/pages/Tasks.tsx`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Establish shared dashboard data derivation and layout scaffolding that all user stories depend on

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [x] T003 Update shared week-selection and grouping helpers for dashboard reuse in `ui/src/utils/weekUtils.ts`
- [x] T004 [P] Establish the stacked dashboard layout scaffold for selected-day detail, weekly overview, and open-todo sections in `ui/src/pages/Dashboard.tsx`
- [x] T005 [P] Prepare base styles for the stacked dashboard layout and reusable selected/today state hooks in `ui/src/pages/Dashboard.css`

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - Focus Dashboard on a Selected Day (Priority: P1) 🎯 MVP

**Goal**: Make the top dashboard section show detailed activities for the currently selected day, with the current day selected by default

**Independent Test**: Open the dashboard with activities on multiple days, confirm the current day is selected by default, then switch days and confirm the top section updates to the newly selected day with correct German loading, empty, and error states.

### Implementation for User Story 1

- [x] T006 [US1] Implement selected-day state, default current-day selection, and selected-day activity filtering in `ui/src/pages/Dashboard.tsx`
- [x] T007 [P] [US1] Style the selected-day detail section, selected-day heading, and selected-day activity cards in `ui/src/pages/Dashboard.css`
- [x] T008 [US1] Add German selected-day loading, empty, and error-state rendering to `ui/src/pages/Dashboard.tsx`
- [x] T009 [US1] Verify selected-day dashboard behavior with `npm run build` in `ui/` and the default-day checks in `specs/003-dashboard-week-layout/quickstart.md`

**Checkpoint**: At this point, User Story 1 should be fully functional and testable independently

---

## Phase 4: User Story 2 - Use a Weekly Overview on the Dashboard (Priority: P1)

**Goal**: Replace the simple week strip with a Wochenübersicht-style weekly overview that drives day selection on the dashboard

**Independent Test**: Open the dashboard, confirm all seven days render in a weekly overview matching the `/week` visual pattern, then select another day and verify the top detail section updates while today and selected-day states remain clear.

### Implementation for User Story 2

- [x] T010 [P] [US2] Rework the dashboard week-overview markup to render seven selectable day columns with grouped activities in `ui/src/pages/Dashboard.tsx`
- [x] T011 [P] [US2] Mirror the Wochenübersicht visual grammar, including selected-day and today states, in `ui/src/pages/Dashboard.css` using `ui/src/pages/WeeklyPlanner.css` as the style reference
- [x] T012 [US2] Align dashboard week grouping and active-day rendering logic with the existing weekly planner behavior in `ui/src/pages/Dashboard.tsx` and `ui/src/pages/WeeklyPlanner.tsx`
- [x] T013 [US2] Verify the embedded weekly overview with `npm run build` in `ui/` and the week-selection checks in `specs/003-dashboard-week-layout/quickstart.md`

**Checkpoint**: At this point, User Stories 1 AND 2 should both work independently

---

## Phase 5: User Story 3 - Simplify the Remaining Dashboard Content (Priority: P2)

**Goal**: Remove dashboard summary tiles, family-member content, and the redundant week preview so only the open-todo overview remains below the activity sections

**Independent Test**: Open the dashboard and confirm that below the selected-day detail and weekly overview there is only the open-todo overview, with the removed sections no longer rendered anywhere on the page.

### Implementation for User Story 3

- [x] T014 [US3] Remove the stats grid, family-member dashboard section, and separate week-preview markup from `ui/src/pages/Dashboard.tsx`
- [x] T015 [P] [US3] Remove obsolete stats, family-member, and secondary-week-preview styles and reflow the dashboard into a single-column stack in `ui/src/pages/Dashboard.css`
- [x] T016 [US3] Retain and adapt only the open-todo overview, including German loading, empty, and error-state handling, in `ui/src/pages/Dashboard.tsx`
- [x] T017 [US3] Verify the simplified dashboard content with `npm run build` in `ui/` and the cleanup checks in `specs/003-dashboard-week-layout/quickstart.md`

**Checkpoint**: At this point, User Stories 1, 2, and 3 should be fully functional on the dashboard

---

## Phase 6: User Story 4 - Open Activity Details from the Selected Day (Priority: P3)

**Goal**: Preserve correct activity detail navigation from the selected-day detail section after day switching

**Independent Test**: Select a non-default day on the dashboard, choose one of that day's activities, and confirm the detailed activities page opens with the same activity selected or highlighted.

### Implementation for User Story 4

- [x] T018 [US4] Preserve activity click-through from selected-day cards using selected activity identity handoff in `ui/src/pages/Dashboard.tsx`
- [x] T019 [P] [US4] Ensure selected activity arrival remains clear after dashboard navigation, including fallback messaging when needed, in `ui/src/pages/Activities.tsx` and `ui/src/pages/Activities.css`
- [x] T020 [US4] Verify selected-day activity navigation with `npm run build` in `ui/` and the activity-navigation checks in `specs/003-dashboard-week-layout/quickstart.md`

**Checkpoint**: All user stories should now be independently functional

---

## Phase 7: Polish & Cross-Cutting Concerns

**Purpose**: Final consistency, responsiveness, and product-surface cleanup across the affected dashboard flows

- [x] T021 [P] Review German dashboard copy, labels, and aria text across `ui/src/pages/Dashboard.tsx`, `ui/src/components/Layout.tsx`, and `ui/src/components/Sidebar.tsx`
- [x] T022 [P] Polish responsive spacing, selected-versus-today contrast, and visual consistency across `ui/src/pages/Dashboard.css` and `ui/src/pages/WeeklyPlanner.css`
- [x] T023 Run `npm run build` in `ui/` and complete the full regression flow in `specs/003-dashboard-week-layout/quickstart.md`

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3+)**: All depend on Foundational phase completion
- **Polish (Phase 7)**: Depends on all desired user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational - establishes selected-day behavior for the top dashboard section
- **User Story 2 (P1)**: Can start after Foundational - builds on the same selected-day data model but remains independently verifiable as the embedded week-selector surface
- **User Story 3 (P2)**: Can start after Foundational - simplifies the remaining dashboard surface while preserving the retained todo overview
- **User Story 4 (P3)**: Can start after Foundational - depends on selected-day dashboard activity cards being present for navigation verification

### Within Each User Story

- Shared data derivation before final UI wiring
- Markup and interaction updates before CSS polish
- Story-specific verification after implementation
- Complete one story before relying on it for polish work

### Parallel Opportunities

- `T004` and `T005` can run in parallel after `T003`
- Within **US1**, `T007` can run in parallel with the state work in `T006` once the selected-day card structure is agreed
- Within **US2**, `T010` and `T011` can run in parallel
- Within **US3**, `T015` can run in parallel with the markup cleanup in `T014`
- Within **US4**, `T019` can run in parallel with the dashboard navigation work in `T018`
- Polish tasks `T021` and `T022` can run in parallel

---

## Parallel Example: User Story 2

```bash
# Launch parallel User Story 2 work after the foundational phase:
Task: "Rework the dashboard week-overview markup to render seven selectable day columns with grouped activities in ui/src/pages/Dashboard.tsx"
Task: "Mirror the Wochenübersicht visual grammar, including selected-day and today states, in ui/src/pages/Dashboard.css using ui/src/pages/WeeklyPlanner.css as the style reference"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational
3. Complete Phase 3: User Story 1
4. **STOP and VALIDATE**: Verify the selected-day top section independently

### Incremental Delivery

1. Finish Setup + Foundational to establish shared dashboard structure
2. Deliver User Story 1 for selected-day detail behavior
3. Add User Story 2 for the embedded Wochenübersicht-style selector
4. Add User Story 3 to simplify the remainder of the dashboard
5. Add User Story 4 to confirm detail navigation remains correct
6. Finish with cross-cutting polish and full regression validation

### Suggested MVP Scope

- **MVP**: Phase 3 / User Story 1
- **Recommended first complete dashboard release**: User Stories 1 + 2 + 3

---

## Notes

- [P] tasks = different files, no dependencies on incomplete tasks
- [US1] to [US4] labels map tasks directly to the specification user stories
- All implementation tasks stay within approved frontend scope under `ui/`
- Build verification is repeated at story checkpoints because no separate automated test tasks were requested in the specification
