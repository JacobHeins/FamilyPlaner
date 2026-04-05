# Contract: Dashboard Layout and Interaction

**Feature**: Dashboard Week-Focused Layout  
**Date**: 2026-04-05

## Purpose

Define the user-facing contract for the redesigned dashboard route and the existing data it may consume without extending backend scope.

## Route Contract

### `/`

- **Purpose**: Primary family dashboard centered on the selected day's activities
- **Must show**:
  - A top detail section for the currently selected day
  - An embedded weekly overview that mirrors the existing Wochenübersicht visual pattern
  - An open-todo overview beneath the activity sections
- **Must not show**:
  - Dashboard summary tiles for counts
  - Dashboard family-member management or family-member overview content
  - The separate dashboard section for other activities of the week
- **Must handle**:
  - Current day selected by default
  - User selection of another week day
  - Loading, empty, and error states for activity and todo data
  - Responsive layout from desktop to phone

### `/activities`

- **Purpose**: Detailed activity management view
- **Dashboard dependency**:
  - Must remain reachable from a selected activity in the top dashboard detail section
  - Must accept existing selected activity identity handoff so the clicked activity can be highlighted or brought into view

## State Ownership Contract

- **RTK Query owns**:
  - Active family data
  - Activity list data
  - Todo list data
  - Cached request lifecycle behavior
- **Dashboard page local state owns**:
  - Currently selected dashboard day
  - Short-lived interaction state for the embedded week selector
- **Route navigation state owns**:
  - Selected activity handoff from dashboard to `/activities`

## Data Dependency Contract

The redesigned dashboard may consume only data categories already available in the product:

- Family collection and primary family context
- Activity list data for the active family
- Todo list data for the active family
- Existing activity identity needed for detail-page navigation

If any of these categories are incomplete, the gap must be treated as a blocker or follow-up, not as justification for backend expansion in this feature.

## Interaction Contract

- Selecting a day in the embedded weekly overview updates only the selected-day detail area; it does not change the current route.
- The current day is the default selected day on initial dashboard load.
- The selected-day detail area always reflects the active selection, including empty-state behavior.
- Selecting an activity from the top detail area opens the existing detailed activities page for that activity.

## Localization Contract

- All dashboard labels, headings, loading indicators, empty states, errors, and interactive text must remain in German.

## Unsupported Extensions

- No new backend endpoints or payload requirements
- No new dashboard family-management workflows
- No persistence of selected-day state across browser sessions
