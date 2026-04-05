# Quickstart: Dashboard Week-Focused Layout

## Goal

Verify that the dashboard now centers on a selected day's activities, uses a Wochenübersicht-style week selector, and keeps only the open-todo overview below.

## Preconditions

- A family exists in the application.
- The family has activities on at least two different days of the current week.
- The family has at least one open todo for the positive todo-overview check.
- The UI dependencies are installed in `ui/`.

## Run

1. Start the frontend from `ui/` with `npm run dev`.
2. Open the application dashboard at `/`.

## Verify Default Dashboard State

1. Confirm the top dashboard section shows activities for the current day by default.
2. Confirm the section heading and any empty or loading text are in German.
3. Confirm the former summary tiles are no longer visible.
4. Confirm the former family-members dashboard section is no longer visible.
5. Confirm the separate "Weitere Aktivitäten der Woche" section is no longer visible.

## Verify Embedded Weekly Overview

1. Confirm the section below the top detail area visually matches the existing Wochenübersicht pattern.
2. Confirm all seven days of the current week are shown.
3. Confirm the current day is visibly marked.
4. Select another day in the week overview.
5. Confirm the selected state changes and the top detail section updates to that day.

## Verify Activity Detail Navigation

1. In the top selected-day section, choose one visible activity.
2. Confirm the application opens the activities page for that same activity.

## Verify Open Todo Overview

1. Return to the dashboard.
2. Confirm the only lower content section is the open-todo overview.
3. Confirm open todo items still show their essential information in German.

## Verify Empty States

1. Select a day with no activities.
2. Confirm the top section shows a German-language empty state for that selected day.
3. If possible, test with no open todos and confirm the remaining todo section shows a German-language empty state.

## Verification Command

1. Run `npm run build` in `ui/` to validate the TypeScript and production build pipeline.
