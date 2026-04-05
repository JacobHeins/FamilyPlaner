# Quickstart: Family Dashboard UI

## Goal

Verify the planned Family Dashboard UI architecture and developer workflow before implementation tasks are generated.

## Prerequisites

- Node and npm available in the workspace
- Backend available if live API verification is needed
- Existing frontend project under `/workspace/ui`

## Planned Setup

1. Change into `/workspace/ui`.
2. Install frontend dependencies, including Redux Toolkit additions required by the plan.
3. Start the development server.
4. Open the SPA and confirm the shell routes load without login.

## Planned Implementation Checks

1. Confirm the SPA bootstraps through a Redux store provider.
2. Confirm RTK Query is the source of backend request state for family and todo views.
3. Confirm dashboard, family management, and detailed todo routes share the same layout shell.
4. Confirm loading, empty, and error states exist on each primary page.
5. Confirm the visual update preserves the existing dark styling direction while improving mobile ergonomics.
6. Confirm API integrations use only the currently supported family and todo endpoints.
7. Confirm any frontend restructuring improves usability or responsiveness without making the product look like a different application.

## Verification Commands

```bash
cd /workspace/ui
npm install
npm run build
npm run dev
```

## Manual QA Checklist

1. Open the dashboard on desktop width and verify family summary and current-week todos are visible immediately.
2. Resize to tablet and phone widths and verify navigation, action buttons, and content sections remain usable without horizontal scrolling.
3. Open the family management route and verify family listing, family creation, family rename, and add-member flows expose clear feedback states.
4. Open the detailed todo route and verify filtering by family member, including empty-filter results and unassigned todo display.
5. Confirm the UI still feels visually consistent with the current application theme.

## Notes

- No login flow is expected in this increment.
- If live todo endpoints are not available yet, use that as an implementation blocker or mock-data transition step only within frontend planning boundaries.
- Activities and planner-specific UI are intentionally deferred to a later increment.
- Member-role editing is intentionally excluded until a matching backend endpoint exists.
