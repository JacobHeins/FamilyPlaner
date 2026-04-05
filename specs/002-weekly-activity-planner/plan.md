# Implementation Plan: Weekly Activity Planner

**Branch**: `002-weekly-activity-planner` | **Date**: 2026-04-04 | **Spec**: [spec.md](spec.md)
**Input**: Feature specification from `/workspace/specs/002-weekly-activity-planner/spec.md`

## Summary

Extend the FamilyPlaner SPA with activity planning centered on the dashboard as the
primary entry point. The feature delivers three surfaces:

1. **Dashboard** — a today-activity section that is the most visually dominant section on
   the page, showing each activity's name, time, and all participants at a glance without
   any hover or expand interaction, plus a secondary rest-of-week preview for the other six
   days of the current week at reduced visual emphasis.
2. **Weekly Planner page** (`/week`) — a full current-week overview grouped by day with
   today visually emphasized.
3. **Detailed Activities page** (`/activities`) — the full-management surface for the
   current week: create, edit, delete, and filter by participant.

All surfaces consume the existing backend activity contract via RTK Query, reuse the
current SPA routing and layout shell, and are implemented without new frontend dependencies.
All user-facing text is in German.

## Technical Context

**Language/Version**: TypeScript 5.7 (SPA); Java 21 backend contract as integration context
**Primary Dependencies**: React 19, React Router 7, RTK Query 2.6, lucide-react
**Storage**: Existing backend persistence only; no new frontend storage
**Testing**: `npm run build` for type/build validation; manual smoke tests across dashboard (today section, rest-of-week preview, click-through), weekly overview, and detailed management flows
**Target Platform**: Modern browsers on desktop, tablet, and smartphone
**Project Type**: Dashboard SPA within a Spring Boot + Vite monorepo
**Performance Goals**: Dashboard users identify today's activities within 5 seconds (SC-001); identify all participants of a today activity in a single glance (SC-011); open the detail view in one interaction (SC-002)
**Constraints**: No new frontend dependencies; all user-facing text in German; generated assets remain untouched
**Scale/Scope**: One dashboard enhancement (two sub-sections), two activity pages, one shared API slice, one shared date utility, route and navigation updates

## Constitution Check

_GATE: Must pass before Phase 0 research. Re-check after Phase 1 design._

| Principle                        | Status  | Notes                                                                                                                                                                                                              |
| -------------------------------- | ------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| I. Frontend-only scope           | ✅ PASS | Main implementation remains in `ui/`. The previously authorized backend identity exposure is treated as an available contract input, not a new planning blocker.                                                   |
| II. Dashboard SPA architecture   | ✅ PASS | The dashboard becomes the primary daily activity surface (dominant today section + secondary rest-of-week preview). All additions fit the existing SPA routes, layout shell, page structure, and shared utilities. |
| III. Backend contract fidelity   | ✅ PASS | The plan consumes the available activity list and item identity contract without proposing speculative endpoint changes. Participant data is already part of the activity response.                                |
| IV. Type-safe UX quality gates   | ✅ PASS | All views define explicit loading, empty, success, and error states. Activity identity is modeled for navigation. Participant arrays are typed and rendered inline — no hidden state.                              |
| V. Incremental frontend delivery | ✅ PASS | Work is organized into independently reviewable slices: (1) dashboard today section, (2) dashboard rest-of-week, (3) weekly overview page, (4) creation flow, (5) detailed management.                             |
| VI. German-language UI           | ✅ PASS | Dashboard (both sub-sections), weekly overview, and detailed activity management all require German-only user-facing text including empty/error/loading states.                                                    |

**Post-design re-check**: All six principles still pass. The participant-clarity requirement (FR-022/023/024) drives inline rendering only — no new API calls or data model changes required.

## Project Structure

### Documentation

```text
specs/002-weekly-activity-planner/
├── plan.md
├── spec.md
├── research.md
├── data-model.md
├── quickstart.md
├── contracts/
│   └── activity-api-contract.md
├── checklists/
│   └── requirements.md
└── tasks.md
```

### Source Code Changes

```text
ui/src/
├── api/
│   ├── baseApi.ts              # MODIFY: ensure "Activity" tag type registered
│   └── activityApi.ts          # MODIFY: RTK Query endpoints for activities
├── app/store/
│   └── types.ts                # MODIFY: activity response and request types
├── utils/
│   └── weekUtils.ts            # MODIFY: shared week, today, and time helpers
├── pages/
│   ├── Dashboard.tsx           # MODIFY: dominant today section + rest-of-week preview
│   ├── Dashboard.css           # MODIFY: today section as dominant focal point;
│   │                           #         participant inline display; rest-of-week styles
│   ├── WeeklyPlanner.tsx       # MODIFY: weekly overview with today emphasis
│   ├── WeeklyPlanner.css       # MODIFY: today column emphasis; responsive grid
│   ├── Activities.tsx          # MODIFY: list/create/edit/delete/filter; highlight on arrival
│   └── Activities.css          # MODIFY: highlighted item; form and list styles
└── components/
    └── Sidebar.tsx             # MODIFY: navigation labels and footer text
```

**Structure Decision**: All implementation work stays within `ui/`. The Spring Boot backend at `src/` is treated as a read-only contract input.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

| Violation | Why Needed | Simpler Alternative Rejected Because |
| --------- | ---------- | ------------------------------------ |
| None      | —          | —                                    |

## Component Architecture

### Dashboard (`/`)

The dashboard is split into three vertically stacked zones:

```
┌─────────────────────────────────────────────┐
│  Page header + stats strip                   │
├─────────────────────────────────────────────┤
│  [DOMINANT] Heutige Aktivitäten              │  ← highest visual weight on the page
│   ┌──────────────────────────────────────┐   │     accent border, accent background
│   │  Activity name         14:00–15:00   │   │     full participant row per entry
│   │  📍 Ort   [Anna] [Ben] [+1]          │   │     each entry is a click-through button
│   └──────────────────────────────────────┘   │
├─────────────────────────────────────────────┤
│  [SECONDARY] Weitere Aktivitäten            │  ← reduced emphasis, compact list
│  der Woche (Mo–So excl. today)              │     grouped by day label + date
│  smaller text, muted border, no accent bg   │
├─────────────────────────────────────────────┤
│  Two-column: Familie  |  Offene Aufgaben    │
└─────────────────────────────────────────────┘
```

**Today section rules** (FR-002, FR-022, FR-023, FR-024):

- Widest accent border + accent-tinted background — more visual weight than any other section
- Each entry reveals: activity name, time range or "ab HH:mm" (if start-only), location (if set), participant avatar/initial badges for every participant
- If participants > 3: show first 3 badges + `+N` overflow badge so count is always visible
- No activity detail is hidden behind hover or tooltip
- Empty state: German placeholder "Heute keine Aktivitäten geplant."
- Loading/error: standard German state copy

**Rest-of-week section rules** (FR-020, FR-021):

- Rendered below today section as a compact card
- Grouped by day (label + date), only days with activities shown; if no other day has activities, section is omitted entirely
- Activity entries show name and time only — no participant display in this section (secondary information density)
- Muted border and standard surface background (no accent)
- Link to full weekly view at section header

### Weekly Planner (`/week`)

- 7-column grid (Mon–Sun), collapses to 1 column on mobile
- Today column: accent border + accent-soft background, today label in accent colour
- Each day: activity cards with name, time, location, participant badges
- Week always rendered regardless of whether any activities exist, so today is always emphasized

### Activities (`/activities`)

- Header with "Aktivität planen" CTA
- Filter row by participant (pill buttons, "Alle" default)
- Activity list — sorted by day then start time, scoped to current week
- Each item: name, date, time, location, participants, inline edit / delete actions
- Selected-activity arrival (from dashboard): `useLocation().state.selectedId` → `scrollIntoView` + accent highlight ring
- Create form: name (required), date (required), description, location, start time, end time, participant toggles

## Implementation Strategy

### Slice 1 — Dashboard today section (US1 · FR-001/002/003/004/022/023/024)

1. `Dashboard.css`: Add `.dash-today-section` with dominant accent border + accent-soft background; `.dash-act-item` as keyboard-accessible button; `.dash-act-badges` / `.dash-act-badge` for inline badge display; overflow badge for `+N` participants.
2. `Dashboard.tsx`: Derive `todayActivities` from RTK Query result; render today section with full participant rows; click-through via `navigate('/activities', { state: { selectedId } })`.

### Slice 2 — Dashboard rest-of-week preview (US2 · FR-020/021)

1. `Dashboard.css`: Add `.dash-week-preview` section with reduced emphasis — standard border, muted text, compact row height.
2. `Dashboard.tsx`: Derive `otherDayActivities` grouped by `WeekDay`; render only days with at least one activity; omit section entirely when empty; show day label + activity name + time per row.

### Slice 3 — Weekly Planner page (US3 · FR-005/006/007)

1. `WeeklyPlanner.css`: Today column gets accent border + background; all other columns standard. Responsive grid: 7 → 4 → 1 column breakpoints.
2. `WeeklyPlanner.tsx`: Always render all 7 columns so today is always highlighted even when empty; fix `key` props; German states.

### Slice 4 — Create activity (US4 · FR-009)

1. `Activities.tsx`: Create form with required fields, optional fields, participant toggles; German validation messages; RTK mutation with `invalidatesTags`.

### Slice 5 — Detailed management (US5 · FR-008/010/011/012/013)

1. `Activities.tsx`: Inline edit form per item (pre-filled); delete button; participant filter pill row; selected-item scroll + highlight on `state.selectedId` arrival.
2. `Activities.css`: `.act-item-highlighted` accent ring; filter row; edit/delete icon actions.

## Risks

| Risk                                                                         | Likelihood | Impact | Mitigation                                                                                                       |
| ---------------------------------------------------------------------------- | ---------- | ------ | ---------------------------------------------------------------------------------------------------------------- |
| Participant overflow on small screens breaks today section layout            | Medium     | High   | Use flex-wrap badges with `+N` overflow cap; test on 320px viewport                                              |
| Today section and rest-of-week both accent-styled → visual hierarchy unclear | Medium     | High   | Rest-of-week uses standard (non-accent) border and background; today section always has `box-shadow` accent glow |
| Selected activity arrives from dashboard but is no longer in current week    | Low        | Medium | Fall back to top of list with German "nicht mehr verfügbar" notice                                               |
| RTK Query re-fetches on every navigation, causing flash of loading state     | Low        | Medium | Use `keepUnusedDataFor` default (60 s) — already present in existing baseApi                                     |
