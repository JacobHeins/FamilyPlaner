# Implementation Plan: Dashboard Week-Focused Layout

**Branch**: `003-dashboard-week-layout` | **Date**: 2026-04-05 | **Spec**: [spec.md](spec.md)
**Input**: Feature specification from `/workspace/specs/003-dashboard-week-layout/spec.md`

## Summary

Refocus the FamilyPlanner dashboard around one selectable day at a time. The dashboard
becomes a three-zone page:

1. **Selected-day activity detail** at the top, defaulting to today and updating when the
   user selects another day.
2. **Embedded weekly overview** directly below, visually aligned with the existing
   Wochenübersicht and used as the day-selection control surface.
3. **Open todo overview** as the only remaining lower content area after removing family
   member dashboard content, summary tiles, and the redundant secondary activity preview.

The feature stays frontend-only, reuses existing RTK Query family/activity/todo data,
preserves the existing `/activities` navigation flow, and keeps all dashboard copy in German.

## Technical Context

**Language/Version**: TypeScript 5.7 (SPA); Java 21 backend contract as integration context  
**Primary Dependencies**: React 19, React Router 7, Redux Toolkit / RTK Query 2.6, lucide-react  
**Storage**: Existing backend persistence only; no new frontend persistence or browser storage  
**Testing**: `npm run build` in `ui/`; manual smoke tests for dashboard day selection, selected-day detail rendering, weekly overview rendering, open-todo overview, and activity click-through  
**Target Platform**: Modern desktop and mobile browsers  
**Project Type**: Dashboard SPA inside a Spring Boot + Vite monorepo  
**Performance Goals**: Meet spec outcomes for selected-day recognition in 3 seconds, selected-day detail comprehension in 5 seconds, and single-interaction day switching  
**Constraints**: No backend changes; no new frontend dependencies unless justified; all user-facing text in German; do not hand-edit generated static assets  
**Scale/Scope**: One dashboard page redesign, reuse of one existing weekly planner visual pattern, one retained todo overview section, no route additions required

## Constitution Check

_GATE: Must pass before Phase 0 research. Re-check after Phase 1 design._

| Principle                        | Status  | Notes                                                                                                                                                          |
| -------------------------------- | ------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| I. Frontend-only scope           | ✅ PASS | Planned work is contained to the SPA under `ui/` and to feature documentation under `specs/003-dashboard-week-layout/`.                                        |
| II. Dashboard SPA architecture   | ✅ PASS | The redesign stays inside the existing dashboard route and reuses the current React, router, and page/CSS structure rather than adding a parallel shell.       |
| III. Backend contract fidelity   | ✅ PASS | Existing family, activity, and todo queries remain the source of truth. No new endpoint, payload, or mutation requirements are introduced.                     |
| IV. Type-safe UX quality gates   | ✅ PASS | Loading, empty, and error states are defined for selected-day activities, weekly overview rendering, and open todos; selected-day state is explicitly modeled. |
| V. Incremental frontend delivery | ✅ PASS | Work splits cleanly into reviewable slices: selected-day state, embedded weekly overview, simplified lower dashboard, and preserved activity navigation.       |
| VI. German-language UI           | ✅ PASS | All dashboard headings, empty states, loading messages, error messages, and interaction labels remain German-only.                                             |

**Post-design re-check**: All six principles still pass after design. The plan keeps the dashboard redesign within the existing SPA, models selected-day behavior without speculative API changes, and preserves explicit German-language states across all visible flows.

## Project Structure

### Documentation

```text
specs/003-dashboard-week-layout/
├── plan.md
├── spec.md
├── research.md
├── data-model.md
├── quickstart.md
├── contracts/
│   └── dashboard-layout-contract.md
├── checklists/
│   └── requirements.md
└── tasks.md
```

### Source Code Changes

```text
ui/src/
├── pages/
│   ├── Dashboard.tsx           # MODIFY: selected-day state, top activity detail, embedded week overview, simplified lower section
│   ├── Dashboard.css           # MODIFY: selected-day hero styling, interactive week grid, simplified single-column lower layout
│   ├── WeeklyPlanner.tsx       # OPTIONAL REUSE SOURCE: visual pattern reference for embedded week overview
│   └── WeeklyPlanner.css       # OPTIONAL REUSE SOURCE: day-card/grid styling reference
├── utils/
│   └── weekUtils.ts            # OPTIONAL MODIFY: shared helpers if selected-day derivation or grouping helpers improve reuse
├── components/
│   ├── Layout.tsx              # OPTIONAL MODIFY: topbar subtitle if dashboard content labels need to reflect the simplified scope
│   └── Sidebar.tsx             # OPTIONAL MODIFY: copy only if dashboard labeling becomes inconsistent after removal of dashboard family content
└── api/
    ├── activityApi.ts          # REUSE: existing activity query and navigation identity source
    ├── todoApi.ts              # REUSE: existing open-todo data source
    └── familyApi.ts            # REUSE: existing active family context source
```

**Structure Decision**: Implementation remains in `ui/`. The backend in `src/` is a read-only integration context only.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

| Violation | Why Needed | Simpler Alternative Rejected Because |
| --------- | ---------- | ------------------------------------ |
| None      | —          | —                                    |

## Research Highlights

- Current dashboard data flow already fetches families first, then conditionally queries todos and activities using the primary family ID.
- The existing week model is derived locally through `getWeekDates()` and `getToday()`, which is sufficient for a dashboard selected-day state without backend involvement.
- The current Wochenübersicht already provides the day-column and activity-card visual grammar the user wants repeated on the dashboard.
- Activity click-through already exists via `navigate('/activities', { state: { selectedId } })`, so the redesign can preserve navigation without changing route contracts.

## Component Architecture

### Dashboard (`/`)

The redesigned dashboard becomes a vertical flow instead of a stats-plus-two-column composition:

```text
┌───────────────────────────────────────────────────────────┐
│ Header                                                    │
├───────────────────────────────────────────────────────────┤
│ [PRIMARY] Aktivitäten am ausgewählten Tag                 │
│  - selected day title                                     │
│  - activity cards for that day                            │
│  - empty / loading / error states                         │
├───────────────────────────────────────────────────────────┤
│ [SECONDARY] Wochenübersicht                               │
│  - same weekly visual grammar as /week                    │
│  - seven day columns/cards                                │
│  - today highlight + selected-day highlight               │
│  - selecting a day updates the top section                │
├───────────────────────────────────────────────────────────┤
│ [TERTIARY] Offene Aufgaben                                │
│  - retained overview only                                 │
└───────────────────────────────────────────────────────────┘
```

**Selected-day state rules**:

- Initialize selected day from `getToday()` after week derivation.
- Keep selected day in component-local state because it is page-scoped, transient, and not a shared navigation concern.
- If activity data refreshes, preserve the selected day unless it falls outside the currently derived week.
- Distinguish between `isToday` and `isSelected` in the embedded week overview so both cues can coexist.

**Selected-day detail rules**:

- The top card always reflects the active day, not implicitly "today" after the user changes selection.
- Each activity row continues to support click-through into `/activities` using the existing selected activity ID handoff.
- Empty state text must refer to the selected day rather than implying the user is still on today.

**Embedded weekly overview rules**:

- Reuse the existing Wochenübersicht visual grammar as closely as practical inside the dashboard.
- Every day in the current week remains visible even when empty.
- The weekly overview becomes both a summary and a selector; day containers therefore need pointer, keyboard, and active-state treatment.

**Open todo overview rules**:

- Keep only the open-todo list from the current dashboard lower content.
- Remove the family-members card, stats grid, and separate "Weitere Aktivitäten der Woche" section.
- Preserve current loading, empty, and error treatment for todo data, adapted to the simplified layout.

## Data Flow Design

1. `useGetFamiliesQuery()` resolves the active family context.
2. `useGetActivitiesQuery({ familyId })` continues to fetch all family activities used by both dashboard sections.
3. `getWeekDates()` derives the current week columns.
4. Local selected-day state points to one of those seven `dateStr` values.
5. Top-section activities are derived by filtering the fetched activities to `selectedDay` and sorting by `startTime`.
6. Embedded week overview groups the same activity list by each week day.
7. `useGetTodosQuery({ familyId })` continues to provide todos; dashboard derives open todo items from the existing response.

## Implementation Strategy

### Slice 1 — Selected-day dashboard state (US1 · FR-001/002/003/004/007/015)

1. Add local selected-day state to `Dashboard.tsx`, seeded from the current week and `getToday()`.
2. Replace the current hard-coded today-only activity derivation with a selected-day derivation.
3. Update loading, error, and empty states so they reflect selected-day semantics.

### Slice 2 — Embedded Wochenübersicht on the dashboard (US2 · FR-005/006/008)

1. Replace the existing simple week strip with a weekly overview that mirrors the current `/week` visual structure.
2. Reuse or mirror existing day-card/activity-card markup and CSS patterns from `WeeklyPlanner`.
3. Add active-day interaction states so clicking or keyboard-selecting a day updates the selected dashboard day.

### Slice 3 — Simplified lower dashboard area (US3 · FR-010/011/012/013/014)

1. Remove the stats grid, family-member card, and redundant week-preview section.
2. Reflow the page into a single-column stack with only the open-todo overview beneath the activity sections.
3. Preserve the current open-todo list behavior and German empty/error messages in the simplified layout.

### Slice 4 — Preserve activity detail navigation (US4 · FR-009)

1. Keep the current `navigate('/activities', { state: { selectedId } })` behavior from selected-day activity entries.
2. Verify the clicked activity always matches the currently selected day after a day switch.

## Risks

| Risk                                                                 | Likelihood | Impact | Mitigation                                                                                     |
| -------------------------------------------------------------------- | ---------- | ------ | ---------------------------------------------------------------------------------------------- |
| Dashboard week overview diverges visually from `/week` over time     | Medium     | Medium | Prefer extracting or deliberately mirroring a shared visual pattern instead of re-inventing it |
| Today highlight and selected-day highlight become visually confusing | Medium     | High   | Design separate states for `today` and `selected`, and validate combinations in the week grid  |
| Removing summary tiles weakens perceived context for todo urgency    | Low        | Medium | Keep meaningful open-todo metadata visible in the retained todo overview                       |
| Family-loading dependency delays selected-day rendering              | Low        | Medium | Gate activity/todo queries on primary family as the current dashboard already does             |

## Verification Strategy

- Run `npm run build` in `ui/`.
- Manually verify dashboard load with activities on multiple days.
- Manually verify default selected day equals the current day.
- Manually verify selecting another day updates only the top activity detail area.
- Manually verify the weekly overview still renders with no activities.
- Manually verify the dashboard no longer shows stats tiles, family member cards, or the old week-preview section.
- Manually verify open todos remain visible and correctly localized in German.
