# Research: Dashboard Week-Focused Layout

## Decision 1: Keep selected-day state local to the dashboard page

- **Decision**: Model the selected dashboard day as component-local state in the dashboard page, initialized from the current local date and constrained to the seven days returned by the shared week utility.
- **Rationale**: The selected day is transient page ergonomics state. It does not need to survive route changes, inform other pages, or alter backend queries. Local state keeps the redesign simple and aligned with the existing page-level interaction model.
- **Alternatives considered**: Store the selected day in Redux or URL state. Rejected because the spec does not require cross-page persistence, deep linking, or global coordination.

## Decision 2: Reuse the existing Wochenübersicht visual pattern as the dashboard week selector

- **Decision**: Base the dashboard week overview on the current `/week` page's day-column and activity-card visual grammar, adding selected-day interaction behavior on top.
- **Rationale**: The user explicitly asked for the dashboard week view to look exactly like the Wochenübersicht. Reusing that existing pattern reduces design drift and implementation risk while meeting the requirement directly.
- **Alternatives considered**: Keep the existing simple dashboard week strip or design a new compact selector. Rejected because both diverge from the requested Wochenübersicht look and provide less weekly context.

## Decision 3: Continue deriving selected-day and weekly groupings client-side from one activity query

- **Decision**: Keep using the current activity list query for the active family and derive both the top selected-day list and the embedded week overview from that single client-side result set.
- **Rationale**: The existing activity API already supports returning family activities, and the current weekly planner filters/groupings are computed client-side. This stays within the approved backend contract and avoids unnecessary query branching.
- **Alternatives considered**: Introduce day-specific queries or new backend filters. Rejected because the spec does not authorize backend changes and the existing data shape is sufficient.

## Decision 4: Remove dashboard family-management and stats content entirely instead of condensing it

- **Decision**: Remove the stats grid, family-member dashboard card, and the separate "Weitere Aktivitäten der Woche" section from the dashboard, leaving only the selected-day detail, week overview, and open-todo overview.
- **Rationale**: The spec calls for a more focused dashboard and explicitly identifies the removed sections as obsolete or unwanted. Partial retention would dilute the primary activity flow and conflict with the requested hierarchy.
- **Alternatives considered**: Keep family or stats content in a collapsed or smaller form. Rejected because the requested scope is removal, not reduction.

## Decision 5: Preserve existing activity click-through behavior from dashboard to detailed activities

- **Decision**: Keep the current route handoff into `/activities` via selected activity ID when a user selects an activity in the top dashboard section.
- **Rationale**: The existing app already supports this flow, and the new dashboard layout still benefits from a direct overview-to-detail transition without adding route complexity.
- **Alternatives considered**: Make the dashboard top section read-only or add an inline details expansion. Rejected because the existing route-based detail page already exists and the spec explicitly preserves activity detail access.
