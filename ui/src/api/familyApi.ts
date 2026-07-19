import { baseApi } from "./baseApi";
import type { RootState } from "../app/store/store";
import type { FetchBaseQueryError } from "@reduxjs/toolkit/query/react";
import type {
  AddFamilyMemberRequest,
  Family,
  FamilyRole,
  UpdateFamilyRequest,
} from "../app/store/types";

export type { Family, FamilyMember, FamilyRole } from "../app/store/types";

/** Extract the `familyId` claim from a stored JWT (base64url payload). */
function familyIdFromToken(token: string | null): number | null {
  if (!token) return null;
  const segment = token.split(".")[1];
  if (!segment) return null;
  try {
    const base64 = segment.replace(/-/g, "+").replace(/_/g, "/");
    const payload = JSON.parse(atob(base64)) as { familyId?: number | string };
    const familyId = Number(payload.familyId);
    return Number.isFinite(familyId) ? familyId : null;
  } catch {
    return null;
  }
}

export const familyApi = baseApi.injectEndpoints({
  endpoints: (builder) => ({
    getFamilies: builder.query<Family[], void>({
      async queryFn(_arg, api, _extraOptions, baseQuery) {
        const token = (api.getState() as RootState).auth.token;
        const familyId = familyIdFromToken(token);

        if (!familyId) {
          return {
            error: {
              status: 401,
              data: "Kein Familienkonto im Token gefunden.",
            } as FetchBaseQueryError,
          };
        }

        const result = await baseQuery(`/families/${familyId}`);
        if (result.error) {
          return { error: result.error };
        }

        return { data: [result.data as Family] };
      },
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
  useUpdateFamilyMutation,
  useAddFamilyMemberMutation,
} = familyApi;

const BASE = "/api";

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
