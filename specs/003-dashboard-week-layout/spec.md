# Feature Specification: Dashboard Week-Focused Layout

**Feature Branch**: `003-dashboard-week-layout`  
**Created**: 2026-04-05  
**Status**: Draft  
**Input**: User description: "I want change the layout of the dashboard page. At the top I want to have the overview box of the activities for the day. Below that I want to have the view of the week. It shall look like exactly like the Wochenübersicht view. If I click on another day of the week the activties of the selected day shall be shown in the box showing the activities of the day. So the top box shall always show the avtivities in detail of the selected day. The current day is selected by default. Below hat I only want to have an overview about the open todos. Everything else related to family member management shall be removed. Also the tiles showing the amount of open todos etc shall be removed. Also the tile showing the other activities of the week is obsolet as it is covered with the week overview."

## Scope Boundaries _(mandatory)_

- **In Scope**: Restructuring the dashboard so the first section is a detailed activity box for the currently selected day; adding a dashboard week overview directly below that uses the same visual language and day-grouping concept as the existing Wochenübersicht; allowing users to change the selected dashboard day by choosing another day in the week overview; defaulting the selected day to the current day when the dashboard opens; keeping only an open-todo overview below the activity sections; removing dashboard-only family member management content, dashboard statistic tiles, and the separate "other activities of the week" preview because the week overview replaces that purpose; preserving dashboard navigation into detailed activity management when a specific activity is selected; maintaining German-language content and responsive behavior on the redesigned dashboard.
- **Out of Scope**: Changes to backend contracts, activity creation or editing workflows outside the dashboard selection behavior, changes to the dedicated family management page itself, changes to the dedicated Wochenübersicht page beyond visual reuse expectations, changes to todo business rules, notifications, recurring activity handling, and database or infrastructure work.
- **Backend Impact**: No backend changes authorized. The feature must use existing family, activity, and todo data already available in the product.

## User Scenarios & Testing _(mandatory)_

### User Story 1 - Focus Dashboard on a Selected Day (Priority: P1)

As a family member, I want the top area of the dashboard to show the detailed activities for one selected day, so that I can immediately understand what is planned for that day without scanning the rest of the dashboard.

**Why this priority**: The user explicitly wants the dashboard reorganized around a daily activity detail box, with the current day selected by default. This is the primary purpose of the requested redesign and the main value of the feature.

**Independent Test**: Can be fully tested by opening the dashboard with activities scheduled across the week and confirming that the current day is selected by default, the top box shows the detailed activities for that day, and the detail box updates when a different day is selected from the weekly overview.

**Acceptance Scenarios**:

1. **Given** the dashboard opens and the current week contains activities, **When** the page finishes loading, **Then** the current day is selected by default and the top activity box shows the activities for that day in detail.
2. **Given** the dashboard shows the weekly overview, **When** the user selects another day of the week, **Then** the top activity box updates to show the detailed activities for the newly selected day.
3. **Given** the selected day contains no activities, **When** the top activity box is shown, **Then** it displays a clear German-language empty state for that selected day rather than showing stale data from another day.
4. **Given** the dashboard is reopened or refreshed on the same day, **When** no day has been manually selected yet in that session, **Then** the current day is selected again by default.
5. **Given** the user views the dashboard on a narrow mobile screen, **When** the selected day's activity box is displayed, **Then** the activity details remain readable without horizontal scrolling on the primary content.
6. **Given** an activity in the selected-day detail box has one or more participants, **When** the activity is displayed, **Then** each participant appears as a colour pill showing their full name, using the same role-based colour coding visible on the weekly overview and todo overview.

---

### User Story 2 - Use a Weekly Overview on the Dashboard (Priority: P1)

As a family member, I want the dashboard to include a weekly overview that looks and behaves like the existing Wochenübersicht, so that I can scan the whole week and choose which day I want to inspect in detail.

**Why this priority**: The redesigned dashboard depends on the weekly overview to drive day selection. The user explicitly requested that this section should look exactly like the existing weekly overview and replace the older secondary activity preview.

**Independent Test**: Can be fully tested by opening the dashboard, comparing the week overview structure to the existing Wochenübersicht, and confirming that each day is visible in the same familiar overview style and can be used to switch the selected day.

**Acceptance Scenarios**:

1. **Given** the dashboard loads successfully, **When** the weekly overview is displayed, **Then** it presents the current week as a day-by-day overview using the same visual pattern as the existing Wochenübersicht.
2. **Given** the current day is selected by default, **When** the weekly overview is shown, **Then** the current day is visibly highlighted within the overview.
3. **Given** activities exist on multiple days of the current week, **When** the user scans the weekly overview, **Then** activities are shown under their corresponding days so the user can understand the week at a glance.
4. **Given** the user selects a day from the weekly overview, **When** the selection is applied, **Then** that day becomes the active dashboard selection and the overview reflects that active state.
5. **Given** the current week has no activities at all, **When** the dashboard loads, **Then** the weekly overview still renders the week structure and shows German-language empty states instead of disappearing.

---

### User Story 3 - Simplify the Remaining Dashboard Content (Priority: P2)

As a family member, I want the rest of the dashboard to contain only the open-todo overview below the activity sections, so that the dashboard stays focused and no longer mixes in unrelated family-management and summary tiles.

**Why this priority**: After the activity-focused redesign, the remaining dashboard content should be intentionally minimal. The user explicitly asked to remove family member management content, statistic tiles, and the redundant weekly activity preview.

**Independent Test**: Can be fully tested by opening the dashboard after the redesign and confirming that below the day detail box and weekly overview there is only the open-todo overview, while the family member section, summary tiles, and separate week-activity preview are absent.

**Acceptance Scenarios**:

1. **Given** the redesigned dashboard is displayed, **When** the user scrolls below the activity sections, **Then** the only remaining dashboard content section is the open-todo overview.
2. **Given** the dashboard previously displayed family member management content, **When** the redesigned dashboard loads, **Then** that family-related section is no longer present on the dashboard.
3. **Given** the dashboard previously displayed summary tiles for open todos and other counts, **When** the redesigned dashboard loads, **Then** those statistic tiles are no longer present.
4. **Given** the dashboard previously displayed a separate "other activities of the week" area, **When** the redesigned dashboard loads, **Then** that section is no longer present because the week overview already covers that purpose.
5. **Given** open todos exist for the current family, **When** the todo overview is shown, **Then** users can still see their open todos without needing the removed summary tiles.

---

### User Story 4 - Open Activity Details from the Selected Day (Priority: P3)

As a family member, I want to open an activity from the selected day's detail box, so that I can move from the dashboard overview into the full activity details when I need to inspect or modify something.

**Why this priority**: The request focuses primarily on layout, but the selected-day detail area remains most useful if it preserves the existing ability to navigate into activity details.

**Independent Test**: Can be fully tested by selecting a day on the dashboard, choosing one of that day's activities from the top detail box, and confirming that the detailed activity view opens for that same activity.

**Acceptance Scenarios**:

1. **Given** the top activity box shows one or more activities for the selected day, **When** the user selects a specific activity, **Then** the application opens the detailed activity view for that activity.
2. **Given** the selected day is changed in the week overview, **When** the user chooses an activity from the updated top box, **Then** the navigation opens the activity that belongs to the newly selected day rather than an activity from the previously selected day.

### Edge Cases

- If the dashboard opens before family data is available, the page must avoid showing an incorrect selected day state and must show a clear loading or empty state instead.
- If the selected day has no activities but other days in the week do, the top activity box must still show the selected day's empty state rather than automatically switching to another populated day.
- If the current week spans a month boundary, the weekly overview must still group activities under the correct visible day columns.
- If the user selects a different day and the activity data refreshes afterward, the selected day must remain stable as long as that day is still within the current week.
- If there are no open todos, the todo overview must still render a clear German-language empty state rather than leaving a blank area below the week overview.
- If the activity selected from the top box is no longer available by the time the detailed view opens, the user must receive a clear German-language message in the detailed activity area.
- If the current day has no activities, it must still be selected by default unless the user actively chooses another day.
- The dashboard must remain understandable when a selected day contains many activities and the weekly overview also shows activity cards for all seven days.

## Requirements _(mandatory)_

### Functional Requirements

- **FR-001**: The system MUST restructure the dashboard so the first primary section is a detailed activity overview for a single selected day.
- **FR-002**: The system MUST select the current day by default when the dashboard initially loads.
- **FR-003**: Users MUST be able to change the selected day directly from the dashboard's weekly overview.
- **FR-004**: When the selected day changes, the top activity overview MUST update to show the activities for that selected day only.
- **FR-005**: The dashboard weekly overview MUST represent the current week using the same visual language and day-based organization as the existing Wochenübersicht.
- **FR-006**: The dashboard weekly overview MUST visibly indicate both the current day and the actively selected day.
- **FR-007**: The selected-day activity overview MUST show a German-language empty state when no activities exist for the selected day.
- **FR-008**: The weekly overview on the dashboard MUST continue to render all days of the current week even when some or all days have no activities.
- **FR-009**: Users MUST be able to open the detailed activity view from an activity shown in the selected-day overview.
- **FR-010**: The dashboard MUST remove the separate secondary section that previews other activities of the week.
- **FR-011**: The dashboard MUST remove summary tiles that display counts such as open todos, activities today, or family member totals.
- **FR-012**: The dashboard MUST remove family-member-management content and family-member overview content from the dashboard surface.
- **FR-013**: Below the selected-day activity overview and weekly overview, the dashboard MUST retain only an overview of open todos as its remaining content section.
- **FR-014**: The open-todo overview MUST continue to show meaningful open-task information without relying on the removed statistic tiles.
- **FR-015**: The dashboard MUST provide explicit German-language loading, empty, and error states for activity data and todo data.
- **FR-016**: All user-facing dashboard text, including headings, labels, empty states, and error messages, MUST be written in German.
- **FR-017**: The redesigned dashboard MUST remain usable across desktop, tablet, and smartphone screen sizes without horizontal scrolling on primary content.
- **FR-018**: The feature MUST stay within the approved interface scope and reuse currently available family, activity, and todo data rather than introducing new backend requirements.
- **FR-019**: Each participant shown in the selected-day activity overview MUST be rendered as a full-name colour pill using the same visual style as participants are shown in the weekly overview and assignees are shown in the todo overview — including the role-based colour coding applied consistently across the product.

### Key Entities _(include if feature involves data)_

- **Selected Dashboard Day**: The day in the current week that drives which activities are shown in detail at the top of the dashboard; defaults to the current day until the user selects another day.
- **Selected-Day Activity Overview**: The top dashboard section that shows the detailed list of activities for the selected dashboard day, including the state where that day has no activities.
- **Dashboard Week Overview**: A dashboard-embedded weekly overview that mirrors the existing Wochenübersicht layout and allows the user to select a day.
- **Open Todo Overview**: The remaining dashboard content section below the activity areas, showing open tasks that still need attention.
- **Activity**: A scheduled family event with a day assignment and optional descriptive details that can be opened from the dashboard for further inspection.

## Success Criteria _(mandatory)_

### Measurable Outcomes

- **SC-001**: Users can identify the currently selected day on the dashboard within 3 seconds of the page becoming visible.
- **SC-002**: Users can switch from the default current day to another day in the same week and see the top activity box update in a single day-selection interaction.
- **SC-003**: Users can determine the detailed activities for the selected day within 5 seconds of opening the dashboard.
- **SC-004**: Users can identify the weekly activity distribution for the current week within 10 seconds of viewing the dashboard.
- **SC-005**: Users can find the open-todo overview below the activity sections without encountering unrelated family-management content or statistic tiles.
- **SC-006**: At least 90% of acceptance-test runs for the dashboard redesign scenarios complete successfully across desktop and mobile-sized layouts.
- **SC-007**: A German-speaking user can complete the primary dashboard flows without encountering English-language text.

## Assumptions

- Implementation is limited to the already approved user-interface scope for this product.
- Existing activity data already supports showing activities by day within the current week and opening an activity in the detailed activity view.
- Existing todo data already supports rendering the open-todo overview without backend changes.
- The family management page remains available elsewhere in the application even though family-related content is removed from the dashboard.
- The dashboard can reuse the established Wochenübersicht presentation pattern without changing the underlying weekly activity business rules.
- The current day is derived from the user's local date.
- The redesigned dashboard continues to use the current family context as the source for activities and todos.
