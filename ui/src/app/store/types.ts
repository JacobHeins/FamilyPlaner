import { createSelector } from "@reduxjs/toolkit";

export type FamilyRole = "DAD" | "MOM" | "CHILD";

export interface FamilyMember {
  id: number;
  name: string;
  role: FamilyRole;
}

export interface Family {
  id: number;
  name: string;
  familyMembers: FamilyMember[];
}

export interface Todo {
  id: number;
  name: string;
  description: string | null;
  dueDate: string | null;
  completed: boolean;
  familyId: number;
  assignee: FamilyMember | null;
}

export interface CreateFamilyRequest {
  name: string;
}

export interface UpdateFamilyRequest {
  id: number;
  name: string;
}

export interface AddFamilyMemberRequest {
  familyId: number;
  name: string;
  role: FamilyRole;
}

export interface TodoQueryArgs {
  familyId: number;
  assigneeId?: number;
}

export interface CreateTodoRequest {
  name: string;
  description?: string;
  deuDate?: string;
  familyId: number;
  assigneeId?: number;
}

export interface UpdateTodoRequest {
  id: number;
  name: string;
  description?: string;
  deuDate?: string;
  completed: boolean;
  assigneeId?: number;
}

// ─── Activity types ────────────────────────────────────────────────────────

export interface Activity {
  id: number;
  name: string;
  description: string | null;
  location: string | null;
  day: string; // YYYY-MM-DD
  startTime: string | null; // HH:mm:ss
  endTime: string | null; // HH:mm:ss
  familyId: number;
  participants: FamilyMember[];
}

export interface ActivityQueryArgs {
  familyId: number;
  memberId?: number;
}

export interface CreateActivityRequest {
  name: string;
  description?: string;
  location?: string;
  day: string;
  startTime?: string;
  endtime?: string; // lowercase 't' — matches backend DTO field name
  familyId: number;
  participants?: number[] | null;
}

export interface UpdateActivityRequest {
  name: string;
  description?: string;
  location?: string;
  day: string;
  startTime?: string;
  endtime?: string; // lowercase 't' — matches backend DTO field name
  participants?: number[] | null;
}

// ─── Selectors ─────────────────────────────────────────────────────────────

export const selectFamilies = createSelector(
  [(families: Family[]) => families],
  (families) => families,
);

export const selectPrimaryFamily = createSelector(
  [selectFamilies],
  (families) => families[0] ?? null,
);

export const selectFamilyMembers = createSelector(
  [selectFamilies],
  (families) => families.flatMap((family) => family.familyMembers),
);

export const selectTodos = createSelector(
  [(todos: Todo[]) => todos],
  (todos) => todos,
);

export const selectOpenTodos = createSelector([selectTodos], (todos) =>
  todos.filter((todo) => !todo.completed),
);

export const selectTodosByAssignee = createSelector(
  [selectTodos, (_todos: Todo[], assigneeId?: number | null) => assigneeId],
  (todos, assigneeId) => {
    if (!assigneeId) {
      return todos;
    }

    return todos.filter((todo) => todo.assignee?.id === assigneeId);
  },
);
