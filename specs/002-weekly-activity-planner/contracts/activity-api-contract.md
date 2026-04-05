# API Contract: Activity Endpoints

**Feature**: Weekly Activity Planner
**Date**: 2026-04-04

## Base Path

`/api/activities`

## Operations

### GET `/api/activities`

List activities for a family, optionally filtered by participant.

Query parameters:

| Parameter  | Type     | Required | Description                                           |
| ---------- | -------- | -------- | ----------------------------------------------------- |
| `familyId` | `number` | Yes      | Family whose activities should be returned            |
| `memberId` | `number` | No       | Restricts results to activities involving that member |

Success response shape:

```json
[
  {
    "id": 17,
    "name": "Fußballtraining",
    "description": "Training im Stadtpark",
    "location": "Stadtpark Platz 3",
    "day": "2026-04-07",
    "startTime": "16:00:00",
    "endTime": "17:30:00",
    "familyId": 1,
    "participants": [{ "id": 2, "name": "Max", "role": "CHILD" }]
  }
]
```

Used by:

- dashboard daily overview
- weekly planner overview
- detailed activities list
- participant-filtered detail list

### POST `/api/activities`

Create a new activity.

Request body:

```json
{
  "name": "Fußballtraining",
  "description": "Training im Stadtpark",
  "location": "Stadtpark Platz 3",
  "day": "2026-04-07",
  "startTime": "16:00",
  "endtime": "17:30",
  "familyId": 1,
  "participants": [2]
}
```

Notes:

- Request payload uses `endtime` with lowercase `t`
- `participants` may be `null` when nobody is selected

### PUT `/api/activities/{id}`

Update an existing activity by identity.

Path parameters:

| Parameter | Type     | Required | Description       |
| --------- | -------- | -------- | ----------------- |
| `id`      | `number` | Yes      | Activity identity |

Request body uses the same scheduling fields as create, without `familyId`.

### DELETE `/api/activities/{id}`

Delete an activity by identity.

Path parameters:

| Parameter | Type     | Required | Description       |
| --------- | -------- | -------- | ----------------- |
| `id`      | `number` | Yes      | Activity identity |

## Frontend Consumption Model

| Operation | Primary Consumers                    |
| --------- | ------------------------------------ |
| `GET`     | Dashboard, WeeklyPlanner, Activities |
| `POST`    | Activities create flow               |
| `PUT`     | Activities edit flow                 |
| `DELETE`  | Activities delete flow               |

## Contract Notes

- Stable activity identity is required for overview-to-detail navigation and item-level updates
- Dashboard and weekly surfaces consume the same activity list and derive their own views client-side
- The detailed activities page is the canonical place for create, edit, delete, and filter flows
