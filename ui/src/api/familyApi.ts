import { baseApi } from "./baseApi";
import type {
  AddFamilyMemberRequest,
  CreateFamilyRequest,
  Family,
  FamilyRole,
  UpdateFamilyRequest,
} from "../app/store/types";

export type { Family, FamilyMember, FamilyRole } from "../app/store/types";

export const familyApi = baseApi.injectEndpoints({
  endpoints: (builder) => ({
    getFamilies: builder.query<Family[], void>({
      query: () => "/families",
      providesTags: (result) =>
        result
          ? [
              ...result.map((family) => ({
                type: "Family" as const,
                id: family.id,
              })),
              { type: "Family" as const, id: "LIST" },
            ]
          : [{ type: "Family" as const, id: "LIST" }],
    }),
    createFamily: builder.mutation<Family, CreateFamilyRequest>({
      query: (body) => ({
        url: "/families",
        method: "POST",
        body,
      }),
      invalidatesTags: [{ type: "Family", id: "LIST" }],
    }),
    updateFamily: builder.mutation<Family, UpdateFamilyRequest>({
      query: ({ id, ...body }) => ({
        url: `/families/${id}`,
        method: "PUT",
        body,
      }),
      invalidatesTags: (_result, _error, body) => [
        { type: "Family", id: body.id },
        { type: "Family", id: "LIST" },
      ],
    }),
    addFamilyMember: builder.mutation<Family, AddFamilyMemberRequest>({
      query: ({ familyId, ...body }) => ({
        url: `/families/${familyId}/members`,
        method: "POST",
        body,
      }),
      invalidatesTags: (_result, _error, body) => [
        { type: "Family", id: body.familyId },
        { type: "Family", id: "LIST" },
      ],
    }),
  }),
});

export const {
  useGetFamiliesQuery,
  useCreateFamilyMutation,
  useUpdateFamilyMutation,
  useAddFamilyMemberMutation,
} = familyApi;

const BASE = "/api";

export async function getFamilies(): Promise<Family[]> {
  const res = await fetch(`${BASE}/families`);
  if (!res.ok) throw new Error(`Failed to fetch families: ${res.status}`);
  return res.json();
}

export async function createFamily(name: string): Promise<Family> {
  const res = await fetch(`${BASE}/families`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ name }),
  });
  if (!res.ok) throw new Error(`Failed to create family: ${res.status}`);
  return res.json();
}

export async function updateFamily(id: number, name: string): Promise<Family> {
  const res = await fetch(`${BASE}/families/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ name }),
  });
  if (!res.ok) throw new Error(`Failed to update family: ${res.status}`);
  return res.json();
}

export async function addFamilyMember(
  familyId: number,
  name: string,
  role: FamilyRole,
): Promise<Family> {
  const res = await fetch(`${BASE}/families/${familyId}/members`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ name, role }),
  });
  if (!res.ok) throw new Error(`Failed to add member: ${res.status}`);
  return res.json();
}

/** Map a FamilyRole to a CSS colour token used throughout the UI */
export function roleColor(role: FamilyRole, childIndex = 0): string {
  if (role === "DAD") return "blue";
  if (role === "MOM") return "pink";
  const childColors = ["green", "orange", "accent"];
  return childColors[childIndex % childColors.length];
}

export function roleLabel(role: FamilyRole): string {
  return role.charAt(0) + role.slice(1).toLowerCase();
}
