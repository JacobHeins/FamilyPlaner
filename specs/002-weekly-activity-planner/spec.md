# Feature Specification: Weekly Activity Planner

**Feature Branch**: `002-weekly-activity-planner`  
**Created**: 2026-04-04  
**Status**: Draft  
**Input**: User description: "I want to add the ability to plan activities for the upcoming week. I want to have an overview about the activities in the week and highlights what is important today. I also want to have a detailed view where I can see details about all activities. Here I also want to have the possibility to plan new activities for the current week." Updated review request: "I don't see any activities on the dashboard. This is the main entry point and always visible. Here I want to have an overview about the daily activities. And the activities for the current day shall be highlighted. If I click on an activity I want to be redirected to the detailed view so I can see details or modify the activity." Updated requirements: "As a family member I also want to see which activities are planned the others day in the week. I also want to see that on the dashboard but not as prominent as for the current day." Updated requirements: "As a family member the Events for the current Day are the most important thing on the dashboard. So they should be in clear focus, easy to understand and very clear how is part of which activity."

## Scope Boundaries _(mandatory)_

- **In Scope**: A dashboard daily-activity overview on the application's main entry page that is the visually dominant focal point of the entire dashboard; direct display of each activity's participants within the dashboard today-activity entries so the user can see at a glance who is attending; a secondary dashboard section showing all other days of the current week and their activities with reduced visual emphasis compared to the current-day overview; a weekly activity overview page that highlights today's activities and shows the full week at a glance; a detailed activity management page that lists all activities for the current week with full details; the ability to plan (create) new activities for the current week; the ability to edit and delete existing activities; direct navigation from dashboard activity entries into the detailed activity view for the selected activity; a German-language user interface on all activity-related pages.
- **Out of Scope**: Changes to underlying activity storage, calendar synchronization, recurring activities, notifications or reminders, historical activity reporting beyond the current week, and activity categorization or tagging features.
- **Backend Impact**: Activity information available to users must support opening and managing a specifically selected activity from overview surfaces, including the dashboard.

## User Scenarios & Testing _(mandatory)_

### User Story 1 - View Today's Activities on the Dashboard (Priority: P1)

As a family member, I want the main dashboard to give today's activities strong visual focus and show me immediately who is part of each activity, so that from the very first screen I open I can see exactly what is happening today and for whom.

**Why this priority**: The dashboard is the application's primary entry point and always visible during regular use. Today's activities are the single most important piece of information a family member needs on arrival — they must be unmistakable, instantly readable, and complete enough to act on without any further navigation.

**Independent Test**: Can be fully tested by opening the dashboard with activities scheduled for today and confirming that today's activities appear as the most visually dominant section, each entry shows the activity name, optional time, and all participants without any additional interaction, and selecting an activity opens the detailed view.

**Acceptance Scenarios**:

1. **Given** there are activities scheduled for the current day, **When** the user opens the dashboard, **Then** the dashboard shows those activities in a dedicated daily overview area without requiring navigation to another page first.
2. **Given** the current day has one or more activities, **When** the dashboard loads, **Then** the current day's activity section is visually the most dominant section on the page so it stands out from all surrounding content including the rest-of-week preview.
3. **Given** the user selects an activity from the dashboard overview, **When** the selection is made, **Then** the application opens the detailed activity view for that same activity so the user can inspect or modify it.
4. **Given** there are no activities scheduled for the current day, **When** the user opens the dashboard, **Then** the daily overview shows an explicit empty state in German indicating that no activities are planned for today.
5. **Given** the user opens the dashboard on a narrow mobile screen, **When** the page loads, **Then** the daily activity overview remains readable and tappable without horizontal scrolling on the primary content.
6. **Given** a today activity has one or more participants, **When** the user views the dashboard, **Then** the names or visual representations of all participants are displayed directly on the activity entry so it is immediately clear who is attending without any additional interaction.
7. **Given** a today activity entry is shown on the dashboard, **When** the user glances at it, **Then** the activity name, scheduled time (if set), and all participants are each immediately legible as a single entry — none of them hidden behind a hover, tooltip, or secondary tap.
8. **Given** multiple today activities are shown on the dashboard, **When** each entry is viewed, **Then** the participants for each activity are displayed with that activity so a family member can distinguish who attends which activity at a glance.

---

### User Story 2 - View the Rest of the Week's Activities on the Dashboard (Priority: P2)

As a family member, I want to see which activities are planned for the other days of the current week directly on the dashboard, but in a less prominent way than today's activities, so that I can get a quick sense of the week ahead without leaving my main overview.

**Why this priority**: Today's activities are the highest-priority dashboard concern; the rest of the week provides useful context but should not compete visually with what needs attention right now. This makes it secondary to the current-day overview.

**Independent Test**: Can be fully tested by opening the dashboard with activities scheduled on multiple days of the current week and confirming that other days' activities appear on the dashboard in a visibly secondary area, while today's section remains the primary focal point.

**Acceptance Scenarios**:

1. **Given** there are activities on multiple days of the current week, **When** the user opens the dashboard, **Then** activities for the other days of the week are visible on the dashboard without requiring navigation away.
2. **Given** the dashboard shows today's activities prominently, **When** activities exist on other days of the week, **Then** those other-day activities are rendered with visually reduced emphasis so today's section remains the primary focal point.
3. **Given** there are no activities on any other day of the current week, **When** the user opens the dashboard, **Then** the rest-of-week section is either hidden or shows a compact, unobtrusive indication that no other activities are planned.
4. **Given** the user views the dashboard on a narrow mobile screen, **When** other-day activities are present, **Then** the rest-of-week summary remains readable without horizontal scrolling or layout breakage.

---

### User Story 3 - View Weekly Activity Overview with Today's Highlights (Priority: P1)

As a family organizer, I want a weekly activity overview page that immediately shows me what activities are happening this week and clearly highlights what is on the schedule for today, so that I can stay on top of daily commitments at a glance.

**Why this priority**: The weekly view complements the dashboard summary by turning the full week into a scannable plan. It remains core to activity awareness but is secondary to the dashboard entry-point experience.

**Independent Test**: Can be fully tested by opening the weekly activity overview page with activities scheduled across the current week and confirming that today's activities are visually distinguished from activities on other days, and that the full week is represented as a structured overview.

**Acceptance Scenarios**:

1. **Given** the current week has activities on multiple days, **When** the user opens the weekly activity overview, **Then** the page shows all activities grouped or organized by day for the entire current week.
2. **Given** there are activities scheduled for today, **When** the user opens the weekly activity overview, **Then** today's activities are visually highlighted and immediately distinguishable from activities on other days.
3. **Given** today has no activities scheduled, **When** the user opens the weekly activity overview, **Then** today's section shows an explicit empty state in German indicating no activities are planned for today.
4. **Given** there are no activities in the entire current week, **When** the user opens the weekly activity overview, **Then** the page shows an explicit empty state in German indicating no activities are planned for this week.
5. **Given** the user opens the weekly activity overview on a narrow mobile screen, **When** the page loads, **Then** the weekly overview remains readable and usable without horizontal scrolling on the primary content.

---

### User Story 4 - Plan a New Activity for the Current Week (Priority: P2)

As a family organizer, I want to create new activities directly from the detailed activity page so that I can quickly schedule something new for the family without leaving the app.

**Why this priority**: Creating activities is the core planning action of this feature. Without it, the planner is read-only and cannot fulfill its primary purpose of enabling families to organize their week.

**Independent Test**: Can be fully tested by opening the detailed activity page, submitting the new activity form with valid data, and confirming the created activity immediately appears in the list without a page reload.

**Acceptance Scenarios**:

1. **Given** the user is on the detailed activity page, **When** the user opens the create activity form and fills in a name and date within the current week, **Then** the new activity is saved and appears in the activity list.
2. **Given** the user submits a new activity with all optional fields filled (description, location, start time, end time, participants), **When** the form is submitted, **Then** the activity appears with all entered details visible in the list.
3. **Given** the user submits the form without a required field (name or date), **When** submission is attempted, **Then** a clear German-language validation message is shown and the activity is not created.
4. **Given** the user adds participants to a new activity, **When** the activity is saved, **Then** the listed participants are shown on the activity detail within the list.

---

### User Story 5 - View and Manage All Activities in Detail (Priority: P3)

As a family organizer, I want a detailed activity management page where I can see the full details of every activity for the current week, edit existing activities, and delete ones that are no longer relevant, so that I can keep the family schedule accurate and up to date.

**Why this priority**: Full management of activities deepens the usefulness of the planner. Editing and deleting depend on the overview and creation workflows established in higher-priority stories.

**Independent Test**: Can be fully tested by opening the detailed activity page, verifying that all activities for the current week appear with complete information, editing one activity to change its name and time, and deleting another activity, confirming all changes reflect immediately in the list.

**Acceptance Scenarios**:

1. **Given** the current week has multiple activities, **When** the user opens the detailed activity page, **Then** each activity shows its name, date, start and end time, location, and assigned participants.
2. **Given** an existing activity is shown in the list, **When** the user opens the edit form for that activity, **Then** all current values (name, description, location, day, start time, end time, participants) are pre-filled in the form.
3. **Given** the user changes one or more fields and saves, **When** the update is submitted, **Then** the activity list reflects the updated values immediately without a full page reload.
4. **Given** the user clicks delete on an activity, **When** the deletion is confirmed by the backend, **Then** the activity is removed from the list immediately.
5. **Given** the user filters activities by a specific family member as participant, **When** the filter is applied, **Then** only activities where that member is listed as a participant are shown.
6. **Given** no family member filter is selected, **When** the detailed activity page loads, **Then** all current-week activities for the family are shown.

---

### Edge Cases

- The weekly overview must correctly identify "today" based on the user's local date, not a server-side date.
- Activities scheduled at the boundary of the week (Monday and Sunday) must appear correctly in the weekly overview.
- If two activities have similar names on the dashboard, selecting one must still open the correct activity in the detailed activity view.
- An activity with no participants must be displayed clearly with a German-language placeholder rather than a blank participants field.
- An activity with no start or end time must still be displayed meaningfully (e.g., shown as an all-day or unscheduled entry).
- If an activity has a location but no start time, both pieces of information must remain accessible in the detailed view.
- When a today activity has many participants, the dashboard entry must still communicate all participants or display a count summary, so the participant context is never entirely hidden.
- If a dashboard activity is selected but is no longer available by the time the detailed view opens, the user must still land in the detailed activity area and receive a clear German-language message that the selected activity is no longer available.
- Given the dashboard shows the rest-of-week section and no activities are planned for any other day of the current week, the section must hide gracefully or show an unobtrusive state without cluttering the dashboard layout.
- When a filter by family member is active and that member is later removed from the participants list via an edit, the filter must recover gracefully and return the user to the full unfiltered view or an appropriate empty state.
- If the backend returns an error when creating, updating, or deleting an activity, a clear German-language error message must be displayed to the user.
- All German-language text must cover labels, button captions, placeholder text, empty states, validation messages, and error messages.

## Requirements _(mandatory)_

### Functional Requirements

- **FR-001**: The system MUST provide a daily activity overview on the main dashboard that shows the current day's activities without requiring the user to navigate away from the dashboard.
- **FR-002**: The current day's activity section on the dashboard MUST be the most visually dominant section on the page, receiving more visual weight than all other dashboard sections — including the rest-of-week preview — so that it is unmistakably the primary focal point.
- **FR-003**: Users MUST be able to select an activity from the dashboard overview and open the detailed activity view for that same activity.
- **FR-004**: The system MUST show an explicit German-language empty state on the dashboard when no activities exist for the current day.
- **FR-005**: The system MUST provide a weekly activity overview page that presents the current week's activities organized by day.
- **FR-006**: The system MUST visually highlight activities scheduled for the current day within the weekly overview so they are immediately distinguishable from other days.
- **FR-007**: The system MUST show explicit German-language empty states when no activities exist for today or for the full current week.
- **FR-008**: The system MUST provide a detailed activity page that shows each activity's name, date, start time, end time, location, and participants.
- **FR-009**: Users MUST be able to create a new activity for the current week by providing at minimum a name and a date; description, location, start time, end time, and participants are optional.
- **FR-010**: Users MUST be able to edit an existing activity's name, description, location, day, start time, end time, and participants from the detailed activity page.
- **FR-011**: When editing an activity, all current values MUST be pre-filled in the edit form so the user modifies only what is necessary.
- **FR-012**: Users MUST be able to delete an activity from the detailed activity page; the activity must be removed from the list immediately after confirmed deletion.
- **FR-013**: Users MUST be able to filter the detailed activity list by a specific family member to see only activities in which that member participates.
- **FR-014**: The system MUST provide explicit loading, empty, and error states for the dashboard daily overview, the weekly overview, and the detailed activity page.
- **FR-015**: The system MUST provide navigation between the dashboard overview, the weekly activity overview, the detailed activity page, and the rest of the application.
- **FR-016**: All user-facing text, including labels, button captions, placeholder text, navigation items, form field hints, validation messages, empty states, and error messages, MUST be written in German.
- **FR-017**: The system MUST display activities with no assigned participants using a clear German-language placeholder so unparticipated activities are not mistaken for missing data.
- **FR-018**: The system MUST remain responsive and usable across desktop, tablet, and smartphone screen sizes for the dashboard overview, weekly overview, and detailed activity page.
- **FR-019**: Activities shown on overview surfaces MUST remain uniquely identifiable so the application can open the correct detailed activity view for the selected item.
- **FR-020**: The system MUST display a rest-of-week section on the dashboard showing activities for all other days of the current calendar week, so that users can see the week's activity context at a glance. Each entry in this section displays activity name and scheduled time only; location and participant details are omitted to preserve reduced information density.
- **FR-021**: The rest-of-week section on the dashboard MUST be rendered with visually reduced emphasis compared to the current day's activity overview to preserve today as the primary focal point.
- **FR-022**: Each today activity entry on the dashboard MUST display the names or visual representations of all assigned participants directly and visibly on the entry, so the user can determine at a glance who is attending that specific activity without opening it.
- **FR-023**: Each today activity entry on the dashboard MUST present the activity name, scheduled time (if available), and participant information as immediately legible content — none of these fields may be hidden behind a hover state, tooltip, or secondary interaction.
- **FR-024**: When a today activity has many participants, the dashboard entry MUST still communicate all participants or show a summary count so the participant context is never completely absent from the activity entry.

### Key Entities _(include if feature involves data)_

- **Activity**: A scheduled family event with a name, an optional description, an optional location, a required calendar day, optional start and end times, and a list of participating family members.
- **Dashboard Daily Activity Overview**: A dashboard section that is the most visually dominant area on the page, surfacing the current day's activities in full detail — including name, time, and a clear per-activity display of who is attending — and enabling the user to open the detailed view for any selected activity.
- **Dashboard Rest-of-Week Preview**: A secondary dashboard section that shows activities for all other days of the current week in a compact, lower-emphasis format, giving users a full-week context from the main entry page.
- **Weekly Activity Overview**: A view of all activities grouped by day for the current calendar week, with today's day visually emphasized.
- **Activity Participant**: A family member associated with a specific activity, representing attendance or assignment.

## Success Criteria _(mandatory)_

### Measurable Outcomes

- **SC-001**: Users can identify today's activities within 5 seconds of opening the dashboard.
- **SC-002**: Users can open the detailed view for a selected dashboard activity in a single interaction.
- **SC-003**: Users can identify today's activities within 10 seconds of opening the weekly activity overview.
- **SC-004**: Users can plan a new activity by filling in the form and submitting it in under 2 minutes from the detailed activity page.
- **SC-005**: Users can edit an existing activity and save changes in no more than 5 interactions from the detailed activity page once the activity is open.
- **SC-006**: Users can filter the detailed activity list to a single family member in no more than 3 interactions.
- **SC-007**: The dashboard overview, weekly overview, and detailed activity page remain fully usable on desktop, tablet, and smartphone screen sizes without loss of core information or blocked primary actions.
- **SC-008**: At least 90% of acceptance-test runs for the five primary user stories complete successfully.
- **SC-009**: A German-speaking user can complete any primary activity planning workflow without encountering English-language labels or system messages.
- **SC-010**: Users can identify which other days of the current week have activities planned within 10 seconds of opening the dashboard.
- **SC-011**: Users can identify all participants of every today activity shown on the dashboard in a single glance, without any hover, tap, or expand interaction.

## Assumptions

- The application already has a working family context with at least one family and family members available.
- The available activity data and management capabilities are sufficient to create, review, update, and remove activities for this feature.
- An activity's "current week" is calculated from Monday to Sunday of the user's local calendar week.
- "Today" is determined by the user's local device date, not a server-side timestamp.
- The dashboard can present a current-day activity summary without replacing the separate weekly overview and detailed activity management surfaces.
- Opening an activity from the dashboard lands the user in the detailed activity area with the selected activity immediately visible, focused, or otherwise easy to find.
- Activities that span overnight (end time before start time) are treated as ending on the same day for display purposes; cross-day spanning is out of scope for this feature.
- Participants are selected from the family's existing members; inviting external people is out of scope.
- The German-language requirement applies to all activity-related pages and is consistent with the language policy established in the family dashboard feature (001).
- Start time and end time are both optional; activities without a time are shown as unscheduled entries on the correct day.
- The application can add this feature without changing the underlying family-planning concept or the user's existing navigation model.
- The rest-of-week section on the dashboard covers all days of the current week excluding today; whether past days of the current week are displayed alongside future days is a layout decision, not a business rule, and both approaches are acceptable.
