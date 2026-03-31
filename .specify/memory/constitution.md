<!--
Sync Impact Report
- Version change: template -> 1.0.0
- Modified principles:
	- [PRINCIPLE_1_NAME] -> I. Frontend-Only Agent Scope
	- [PRINCIPLE_2_NAME] -> II. Dashboard SPA Architecture
	- [PRINCIPLE_3_NAME] -> III. Backend Contract Fidelity
	- [PRINCIPLE_4_NAME] -> IV. Type-Safe UX Quality Gates
	- [PRINCIPLE_5_NAME] -> V. Incremental Frontend Delivery
- Added sections:
	- Technical Boundaries
	- Delivery Workflow
- Removed sections:
	- None
- Templates requiring updates:
	- ✅ .specify/templates/plan-template.md
	- ✅ .specify/templates/spec-template.md
	- ✅ .specify/templates/tasks-template.md
	- ⚠ pending: .specify/templates/commands/*.md (directory not present in this workspace)
- Follow-up TODOs:
	- None
-->

# FamilyPlaner Constitution

## Core Principles

### I. Frontend-Only Agent Scope
Agent-authored implementation MUST be limited to frontend artifacts unless the user
explicitly authorizes backend work for the current task. By default, changes belong in
`ui/` and related frontend-only documentation; Java source under `src/main/java`, backend
configuration, persistence, and migrations are read-only. Rationale: the project owner is
delegating SPA delivery, not autonomous server-side changes.

### II. Dashboard SPA Architecture
The primary product surface MUST be a dashboard-style single-page application implemented
with the existing React, TypeScript, and Vite stack in `ui/`. New functionality MUST fit
the established page, component, routing, and shared-state patterns before introducing
new frameworks or parallel frontend shells. Rationale: one coherent SPA minimizes UI
fragmentation and matches the current repository layout.

### III. Backend Contract Fidelity
Frontend work MUST consume existing backend endpoints and payloads as the source of truth.
If the current backend contract is missing, ambiguous, or insufficient, the gap MUST be
recorded in the spec or plan as a blocker or explicit follow-up instead of being resolved
through unrequested backend edits. Rationale: frontend progress must not create hidden API
scope or destabilize the Java service.

### IV. Type-Safe UX Quality Gates
Every delivered frontend slice MUST preserve TypeScript correctness and define explicit
loading, empty, success, and error states for user-visible flows. Shared API clients,
context state, and page components MUST model nullable and failure cases deliberately
instead of relying on unchecked assumptions. Rationale: dashboard SPAs fail in production
when data shape and user-state handling are implicit.

### V. Incremental Frontend Delivery
Features MUST be planned and implemented as independently reviewable user-story slices that
can be demonstrated from the SPA without parallel backend rewrites. Existing UI modules,
API helpers, and styling patterns MUST be reused before adding new abstractions, and any
new abstraction MUST be justified by repeated use or clear simplification. Rationale: small
frontend increments keep the product maintainable and aligned with real backend behavior.

## Technical Boundaries

- The repository contains a Spring Boot backend in `src/` and the dashboard SPA in `ui/`.
	The backend is integration context, not default implementation scope.
- Frontend work may add or modify pages, components, styles, context providers, and API
	client code under `ui/src/`.
- Generated assets in `src/main/resources/static/` and build output under `target/` MUST
	not be edited by hand; they are derived from the frontend build.
- The approved frontend stack is React 19, TypeScript 5, Vite 6, and React Router 7 unless
	a future amendment explicitly changes that baseline.

## Delivery Workflow

1. Every feature spec, plan, and task list MUST state whether the work is frontend-only or
	 explicitly authorizes backend changes.
2. Every implementation plan MUST pass a constitution check that confirms frontend-only
	 file scope, SPA/dashboard fit, backend contract reuse, and verification steps for build
	 and user-visible UI states.
3. Reviews MUST reject work that changes backend behavior without explicit user approval or
	 that skips TypeScript/build validation for frontend changes.
4. When API gaps block delivery, the gap MUST be documented for follow-up rather than
	 patched through speculative server-side code.

## Governance

This constitution overrides conflicting local practices for planning and implementation
scope. Amendments require updating this file and any affected templates in `.specify/` in
the same change. Versioning follows semantic versioning: MAJOR for incompatible governance
changes or principle removal, MINOR for new principles or materially expanded requirements,
and PATCH for clarifications that do not change expected behavior. Compliance review is
mandatory for every spec, plan, tasks document, and implementation review, with explicit
checks for frontend-only scope, SPA alignment, backend contract fidelity, and frontend
quality gates.

**Version**: 1.0.0 | **Ratified**: 2026-03-31 | **Last Amended**: 2026-03-31
