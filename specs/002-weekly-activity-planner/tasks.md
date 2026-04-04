# Tasks: Weekly Activity Planner

**Input**: Design documents from `/specs/002-weekly-activity-planner/`
**Prerequisites**: plan.md ✅ spec.md ✅ research.md ✅ data-model.md ✅ contracts/activity-api-contract.md ✅ quickstart.md ✅

**Tests**: Not requested — no dedicated test tasks generated. Verification relies on `npm run build` and targeted manual smoke tests.

**Scope**: Frontend-focused implementation in `ui/` and feature documentation in `specs/002-weekly-activity-planner/`. Generated assets and unrelated backend files remain out of scope.

---

## Phase 1: Setup

**Purpose**: Confirm the updated scope, affected frontend files, and no-new-dependency constraint.

- [x] T001 Confirm the updated dashboard-first scope, affected `ui/src/` paths, and no-new-dependency constraint in `ui/package.json`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Shared infrastructure required by all user story surfaces.

**⚠️ CRITICAL**: No user story implementation can begin until this phase is complete.

- [x] T002 Add or verify `"Activity"` tag integration in `ui/src/api/baseApi.ts` and shared activity endpoint wiring in `ui/src/api/activityApi.ts`
- [x] T003 [P] Define or align `Activity`, request models, and selection-context types in `ui/src/app/store/types.ts`
- [x] T004 [P] Create or align shared date and time helpers in `ui/src/utils/weekUtils.ts` for current day, current week, and time formatting
- [x] T005 Update routing and navigation in `ui/src/App.tsx` and `ui/src/components/Sidebar.tsx` so `/`, `/week`, and `/activities` match the plan
- [x] T006 Establish selected-activity handoff pattern (`useLocation().state.selectedId`) between `ui/src/pages/Dashboard.tsx` and `ui/src/pages/Activities.tsx`

**Checkpoint**: Foundation complete — shared activity data, navigation, and selection context are ready for story work.

---

## Phase 3: User Story 1 — View Today's Activities on the Dashboard (Priority: P1) 🎯 MVP

**Goal**: The dashboard today section is the most visually dominant section on the page. Each activity entry shows name, time, and all participant badges inline — no hover or expand needed. Selecting an entry opens the detailed view for that activity.

**Independent Test**: Open `/` with activities today and confirm: today section has more visual weight than all other sections; each entry shows name, time, location (if set), and participant badge(s) with `+N` overflow; German empty/loading/error states appear correctly; clicking an entry opens `/activities` for that item.

### Implementation for User Story 1

- [x] T007 [P] [US1] Add `.dash-today-section` dominant accent styles, `.dash-act-item` clickable row, `.dash-act-badges` / `.dash-act-badge` inline badge display, and `+N` overflow badge to `ui/src/pages/Dashboard.css`
- [x] T008 [US1] Render today activities in `ui/src/pages/Dashboard.tsx` with inline participant badges (first-letter avatar + `+N` overflow), loading/empty/error states in German, and dominant visual treatment
- [x] T009 [US1] Wire click-through in `ui/src/pages/Dashboard.tsx` using `navigate('/activities', { state: { selectedId } })` and accept `selectedId` in `ui/src/pages/Activities.tsx` with scroll + highlight on arrival
- [x] T010 [US1] Verify User Story 1: `npm run build` in `ui/` and smoke tests confirming dominant today section, visible participant badges, and correct detail-view arrival on desktop and mobile

**Checkpoint**: Dashboard delivers today's activity awareness with participant clarity and opens the correct detail view.

---

## Phase 4: User Story 2 — View the Rest of the Week's Activities on the Dashboard (Priority: P2)

**Goal**: A secondary section below the today section shows activities for all other days of the current week grouped by day, at visually reduced emphasis. Days with no activities are omitted; if no other day has activities the section is hidden entirely.

**Independent Test**: Open `/` with activities on multiple days and confirm: other-day activities appear below the today section; the rest-of-week section has clearly less visual weight than the today section (no accent background, standard border); the section is absent when no other day has activities; layout stays readable on mobile.

### Implementation for User Story 2

- [x] T011 [P] [US2] Add `.dash-week-preview` section styles (standard surface border, muted text, compact rows, no accent) and `.dash-week-preview-day` label row to `ui/src/pages/Dashboard.css`
- [x] T012 [US2] In `ui/src/pages/Dashboard.tsx`, derive `otherDayActivities` grouped by `WeekDay` (all current-week days excluding today); render as a compact secondary card with day label, activity name, and time per row; hide section entirely when all other days are empty; add "Alle anzeigen →" link to `/week`
- [x] T013 [US2] Verify User Story 2: `npm run build` in `ui/` and smoke tests confirming secondary visual weight, correct day grouping, section hidden when empty, and mobile readability

**Checkpoint**: Dashboard gives a full week-at-a-glance without letting the rest-of-week preview compete with today's dominant section.

---

## Phase 5: User Story 3 — View Weekly Activity Overview with Today's Highlights (Priority: P1)

**Goal**: The `/week` page shows all seven days of the current week, always renders today's column with accent emphasis, and uses German states throughout.

**Independent Test**: Open `/week` with activities across the week and confirm: all 7 day columns render regardless of activity count; today's column is visually distinct; German empty/loading/error states appear; layout collapses correctly on mobile.

### Implementation for User Story 3

- [x] T014 [P] [US3] Update `ui/src/pages/WeeklyPlanner.css` for today-column accent emphasis, responsive grid breakpoints, and dark-theme error state
- [x] T015 [US3] Align `ui/src/pages/WeeklyPlanner.tsx` to always render all 7 day columns, use stable `act.id` as key, and show correct German empty states
- [x] T016 [US3] Verify User Story 3: `npm run build` in `ui/` and smoke tests for `/week`

**Checkpoint**: Weekly overview is independently functional and consistent with dashboard date logic.

---

## Phase 6: User Story 4 — Plan a New Activity for the Current Week (Priority: P2)

**Goal**: Users can create activities from `/activities` with German validation and immediate list updates.

**Independent Test**: Open `/activities`, create an activity with valid data — it appears immediately; submit without required fields — German validation messages appear.

### Implementation for User Story 4

- [x] T017 [P] [US4] Align `ui/src/pages/Activities.css` styles for form, validation errors (dark-theme CSS variables), and create button
- [x] T018 [US4] Align create-form behavior in `ui/src/pages/Activities.tsx`: required-field validation, optional fields, participant toggles, RTK mutation with `invalidatesTags`, German states
- [x] T019 [US4] Verify User Story 4: `npm run build` in `ui/` and manual create-flow smoke tests for `/activities`

**Checkpoint**: Activity creation works independently on the detailed page.

---

## Phase 7: User Story 5 — View and Manage All Activities in Detail (Priority: P3)

**Goal**: `/activities` supports focused arrival from the dashboard (scroll + accent highlight), participant filtering, inline edit, and delete — all with immediate list updates.

**Independent Test**: Navigate from dashboard to `/activities` — selected activity is highlighted and scrolled into view; apply a participant filter; edit and save an activity; delete an activity — all update the list immediately.

### Implementation for User Story 5

- [x] T020 [P] [US5] Add `.act-item-highlighted` accent ring and filter-row styles to `ui/src/pages/Activities.css`
- [x] T021 [US5] In `ui/src/pages/Activities.tsx`, read `useLocation().state.selectedId`, scroll to and highlight the matching item via `scrollIntoView`, and pass `highlighted` prop to `ActivityItem`
- [x] T022 [US5] Implement participant filter pill row in `ui/src/pages/Activities.tsx` with "Alle" default and per-member RTK Query filter arg
- [x] T023 [US5] Implement inline edit form (pre-filled) and delete flow in `ActivityItem` using `updateActivity` and `deleteActivity` RTK mutations
- [x] T024 [US5] Verify User Story 5: `npm run build` in `ui/` and smoke tests for dashboard click-through, filter, edit, and delete

**Checkpoint**: Detailed management supports focused navigation and full current-week maintenance.

---

## Phase 8: Polish & Cross-Cutting Concerns

**Purpose**: Final consistency check covering all five stories, including the new rest-of-week section.

- [x] T025 [P] Audit German user-facing copy in `ui/src/pages/Dashboard.tsx` (today section + rest-of-week section titles, empty states), `ui/src/pages/WeeklyPlanner.tsx`, `ui/src/pages/Activities.tsx`, and `ui/src/components/Sidebar.tsx`
- [x] T026 [P] Review responsive behavior on 320 px mobile for the today section participant badges (flex-wrap + overflow badge), the rest-of-week compact rows, and the weekly planner collapsed grid in `ui/src/pages/Dashboard.css` and `ui/src/pages/WeeklyPlanner.css`
- [x] T027 Run final `npm run build` in `ui/` and validate the full quickstart smoke-test sequence across `/` (today + rest-of-week), `/week`, and `/activities`

---

## Dependencies & Execution Order

### Phase Dependencies

```
Phase 1 (Setup)
    └── Phase 2 (Foundational) ← BLOCKS all story work
            ├── Phase 3 (US1 — Today Section) 🎯 MVP  ✅ done
            ├── Phase 4 (US2 — Rest-of-Week Preview)  ✅ done
            ├── Phase 5 (US3 — Weekly Overview)       ✅ done
            ├── Phase 6 (US4 — Create Activity)       ✅ done
            └── Phase 7 (US5 — Detailed Management)   ✅ done
                    └── Phase 8 (Polish)               ← after US2
```

### User Story Dependencies

- **US1 (P1 today section)**: Done. Dominant accent section, inline participant badges, click-through navigation.
- **US2 (P2 rest-of-week preview)**: Done. Compact secondary section below today; grouped by day; hidden when empty.
- **US3 (P1 weekly overview)**: Done. Independent of dashboard stories.
- **US4 (P2 create activity)**: Done. Independent of overview stories.
- **US5 (P3 detailed management)**: Done. Builds on the detail page from US4.

### Parallel Opportunities

**US2 (next sprint):**

```
T011 [P]  ← rest-of-week CSS styles
T012      ← rest-of-week rendering in Dashboard.tsx
T013      ← verification
```

**Polish (after US2):**

```
T025 [P], T026 [P]  ← can run in parallel
T027                ← final validation gate
```

---

## Implementation Strategy

### Remaining Work

None — all 27 tasks complete.

### Already Delivered

- T001–T024: Done. Dashboard today section with participant badges, weekly planner, create/edit/delete/filter on activities page, selected-item scroll+highlight, rest-of-week preview.

---

## Summary

| Metric               | Value          |
| -------------------- | -------------- |
| Total tasks          | 27             |
| Completed            | 27 (T001–T027) |
| Remaining            | 0              |
| Setup tasks          | 1              |
| Foundational tasks   | 5              |
| US1 tasks            | 4 ✅           |
| US2 tasks            | 3 ✅           |
| US3 tasks            | 3 ✅           |
| US4 tasks            | 3 ✅           |
| US5 tasks            | 5 ✅           |
| Polish tasks         | 3 ✅           |
| Parallelizable `[P]` | 9              |
| MVP scope            | T001–T010 ✅   |

---
