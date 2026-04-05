# Research: Weekly Activity Planner

**Phase**: 0 – Pre-Design Research
**Branch**: `002-weekly-activity-planner`
**Date**: 2026-04-04

## 1. Dashboard as Primary Activity Surface — Today Section is the Dominant Focal Point

**Decision**: The dashboard today-activity section is given the highest visual weight on the page — more than the rest-of-week preview, the stats strip, the member list, and the open-tasks section combined. Accent border, accent-tinted background, and elevated shadow are applied to that section only.

**Rationale**: The user requirement is explicit: today's activities are the most important information on the dashboard and must be in clear focus. Lower-emphasis treatment on any other section preserves this hierarchy without needing to remove those sections.

**Alternatives considered**: Give today and rest-of-week equal visual weight.
Rejected because it blurs the priority hierarchy and makes it harder to identify what needs attention right now.

## 4. Participant Visibility — Always Inline, Never Hidden

**Decision**: All participants for a today activity entry are displayed as visible badge/avatar elements directly on the dashboard entry row. If more than a threshold (3) exist, the first 3 are shown plus a `+N` overflow badge. No hover, tooltip, or expand interaction is required to see participant information.

**Rationale**: FR-022, FR-023, and FR-024 all require that participant context be immediately legible from a glance. Hiding participants behind interaction defeats the "clear focus" goal and makes it impossible to attribute activities to family members without additional taps.

**Alternatives considered**: Show participant count only; show participants on hover.
Both rejected because they require interaction to obtain basic context the user needs at a glance.

## 3. Dashboard Rest-of-Week Preview — Secondary Section with Reduced Emphasis

**Decision**: A second dashboard section below the today section shows other days of the current week that have activities. Days with no activities are omitted. If no other day has activities, the section is hidden entirely. The section uses a standard surface (non-accent) background and border.

**Rationale**: Users want to see the week's activity context from the dashboard without leaving it. The section must not compete with today's dominant section — hence the reduced affordance. Omitting empty days keeps the section compact.

**Alternatives considered**: Show all 7 days including empty ones in the preview.
Rejected because it wastes space and introduces noise when most days are inactive.

**Decision**: Dashboard activity entries must carry enough identity to open the corresponding item in the detailed activity management view.

**Rationale**: The user asked for direct redirection from a dashboard activity to the detailed view so the item can be inspected or modified immediately. This requires stable activity identity and a way to preserve which item was selected.

**Alternatives considered**: Open the activities page without any item context.
Rejected because it adds search effort and weakens the dashboard-to-detail workflow.

## 5. Reuse of Shared Date Logic

**Decision**: Reuse one shared date utility for current-day and current-week calculations across dashboard, weekly planner, and activities pages.

**Rationale**: Dashboard, weekly overview, and detailed management all depend on the same interpretation of today's date and the current week. A single utility avoids inconsistent behavior across screens.

**Alternatives considered**: Page-local date calculations.
Rejected because duplicated logic makes highlights and filtering drift over time.

## 6. Existing Activity Contract Is Sufficient

**Decision**: Plan against the current activity contract with stable identity available for list items and item-level actions.

**Rationale**: The detailed view requires identity for edit and delete, and the dashboard requires identity for correct click-through. No additional contract expansion is needed for the planned UX.

**Alternatives considered**: Introduce a separate dashboard-specific activity endpoint.
Rejected because the current list operation already supplies the necessary information.

## 7. State Management Approach

**Decision**: Keep RTK Query for server data and use page-local UI state for selection, edit mode, filters, and temporary form state.

**Rationale**: Activities remain server-authoritative. The selected activity from the dashboard is view state, not shared business state that justifies a new Redux slice.

**Alternatives considered**: Add a dedicated global activity selection store.
Rejected because it adds complexity without clear reuse beyond this feature.

## 8. Styling Direction

**Decision**: Extend existing page-scoped CSS patterns and reuse the dashboard visual language for highlighting today's activities.

**Rationale**: The application already uses page-level CSS with shared variables. Keeping the dashboard daily overview visually consistent with existing cards, section headers, and highlight treatments reduces design drift.

**Alternatives considered**: Introduce a new styling abstraction or dependency.
Rejected because it is unnecessary for this scope and conflicts with the existing project approach.

## 9. German-Only Copy Across All Surfaces

**Decision**: Dashboard, weekly planner, and detailed activities page all use German copy for headings, actions, empty states, loading states, and errors.

**Rationale**: The constitution makes German UI text mandatory across the SPA, and the dashboard addition is part of that same surface.

**Alternatives considered**: Reuse English placeholder copy from prototypes.
Rejected because it violates a non-negotiable product rule.

## Summary of Decisions

| Topic                 | Decision                                                                               |
| --------------------- | -------------------------------------------------------------------------------------- |
| Dashboard entry point | Today section is the visually dominant focal point on the entire dashboard             |
| Participant display   | All participants inline per today entry — no hover, tooltip, or expand required        |
| Rest-of-week preview  | Secondary, non-accent section; omit days with no activities; hide section if all empty |
| Click-through         | Open the detailed activities view for the selected item                                |
| Date logic            | Share one source of truth across all activity surfaces                                 |
| Data contract         | Use current list/create/update/delete activity operations                              |
| State management      | RTK Query plus local UI state only                                                     |
| Styling               | Extend existing dashboard and page-scoped CSS patterns                                 |
| Language              | German-only user-facing copy                                                           |
