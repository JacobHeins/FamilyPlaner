import { baseApi } from "./baseApi";
import type {
  Activity,
  ActivityQueryArgs,
  CreateActivityRequest,
  UpdateActivityRequest,
} from "../app/store/types";

export const activityApi = baseApi.injectEndpoints({
  endpoints: (builder) => ({
    getActivities: builder.query<Activity[], ActivityQueryArgs>({
      query: ({ familyId, memberId }) => ({
        url: "/activities",
        params: {
          familyId,
          ...(memberId ? { familyMemberId: memberId } : {}),
        },
      }),
      providesTags: (result) =>
        result
          ? [
              ...result.map((act) => ({
                type: "Activity" as const,
                id: act.id,
              })),
              { type: "Activity" as const, id: "LIST" },
            ]
          : [{ type: "Activity" as const, id: "LIST" }],
    }),

    createActivity: builder.mutation<Activity, CreateActivityRequest>({
      query: (body) => ({
        url: "/activities",
        method: "POST",
        body,
      }),
      invalidatesTags: [{ type: "Activity", id: "LIST" }],
    }),

    updateActivity: builder.mutation<
      Activity,
      { id: number; body: UpdateActivityRequest }
    >({
      query: ({ id, body }) => ({
        url: `/activities/${id}`,
        method: "PUT",
        body,
      }),
      invalidatesTags: (_result, _error, { id }) => [
        { type: "Activity", id },
        { type: "Activity", id: "LIST" },
      ],
    }),

    deleteActivity: builder.mutation<void, number>({
      query: (id) => ({
        url: `/activities/${id}`,
        method: "DELETE",
      }),
      invalidatesTags: (_result, _error, id) => [
        { type: "Activity", id },
        { type: "Activity", id: "LIST" },
      ],
    }),
  }),
});

export const {
  useGetActivitiesQuery,
  useCreateActivityMutation,
  useUpdateActivityMutation,
  useDeleteActivityMutation,
} = activityApi;
