# Feature Specification: Family Dashboard UI

**Feature Branch**: `001-family-dashboard`  
**Created**: 2026-03-31  
**Status**: Draft  
**Input**: User description: "I want to build a dashboard ui for my family planer app. I want to have a modern looking UI that is also responsive so I can access it from different devices, from PC to mobile devices like tablets or smartphones. I want to have a central dashboard where I get a good overview about my family and what is coming up in the current week. I want to have an overview about the todos for the current week. I also want to have a new page to manage my family. I want to add family members. I also want to have a detailed view for todos. I want to have a detailed overview about the family todos but I also want to filter for todos that are assigned to one specific family member. Activities are coming later and are out of scope for now. The frontend should only use the current backend API as it exists today."

## Scope Boundaries _(mandatory)_

- **In Scope**: A responsive dashboard homepage, a weekly overview of upcoming todos, a family management page with currently supported family and member actions, a detailed todo page with filtering by family member, a German-language user interface throughout all pages, and a prominently visible assignee indicator on every todo item.
- **Out of Scope**: Backend behavior changes, database changes, authentication redesign, notification delivery, activity or event planning, calendar synchronization, historical reporting beyond the current week, member role changes, and multi-language switching or locale selection.
- **Backend Impact**: No backend changes authorized. The feature must use only the currently available family and todo endpoints and their existing request and response payloads.

## User Scenarios & Testing _(mandatory)_

### User Story 1 - Review the Week at a Glance (Priority: P1)

As a family organizer, I want a central dashboard that highlights my family summary and the current week's upcoming todos so that I can understand what needs attention without visiting multiple pages.

**Why this priority**: This is the main entry point of the application and delivers the highest immediate value by turning the app into a useful weekly planning dashboard.

**Independent Test**: Can be fully tested by opening the dashboard on desktop and mobile-sized screens and confirming that the family summary and current-week todos are clearly visible and understandable without navigating elsewhere.

**Acceptance Scenarios**:

1. **Given** current-week todos exist, **When** the user opens the dashboard, **Then** the dashboard shows a concise family overview plus the relevant todo items scheduled for the current week.
2. **Given** there are no todos for the current week, **When** the user opens the dashboard, **Then** the dashboard shows an explicit empty state indicating that no todo items are planned.
3. **Given** the user opens the dashboard on a narrow screen, **When** the page loads, **Then** the same weekly overview remains available in a layout that fits the device without horizontal scrolling for primary content.

---

### User Story 2 - Manage Family Details (Priority: P2)

As a family organizer, I want a dedicated family management page so that I can view my household details, rename my family, and add family members without leaving the app or using admin tools.

**Why this priority**: Family structure drives who appears across the rest of the experience, but the application still provides value without management tools if the dashboard exists first.

**Independent Test**: Can be fully tested by opening the family page and confirming that the user can view current family information, create a family if needed, rename the family, and add a member through a complete management workflow.

**Acceptance Scenarios**:

1. **Given** an existing family is loaded, **When** the user opens the family management page, **Then** the current family name, members, and roles are displayed.
2. **Given** the user enters a valid new name for an existing family, **When** the user submits the rename, **Then** the updated family name appears in the family management view.
3. **Given** the user enters valid information for a new family member, **When** the user submits the addition, **Then** the member appears in the family overview.
4. **Given** no family exists yet, **When** the user submits a valid family name, **Then** a new family appears in the family management view and can be used for adding members.

---

### User Story 3 - Review, Filter, and Manage Family Todos (Priority: P3)

As a family organizer, I want a detailed todo page with family-member filtering and full todo management so that I can inspect all household work, focus on one person's assignments, update todo details, and remove completed or obsolete tasks.

**Why this priority**: Detailed task management deepens the usefulness of the dashboard, but it depends on the dashboard and family overview already establishing the core navigation and context.

**Independent Test**: Can be fully tested by opening the todo page, confirming that all todos are listed in detail, applying a family-member filter, editing a todo's name, description, due date, or assignee, and deleting a todo.

**Acceptance Scenarios**:

1. **Given** the family has multiple todos, **When** the user opens the detailed todo page, **Then** the page shows a full list with enough detail to distinguish status, assignee, and timing.
2. **Given** todos are assigned to multiple family members, **When** the user filters by one member, **Then** only todos assigned to that member are shown.
3. **Given** the selected family member has no assigned todos, **When** the filter is applied, **Then** the page shows an explicit empty result state rather than a blank list.
4. **Given** an existing todo is displayed, **When** the user opens the inline edit form, **Then** the current name, description, due date, and assignee are pre-filled and editable.
5. **Given** the user changes one or more fields and saves, **When** the update is submitted, **Then** the todo list reflects the saved changes without a page reload.
6. **Given** the user clicks delete on a todo, **When** the deletion is confirmed by the backend, **Then** the todo is removed from the list immediately.
7. **Given** a todo has an assigned family member, **When** the user views the todo list, **Then** the assignee's name is displayed prominently and visually distinguishable from secondary metadata such as due date or description.

### Edge Cases

- A newly created or empty family can be managed even when no members exist yet.
- The dashboard and todo views must communicate clearly when no todos are scheduled for the current week.
- Unassigned todos must remain visible and clearly labeled so they are not mistaken for missing data.
- If a previously selected member filter is no longer valid, the detailed todo view must recover gracefully and return the user to a meaningful default state.
- Long names and dense weekly todo lists must remain readable and actionable on smaller screens.
- The family management page must clearly communicate that member role changes are not available in the current API-backed increment.
- All user-facing labels, button text, placeholder text, status messages, empty states, and error messages must be written in German.
- Todos with an assigned member must display the assignee name in a way that is immediately scannable without requiring the user to read surrounding metadata first.

## Requirements _(mandatory)_

### Functional Requirements

- **FR-001**: The system MUST provide a central dashboard page that summarizes the family and the current week's upcoming todos.
- **FR-002**: The system MUST present current-week todos in the dashboard overview.
- **FR-003**: The system MUST provide a responsive interface that remains usable across desktop, tablet, and smartphone screen sizes.
- **FR-004**: The system MUST provide a dedicated family management page.
- **FR-005**: Users MUST be able to add a family member from the family management page.
- **FR-006**: Users MUST be able to rename a family from the family management page.
- **FR-007**: Users MUST be able to create a family from the family management page when no suitable family exists yet.
- **FR-008**: The system MUST provide a detailed todo view that shows the family's todos beyond the condensed dashboard summary.
- **FR-009**: Users MUST be able to filter the detailed todo view by one specific family member.
- **FR-010**: The system MUST provide explicit loading, empty, and error states for the dashboard, family management page, and detailed todo view.
- **FR-011**: The system MUST preserve clear navigation between the dashboard, family management page, and detailed todo view.
- **FR-012**: The system MUST display unassigned todos in a way that distinguishes them from member-assigned todos.
- **FR-013**: The system MUST keep the feature within the approved interface scope and treat missing or insufficient household data as a documented dependency rather than silently expanding the feature scope.
- **FR-014**: The system MUST use only the currently available backend operations: list families, create a family, update a family name, add a family member, list todos by family with optional assignee filter, create a todo, update a todo, and delete a todo.
- **FR-015**: The frontend MAY be restructured as needed to improve usability, navigation clarity, and responsiveness, provided the application keeps a recognizably similar visual style.
- **FR-016**: Users MUST be able to edit an existing todo's name, description, due date, and assignee from the detailed todo view using an inline edit form.
- **FR-017**: Users MUST be able to delete an existing todo from the detailed todo view.
- **FR-018**: When editing a todo, the current values of all editable fields MUST be pre-filled so the user only changes what is necessary.
- **FR-019**: All user-facing text in the interface — including labels, button captions, placeholder text, navigation items, empty states, and error messages — MUST be written in German.
- **FR-020**: Every todo item MUST display the assignee's name as a visually prominent element, clearly distinguishable from secondary information such as due date or description. Unassigned todos MUST display an explicit German-language placeholder instead of leaving the assignee field blank.

### Key Entities _(include if feature involves data)_

- **Family**: The household being managed, including its display name and the members associated with it.
- **Family Member**: A person within the family, including their name, role, and assignment relationship to todos.
- **Todo**: A family task with summary details, timing or due-period relevance, assignment status, and completion state.
- **Weekly Todo Overview**: A dashboard-visible summary of current-week todo items that helps highlight what is coming up next.

## Success Criteria _(mandatory)_

### Measurable Outcomes

- **SC-001**: Users can understand the current week's upcoming todos from the dashboard within 30 seconds of opening the app.
- **SC-002**: Users can complete the currently supported family management tasks of creating a family, renaming a family, or adding a member in under 2 minutes per task.
- **SC-003**: Users can narrow the detailed todo view to a single family member in no more than 3 interactions.
- **SC-004**: The primary pages remain usable on desktop, tablet, and smartphone-sized screens without loss of core information or blocked primary actions.
- **SC-005**: At least 90% of acceptance-test runs for the primary user stories complete successfully without requiring backend changes.
- **SC-006**: Users can open the inline edit form for a todo, change any field, and save the update in no more than 5 interactions.
- **SC-007**: A German-speaking user can complete any primary workflow without encountering English-language labels or system messages.
- **SC-008**: A user scanning the todo list can identify who is assigned to each todo within 5 seconds without reading secondary metadata.

## Assumptions

- The application serves one actively managed family context at a time for this feature.
- Implementation is limited to the existing web interface unless the user later authorizes backend work.
- Existing backend services already provide or will provide access to family details, family members, and todos without requiring scope expansion in this feature.
- Activity planning will be specified in a later feature increment and is intentionally excluded from the current dashboard scope.
- Family rename is supported in the current backend API, while member-role update capabilities remain excluded because the backend does not yet expose that operation.
- Frontend route structure, page composition, and component boundaries may change during implementation if that improves usability and responsive behavior without changing the established visual language.
- Users accessing the dashboard have typical internet connectivity and expect a modern responsive interface across common device sizes.
- The application targets German-speaking households; German is the sole UI language for this feature increment. Multi-language support or language switching is out of scope.
- Assignee highlighting applies to all surfaces where todos are displayed, including the dashboard summary and the detailed todo page.
