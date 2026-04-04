# Quickstart: Weekly Activity Planner

**Feature**: Weekly Activity Planner
**Branch**: `002-weekly-activity-planner`
**Date**: 2026-04-04

## Prerequisites

- Development backend available
- Frontend available at the existing SPA entry point
- At least one family with members present in the application
- Activities available for the active family to verify dashboard and weekly surfaces

## What Gets Built

| Surface                   | Route         | Outcome                                                                                                                                 |
| ------------------------- | ------------- | --------------------------------------------------------------------------------------------------------------------------------------- |
| Dashboard — today section | `/`           | Most visually dominant section; shows today's activities with inline participant badges per entry; click-through to detail              |
| Dashboard — rest-of-week  | `/`           | Secondary, compact section; shows other days with activities at reduced emphasis; hidden when no other day has activities               |
| Weekly overview           | `/week`       | Shows the current week grouped by day with today emphasized; always renders all 7 days                                                  |
| Detailed management       | `/activities` | Shows full activity details, creation, editing, deletion, and filtering; highlights selected item arriving from dashboard click-through |

## Implementation Order

### 1. Dashboard today section (dominant, with participant display)

1. Add activity loading to the dashboard
2. Derive current-day activities; sort by start time
3. Render a dominant accent-highlighted section for today's items — each entry showing name, time, location, and all participant badges inline
4. Apply overflow badge (`+N`) for entries with more than 3 participants
5. Make activity selection open the detailed management page for the chosen item

### 2. Dashboard rest-of-week preview

6. Derive other-day activities grouped by day
7. Render a secondary (non-accent) compact section below today; omit entirely when empty

### 3. Weekly overview alignment

8. Keep the weekly planner consistent with shared current-week and today rules
9. Always render all 7 day columns; preserve today emphasis and German empty states

### 4. Detailed activity management

10. Ensure create, edit, delete, and filter flows stay available on `/activities`
11. Make the item selected from the dashboard immediately visible via scroll + highlight ring on arrival

### 5. Verification and polish

12. Validate German copy across all activity surfaces
13. Validate responsive behavior on dashboard (both sections), weekly view, and detailed management
14. Run a final build check

## Verification

```bash
cd /workspace/ui
npm run build
```

Manual checks:

1. Open `/` and verify the today-activity section is visually the most dominant element on the page
2. Verify each today activity entry shows the activity name, time (if set), and all participant badges without any hover or tap
3. Verify the `+N` overflow badge appears for entries with more than 3 participants
4. Verify an empty German state appears on the dashboard when today has no activities
5. Click a dashboard activity and verify `/activities` opens for that specific item with it highlighted
6. Verify the rest-of-week preview shows other days' activities below the today section at reduced emphasis; verify it is hidden when no other day has activities
7. Open `/week` and verify the full week is grouped correctly with today emphasized; verify all 7 day columns are always visible
8. Open `/activities` and verify create, edit, delete, and filter flows still work
9. Repeat the key dashboard flows on a 320 px mobile viewport — confirm participant badges wrap without layout breakage

## German UI Reference

Suggested key copy:

| Context                      | German String                              |
| ---------------------------- | ------------------------------------------ |
| Dashboard today title        | "Heutige Aktivitäten"                      |
| Dashboard rest-of-week title | "Weitere Aktivitäten dieser Woche"         |
| Dashboard today empty state  | "Heute keine Aktivitäten geplant."         |
| Dashboard loading state      | "Aktivitäten werden geladen…"              |
| Dashboard error state        | "Aktivitäten konnten nicht geladen werden" |
| No participants placeholder  | "Keine Teilnehmer"                         |
| Weekly page title            | "Wochenübersicht"                          |
| Weekly today empty           | "Heute leer"                               |
| Detailed page title          | "Aktivitäten"                              |
| Create button                | "Aktivität planen"                         |
| Edit action                  | "Bearbeiten"                               |
| Delete action                | "Löschen"                                  |
