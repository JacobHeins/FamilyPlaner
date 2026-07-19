import { baseApi } from "./baseApi";
import type {
  CreateTodoRequest,
  Todo,
  TodoQueryArgs,
  UpdateTodoRequest,
} from "../app/store/types";

export const todoApi = baseApi.injectEndpoints({
  endpoints: (builder) => ({
    getTodos: builder.query<Todo[], TodoQueryArgs>({
      query: ({ familyId, assigneeId }) => ({
        url: "/todos",
        params: {
          familyId,
          ...(assigneeId ? { assigneeId } : {}),
        },
      }),
      providesTags: (result) =>
        result
          ? [
              ...result.map((todo) => ({ type: "Todo" as const, id: todo.id })),
              { type: "Todo" as const, id: "LIST" },
            ]
          : [{ type: "Todo" as const, id: "LIST" }],
    }),
    createTodo: builder.mutation<Todo, CreateTodoRequest>({
      query: (body) => ({
        url: "/todos",
        method: "POST",
        body,
      }),
      invalidatesTags: [{ type: "Todo", id: "LIST" }],
    }),
    updateTodo: builder.mutation<Todo, UpdateTodoRequest>({
      query: ({ id, ...body }) => ({
        url: `/todos/${id}`,
        method: "PUT",
        body,
      }),
      invalidatesTags: (_result, _error, body) => [
        { type: "Todo", id: body.id },
        { type: "Todo", id: "LIST" },
      ],
    }),
    deleteTodo: builder.mutation<void, number>({
      query: (id) => ({
        url: `/todos/${id}`,
        method: "DELETE",
      }),
      invalidatesTags: (_result, _error, id) => [
        { type: "Todo", id },
        { type: "Todo", id: "LIST" },
      ],
    }),
  }),
});

export const {
  useGetTodosQuery,
  useCreateTodoMutation,
  useUpdateTodoMutation,
  useDeleteTodoMutation,
} = todoApi;
