# Dashboard SPA Contract

## Purpose

Define the user-facing route contract for the FamilyPlanner dashboard SPA and the backend data dependencies the frontend may consume without extending backend scope.

## Route Contract

### `/`

- **Purpose**: Central dashboard for the active family and current week
- **Must show**:
  - Family summary headline
  - Current-week todo overview
  - Navigation entry points to family management and detailed todos
- **Must handle**:
  - Loading state
  - Empty weekly state
  - Error state
  - Responsive layout from desktop to phone

### `/members`

- **Purpose**: Family management surface
- **Must show**:
  - Current family name
  - Member list with roles
  - Actions to add a member and rename the family
- **Must handle**:
  - Empty family state
  - Save in progress state
  - Validation errors and request errors

### `/tasks`

- **Purpose**: Detailed household todo view
- **Must show**:
  - Full todo list for the active family
  - Filtering by specific family member
  - Distinct treatment of unassigned todos
- **Must handle**:
  - Empty list state
  - Empty filtered state
  - Loading and error state

## State Ownership Contract

- **RTK Query owns**:
  - Request lifecycle for family and todo data
  - Cached backend responses
  - Mutation success and invalidation behavior
- **Redux Toolkit slices own**:
  - Route-adjacent UI state such as active filters, active week selection, and mobile navigation state
  - Form drafts and non-persisted page ergonomics state
- **Component local state owns**:
  - Short-lived uncontrolled interaction state only when it does not affect navigation, caching, or cross-page behavior

## Backend Dependency Contract

The frontend may consume the following backend data categories if they already exist:

- Family collection or active family details
- Family member data including names and roles
- Todo data for the current week and detailed list views

The frontend must not introduce new backend assumptions as implementation requirements. If any of the categories above are missing or incomplete, the gap must be tracked as a blocker or follow-up instead of solved through unapproved backend changes.

## Supported Backend Operations

### Family API

- `GET /api/families`
  - **Response**: array of families with `id`, `name`, and `familyMembers`
- `POST /api/families`
  - **Request**: `{ name }`
  - **Response**: created family object
- `PUT /api/families/{id}`
  - **Request**: `{ name }`
  - **Response**: updated family object
- `POST /api/families/{id}/members`
  - **Request**: `{ name, role }`
  - **Response**: updated family object

### Todo API

- `GET /api/todos?familyId={id}&assigneeId={optional}`
  - **Response**: array of todos for a family, optionally filtered by assignee
- `POST /api/todos`
  - **Request**: `{ name, description, deuDate, familyId, assigneeId }`
  - **Response**: created todo object
- `PUT /api/todos`
  - **Request**: `{ id, name, description, deuDate, completed, assigneeId }`
  - **Response**: updated todo object
- `DELETE /api/todos/{id}`
  - **Response**: no content

## Unsupported Backend Operations

- Family member role update
- Activity or planner endpoints

The frontend must not render API-dependent primary actions for unsupported operations as if they are available.

## No-Login Contract

- No login, sign-up, or protected-route flow is part of this feature.
- The application assumes the user enters directly into the active family dashboard context.
- Future authentication must be able to wrap this shell later without forcing a redesign of route hierarchy or store ownership.

## Look-and-Feel Continuity Contract

- The redesign must preserve the application's current dark, elevated-card visual language.
- Frontend structure may change substantially for usability and responsive behavior, including route-level layout composition, page sections, and component boundaries.
- Ergonomic changes may improve hierarchy, spacing, navigation, and responsive action placement.
- New patterns must feel like an evolution of the existing product, not a separate product line.
- Activity and planner surfaces are outside the current contract and must not be treated as implementation requirements in this increment.
