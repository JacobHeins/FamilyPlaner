# Data Model: Weekly Activity Planner

**Phase**: 1 – Design
**Branch**: `002-weekly-activity-planner`
**Date**: 2026-04-04

## Entities

### Activity

Represents a scheduled family event on a specific calendar day.

| Field          | Type             | Required | Constraints            | Notes                                                               |
| -------------- | ---------------- | -------- | ---------------------- | ------------------------------------------------------------------- |
| `id`           | `number`         | Yes      | Stable identity        | Used for selection, edit, delete, and overview-to-detail navigation |
| `name`         | `string`         | Yes      | 2–50 chars             | Primary display label                                               |
| `description`  | `string \| null` | No       | 2–300 chars if present | Optional details                                                    |
| `location`     | `string \| null` | No       | 2–50 chars if present  | Optional place                                                      |
| `day`          | `string`         | Yes      | `YYYY-MM-DD`           | Activity date                                                       |
| `startTime`    | `string \| null` | No       | Time value if present  | Optional start time                                                 |
| `endTime`      | `string \| null` | No       | Time value if present  | Optional end time                                                   |
| `familyId`     | `number`         | Yes      | Min 1                  | Owning family                                                       |
| `participants` | `FamilyMember[]` | No       | Family-member subset   | Empty or omitted means unassigned                                   |

### DashboardDailyActivityOverview

Represents the dashboard-specific view model for the current day's activities. This section is the most visually dominant on the page.

| Field                | Type             | Required | Notes                                               |
| -------------------- | ---------------- | -------- | --------------------------------------------------- |
| `date`               | `string`         | Yes      | Current local day                                   |
| `isToday`            | `boolean`        | Yes      | Always true for this overview                       |
| `activities`         | `Activity[]`     | Yes      | Current-day activities ordered for display          |
| `selectedActivityId` | `number \| null` | No       | Optional item the user chose to open in detail view |

**Display rules**: Each entry renders name, time (if set), location (if set), and all participants as visible badge elements. Overflow beyond 3 participants shows a `+N` badge. No information is hidden behind hover or secondary interaction.

### DashboardRestOfWeekPreview

Represents the secondary dashboard section showing activities for all other days of the current week.

| Field  | Type        | Required | Notes                                             |
| ------ | ----------- | -------- | ------------------------------------------------- |
| `days` | `WeekDay[]` | Yes      | Only days with at least one activity are included |

**Display rules**: Uses standard (non-accent) styling. Shows activity name and time per entry only — no participant badges in this section. Hidden entirely when no other day has activities.

### WeekDay

Represents one day in the weekly planner overview.

| Field        | Type         | Required | Notes                       |
| ------------ | ------------ | -------- | --------------------------- |
| `date`       | `string`     | Yes      | `YYYY-MM-DD`                |
| `label`      | `string`     | Yes      | German short day label      |
| `isToday`    | `boolean`    | Yes      | Controls emphasis           |
| `activities` | `Activity[]` | Yes      | Items scheduled on that day |

### ActivitySelectionContext

Represents navigation context when an overview surface opens the detailed page for a specific activity.

| Field        | Type                                    | Required | Notes                           |
| ------------ | --------------------------------------- | -------- | ------------------------------- |
| `activityId` | `number`                                | Yes      | Selected activity identity      |
| `source`     | `"dashboard" \| "week" \| "activities"` | Yes      | Helps preserve user orientation |

## Request Models

### CreateActivityRequest

| Field          | Type               | Required | Notes                               |
| -------------- | ------------------ | -------- | ----------------------------------- |
| `name`         | `string`           | Yes      | Required primary label              |
| `description`  | `string`           | No       | Optional details                    |
| `location`     | `string`           | No       | Optional place                      |
| `day`          | `string`           | Yes      | Scheduled day                       |
| `startTime`    | `string`           | No       | Optional start time                 |
| `endtime`      | `string`           | No       | Request contract uses lowercase `t` |
| `familyId`     | `number`           | Yes      | Owning family                       |
| `participants` | `number[] \| null` | No       | Nullable participant IDs            |

### UpdateActivityRequest

| Field          | Type               | Required | Notes                               |
| -------------- | ------------------ | -------- | ----------------------------------- |
| `name`         | `string`           | Yes      | Required primary label              |
| `description`  | `string`           | No       | Optional details                    |
| `location`     | `string`           | No       | Optional place                      |
| `day`          | `string`           | Yes      | Scheduled day                       |
| `startTime`    | `string`           | No       | Optional start time                 |
| `endtime`      | `string`           | No       | Request contract uses lowercase `t` |
| `participants` | `number[] \| null` | No       | Nullable participant IDs            |

## Derived Views

### Dashboard Daily View

Derivation rules:

1. Resolve the current local day
2. Filter family activities to the current day
3. Sort activities by start time, then stable fallback identity
4. Render the list inside the dominant accent-highlighted dashboard section with all participant badges inline per entry

### Dashboard Rest-of-Week Preview

Derivation rules:

1. Resolve Monday through Sunday of the current week, excluding today
2. Filter family activities to those days
3. Group by day; include only days with at least one activity
4. If no other day has activities, omit the section entirely
5. Render as a compact lower-emphasis card below the today section

### Weekly Planner View

Derivation rules:

1. Resolve Monday through Sunday of the current week
2. Filter activities to those dates
3. Group activities by day
4. Render seven visible day slots with today emphasized

## Validation Rules

| Field                     | Rule                                      | German Error Message                     |
| ------------------------- | ----------------------------------------- | ---------------------------------------- |
| `name`                    | Required, 2–50 chars                      | "Name ist erforderlich (2–50 Zeichen)"   |
| `day`                     | Required                                  | "Datum ist erforderlich"                 |
| `startTime` and `endtime` | End must be after start when both are set | "Endzeit muss nach der Startzeit liegen" |
| `description`             | 2–300 chars if set                        | "Beschreibung: 2–300 Zeichen"            |
| `location`                | 2–50 chars if set                         | "Ort: 2–50 Zeichen"                      |

## Relationship Model

```text
Family (1) ────── (many) Activity
                         │
FamilyMember (many) ─────┘ via participants

Activity (1) ────── (0..1) ActivitySelectionContext
DashboardDailyActivityOverview (1) ────── (many) Activity
WeekDay (1) ────── (many) Activity
```
