# Data Model: Dashboard Week-Focused Layout

## Overview

This feature does not introduce new persisted backend entities. It introduces a refined
frontend interaction model for how existing activity and todo data are grouped and presented
on the dashboard.

## Entities

### Selected Dashboard Day

- **Purpose**: Represents the active day whose activities are shown in detail at the top of the dashboard.
- **Fields**:
  - `dateStr`: current-week date in `YYYY-MM-DD` format
  - `isToday`: whether this date matches the user's current local day
  - `isSelected`: whether this date is currently active in the dashboard UI
- **Validation rules**:
  - Must match one of the seven `dateStr` values in the current week overview
  - Exactly one day may be selected at a time
- **State transitions**:
  - `default-selected` on initial dashboard load for the current day
  - `user-selected` when a different week day is clicked or keyboard-selected
  - `revalidated` after data refresh if the same day still belongs to the current week

### Dashboard Week Day

- **Purpose**: Represents one visible day column or card within the embedded weekly overview.
- **Fields**:
  - `label`: localized short weekday label such as `Mo` or `Di`
  - `dateStr`: full date key used for comparisons and grouping
  - `date`: day-of-month display number
  - `isToday`: visual marker for the current local date
  - `isSelected`: visual marker for the active dashboard selection
  - `activities`: zero or more activities assigned to that date
- **Relationships**:
  - One dashboard week day can contain many activities
  - One dashboard week contains exactly seven dashboard week days

### Selected-Day Activity Overview Item

- **Purpose**: Represents an activity rendered in the top dashboard detail section for the selected day.
- **Fields**:
  - `id`: stable activity identifier used for navigation
  - `name`: activity title
  - `day`: assigned date
  - `startTime`: optional start time
  - `endTime`: optional end time
  - `location`: optional location text
  - `description`: optional descriptive text if already available in the activity model
  - `participants`: zero or more assigned family members
- **Validation rules**:
  - Must belong to the currently selected day to appear in the top detail section
  - Must remain uniquely identifiable for detail-page navigation

### Open Todo Overview Item

- **Purpose**: Represents a todo rendered in the only remaining lower dashboard section.
- **Fields**:
  - `id`: stable todo identifier
  - `name`: todo title
  - `dueDate`: optional due date
  - `completed`: completion flag used to exclude closed items from the overview
  - `assignee`: optional family member assignment
- **Validation rules**:
  - Only todos that are still open may appear in the retained overview
  - Empty state must render when no open todos qualify for display

## Relationships

- One **Selected Dashboard Day** maps to exactly one **Dashboard Week Day**.
- One **Dashboard Week Day** can contain zero or more **Selected-Day Activity Overview Items** when it is the active day.
- The dashboard screen contains one selected-day detail section, one seven-day weekly overview, and one open-todo overview section.

## Derived Views

- **Selected-day detail list**: `activities.filter(activity.day === selectedDay.dateStr)` sorted by start time.
- **Embedded weekly overview**: group all current-week activities by week-day `dateStr`.
- **Open todo overview**: current open todos derived from the existing todo response using the app's current dashboard filtering rules.

## Empty-State Conditions

- **Selected day empty**: the selected-day detail area renders a German-language empty state for that day.
- **Full week empty**: the weekly overview still shows all seven days, each without activity cards.
- **No open todos**: the lower dashboard section renders a German-language empty state rather than disappearing.
