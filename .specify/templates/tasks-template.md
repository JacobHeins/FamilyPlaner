---
description: "Task list template for feature implementation"
---

# Tasks: [FEATURE NAME]

**Input**: Design documents from `/specs/[###-feature-name]/`
**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, contracts/

**Tests**: The examples below include test tasks. Tests are OPTIONAL - only include them if explicitly requested in the feature specification.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

**Scope Rule**: Unless the feature spec explicitly authorizes backend work, tasks MUST stay in
`ui/` and related frontend documentation only.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Path Conventions

- **Frontend SPA**: `ui/src/` for pages, components, context, API clients, and styles
- **Frontend verification**: co-located tests in `ui/src/` or other frontend-only test paths defined by the plan
- **Backend context**: `src/main/java/` and `src/test/java/` are read-only unless backend work is explicitly approved
- **Generated assets**: `src/main/resources/static/` and `target/` are build outputs and should not receive manual tasks

<!--
  ============================================================================
  IMPORTANT: The tasks below are SAMPLE TASKS for illustration purposes only.

  The /speckit.tasks command MUST replace these with actual tasks based on:
  - User stories from spec.md (with their priorities P1, P2, P3...)
  - Feature requirements from plan.md
  - Entities from data-model.md
  - Endpoints from contracts/

  Tasks MUST be organized by user story so each story can be:
  - Implemented independently
  - Tested independently
  - Delivered as an MVP increment

  DO NOT keep these sample tasks in the generated tasks.md file.
  ============================================================================
-->

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [ ] T001 Confirm frontend-only scope and affected `ui/` paths from the implementation plan
- [ ] T002 Initialize or align required frontend dependencies in `ui/package.json`
- [ ] T003 [P] Configure frontend linting, formatting, or type-check tooling if required by the feature

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

Examples of foundational tasks (adjust based on your project):

- [ ] T004 Create or refine shared API client helpers in `ui/src/api/`
- [ ] T005 [P] Establish shared page/layout/context state in `ui/src/components/` or `ui/src/context/`
- [ ] T006 [P] Define routing/navigation updates in `ui/src/App.tsx` or layout modules
- [ ] T007 Create shared DTO or view-model types needed by multiple stories
- [ ] T008 Configure loading, empty, and error-state patterns for the feature area
- [ ] T009 Record any backend contract gaps as follow-up items instead of implementation tasks

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - [Title] (Priority: P1) 🎯 MVP

**Goal**: [Brief description of what this story delivers]

**Independent Test**: [How to verify this story works on its own]

### Tests for User Story 1 (OPTIONAL - only if tests requested) ⚠️

> **NOTE: Write these tests FIRST, ensure they FAIL before implementation**

- [ ] T010 [P] [US1] Add frontend component or flow test in `ui/src/`
- [ ] T011 [P] [US1] Add API integration mock or contract fixture for the user journey

### Implementation for User Story 1

- [ ] T012 [P] [US1] Add or update page/component files in `ui/src/pages/` or `ui/src/components/`
- [ ] T013 [P] [US1] Add or update feature styles in frontend CSS modules or page styles
- [ ] T014 [US1] Implement API client/state wiring in `ui/src/api/` or `ui/src/context/`
- [ ] T015 [US1] Implement route or interaction flow in the relevant frontend module
- [ ] T016 [US1] Add loading, empty, validation, and error handling states
- [ ] T017 [US1] Verify the user story with `npm run build` in `ui/` and any requested tests

**Checkpoint**: At this point, User Story 1 should be fully functional and testable independently

---

## Phase 4: User Story 2 - [Title] (Priority: P2)

**Goal**: [Brief description of what this story delivers]

**Independent Test**: [How to verify this story works on its own]

### Tests for User Story 2 (OPTIONAL - only if tests requested) ⚠️

- [ ] T018 [P] [US2] Add frontend component or flow test in `ui/src/`
- [ ] T019 [P] [US2] Add API integration mock or contract fixture for the user journey

### Implementation for User Story 2

- [ ] T020 [P] [US2] Add or update page/component files in `ui/src/pages/` or `ui/src/components/`
- [ ] T021 [US2] Implement API client/state updates in `ui/src/api/` or `ui/src/context/`
- [ ] T022 [US2] Implement the user interaction flow in the relevant frontend module
- [ ] T023 [US2] Integrate with shared frontend components from User Story 1 when needed

**Checkpoint**: At this point, User Stories 1 AND 2 should both work independently

---

## Phase 5: User Story 3 - [Title] (Priority: P3)

**Goal**: [Brief description of what this story delivers]

**Independent Test**: [How to verify this story works on its own]

### Tests for User Story 3 (OPTIONAL - only if tests requested) ⚠️

- [ ] T024 [P] [US3] Add frontend component or flow test in `ui/src/`
- [ ] T025 [P] [US3] Add API integration mock or contract fixture for the user journey

### Implementation for User Story 3

- [ ] T026 [P] [US3] Add or update page/component files in `ui/src/pages/` or `ui/src/components/`
- [ ] T027 [US3] Implement API client/state updates in `ui/src/api/` or `ui/src/context/`
- [ ] T028 [US3] Implement the user interaction flow in the relevant frontend module

**Checkpoint**: All user stories should now be independently functional

---

[Add more user story phases as needed, following the same pattern]

---

## Phase N: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [ ] TXXX [P] Documentation updates in docs/
- [ ] TXXX Code cleanup and refactoring in `ui/`
- [ ] TXXX Performance and responsiveness optimization across affected frontend flows
- [ ] TXXX [P] Additional frontend tests (if requested) in `ui/`
- [ ] TXXX Accessibility and UX-state review across user stories
- [ ] TXXX Run `npm run build` in `ui/` and quickstart validation

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3+)**: All depend on Foundational phase completion
  - User stories can then proceed in parallel (if staffed)
  - Or sequentially in priority order (P1 → P2 → P3)
- **Polish (Final Phase)**: Depends on all desired user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - No dependencies on other stories
- **User Story 2 (P2)**: Can start after Foundational (Phase 2) - May integrate with US1 but should be independently testable
- **User Story 3 (P3)**: Can start after Foundational (Phase 2) - May integrate with US1/US2 but should be independently testable

### Within Each User Story

- Tests (if included) MUST be written and FAIL before implementation
- API/state contracts before page wiring
- Shared UI/state primitives before page-specific assembly
- Core implementation before integration
- Story complete before moving to next priority

### Parallel Opportunities

- All Setup tasks marked [P] can run in parallel
- All Foundational tasks marked [P] can run in parallel (within Phase 2)
- Once Foundational phase completes, all user stories can start in parallel (if team capacity allows)
- All tests for a user story marked [P] can run in parallel
- Models within a story marked [P] can run in parallel
- Different user stories can be worked on in parallel by different team members

---

## Parallel Example: User Story 1

```bash
# Launch all tests for User Story 1 together (if tests requested):
Task: "Frontend component or flow test in ui/src/"
Task: "API integration mock or contract fixture for the user journey"

# Launch parallel frontend implementation tasks for User Story 1:
Task: "Add or update page/component files in ui/src/pages/ or ui/src/components/"
Task: "Add or update feature styles in frontend CSS modules or page styles"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL - blocks all stories)
3. Complete Phase 3: User Story 1
4. **STOP and VALIDATE**: Test User Story 1 independently
5. Deploy/demo if ready

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add User Story 1 → Test independently → Deploy/Demo (MVP!)
3. Add User Story 2 → Test independently → Deploy/Demo
4. Add User Story 3 → Test independently → Deploy/Demo
5. Each story adds value without breaking previous stories

### Parallel Team Strategy

With multiple developers:

1. Team completes Setup + Foundational together
2. Once Foundational is done:
   - Developer A: User Story 1
   - Developer B: User Story 2
   - Developer C: User Story 3
3. Stories complete and integrate independently

---

## Notes

- [P] tasks = different files, no dependencies
- [Story] label maps task to specific user story for traceability
- Each user story should be independently completable and testable
- Verify tests fail before implementing
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
- Avoid: vague tasks, same file conflicts, cross-story dependencies that break independence
