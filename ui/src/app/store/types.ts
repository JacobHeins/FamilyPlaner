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
