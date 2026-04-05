# Data Model: Family Dashboard UI

## Overview

This feature uses backend-provided household data plus frontend-owned UI state to drive a dashboard-style SPA. The model below separates domain entities fetched from the backend from client-only view state managed through Redux Toolkit.

## Domain Entities

### Family

- **Fields**:
  - `id`: numeric identifier
  - `name`: display name shown across dashboard and management screens
  - `members`: ordered collection of `FamilyMember`
- **Relationships**:
  - One family has many family members
  - One family is the active planning context for dashboard, planner, and todos in this feature
- **Validation rules**:
  - `name` must be non-empty when edited or created
  - `name` must remain readable in constrained layouts
  - Persisted name changes are supported through the current family update endpoint

### FamilyMember

- **Fields**:
  - `id`: numeric identifier
  - `name`: display label for assignments and management views
  - `role`: family role label
  - `familyId`: parent family reference
- **Relationships**:
  - Many members belong to one family
  - One member can be assigned to many todos
- **Validation rules**:
  - `name` must be non-empty
  - `role` must be one of the supported family roles from the backend contract
  - Role values are readable in the UI but not editable in the current increment

### TodoItem

- **Fields**:
  - `id`: numeric identifier
  - `title`: short summary of the task
  - `description`: optional longer detail for the detailed todo view
  - `status`: open or completed state
  - `assigneeId`: optional member reference for assigned work
  - `assigneeLabel`: derived display value for list rendering
  - `weekDate`: date or day grouping used for current-week placement
  - `priority`: optional emphasis level if provided by backend
- **Relationships**:
  - Many todos can belong to one family
  - Many todos can reference zero or one family member
- **Validation rules**:
  - `title` must be non-empty
  - Unassigned todos remain valid and must render distinctly
  - New and updated todo requests must match the current backend request contract, including the existing request field name `deuDate`

## Derived View Models

### WeeklyOverview

- **Purpose**: Combined dashboard summary for the current week
- **Fields**:
  - `familyName`
  - `memberCount`
  - `openTodoCount`
  - `upcomingTodos`: ordered subset of `TodoItem`
  - `calendarDays`: seven-day strip for the active week

### TodoFilterState

- **Purpose**: Drives the detailed todo page filtering
- **Fields**:
  - `selectedAssigneeId`: nullable member identifier
  - `selectedStatus`: all/open/completed
  - `selectedWeekRange`: current-week range token
- **Validation rules**:
  - If `selectedAssigneeId` is no longer valid, reset to the default unfiltered state

### FamilyManagementDraft

- **Purpose**: Holds client-side form state for renaming a family, adding members, or changing roles
- **Fields**:
  - `familyNameDraft`
  - `memberNameDraft`
  - `memberRoleDraft`
  - `targetFamilyId`
  - `submissionState`

## Query and UI State

### RTK Query Cache Domains

- `familiesApi`: family details, members, create/update operations
- `todosApi`: todo list, filtered list, status mutations if exposed by backend

### Supported Mutations in Current Increment

- `createFamily`
- `renameFamily`
- `addFamilyMember`
- `createTodo`
- `updateTodo`
- `deleteTodo`

### Unsupported Mutations in Current Increment

- `changeFamilyMemberRole`

### Redux UI State

- `navigation`: sidebar open/closed state on smaller screens
- `dashboard`: active week offset and user dismissible UI affordances
- `todos`: active filter selections and list display mode
- `family`: transient form visibility and draft state

## State Transitions

### Route Data State

- `idle -> loading -> success`
- `idle -> loading -> error`
- `success -> refetching -> success`

### Family Management Submission

- `pristine -> editing -> submitting -> success`
- `editing -> submitting -> error -> editing`

### Todo Filtering

- `default view -> member filtered view`
- `member filtered view -> empty result state`
- `member filtered view -> reset to default` when selected member becomes invalid
