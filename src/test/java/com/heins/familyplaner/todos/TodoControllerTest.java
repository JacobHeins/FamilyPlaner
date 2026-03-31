package com.heins.familyplaner.todos;

import com.heins.familyplaner.exceptions.Result;
import com.heins.familyplaner.todos.dtos.TodoResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TodoController.class)
@ImportAutoConfiguration(exclude = com.heins.familyplaner.configuration.JpaConfig.class)
public class TodoControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    TodoService todoService;

    // -------------------------------------------------------
    // GET /api/todos
    // -------------------------------------------------------
    @Test
    void getTodos_returnsOk_whenFamilyExists() throws Exception {
        TodoResponse todo = new TodoResponse(1L, "Buy milk", null, null, false, 1L, null);
        when(todoService.getTasks(1L, null)).thenReturn(Result.success(List.of(todo)));

        mockMvc.perform(get("/api/todos").param("familyId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Buy milk"));

        verify(todoService).getTasks(1L, null);
    }

    @Test
    void getTodos_returnsNotFound_whenFamilyMissing() throws Exception {
        when(todoService.getTasks(99L, null)).thenReturn(Result.notFound("Family not found: 99"));

        mockMvc.perform(get("/api/todos").param("familyId", "99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Family not found: 99"));
    }

    // -------------------------------------------------------
    // POST /api/todos
    // -------------------------------------------------------
    @Test
    void addTodo_returnsCreated_whenValid() throws Exception {
        TodoResponse todo = new TodoResponse(1L, "Buy milk", null, null, false, 1L, null);
        when(todoService.addTask(any())).thenReturn(Result.success(todo));

        String json = """
                { "name": "Buy milk", "familyId": 1 }
                """;

        mockMvc.perform(post("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Buy milk"));
    }

    @Test
    void addTodo_returnsBadRequest_whenNameMissing() throws Exception {
        mockMvc.perform(post("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"familyId\": 1 }"))
                .andExpect(status().isBadRequest());

        verify(todoService, never()).addTask(any());
    }

    @Test
    void addTodo_returnsBadRequest_whenFamilyIdMissing() throws Exception {
        mockMvc.perform(post("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"name\": \"Buy milk\" }"))
                .andExpect(status().isBadRequest());

        verify(todoService, never()).addTask(any());
    }

    @Test
    void addTodo_returnsNotFound_whenFamilyMissing() throws Exception {
        when(todoService.addTask(any())).thenReturn(Result.notFound("Family not found: 99"));

        String json = """
                { "name": "Buy milk", "familyId": 99 }
                """;

        mockMvc.perform(post("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Family not found: 99"));
    }

    // -------------------------------------------------------
    // PUT /api/todos
    // -------------------------------------------------------
    @Test
    void updateTodo_returnsOk_whenValid() throws Exception {
        TodoResponse updated = new TodoResponse(1L, "Updated name", null, null, true, 1L, null);
        when(todoService.updateTodo(any())).thenReturn(Result.success(updated));

        String json = """
                { "id": 1, "name": "Updated name", "completed": true }
                """;

        mockMvc.perform(put("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated name"))
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    void updateTodo_returnsNotFound_whenTodoMissing() throws Exception {
        when(todoService.updateTodo(any())).thenReturn(Result.notFound("Todo not found: 99"));

        String json = """
                { "id": 99, "name": "Updated name", "completed": false }
                """;

        mockMvc.perform(put("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Todo not found: 99"));
    }

    // -------------------------------------------------------
    // DELETE /api/todos/{id}
    // -------------------------------------------------------
    @Test
    void deleteTodo_returnsNoContent_whenTodoExists() throws Exception {
        when(todoService.deleteTodo(1L)).thenReturn(Result.success(null));

        mockMvc.perform(delete("/api/todos/1"))
                .andExpect(status().isNoContent());

        verify(todoService).deleteTodo(1L);
    }

    @Test
    void deleteTodo_returnsNotFound_whenTodoMissing() throws Exception {
        when(todoService.deleteTodo(99L)).thenReturn(Result.notFound("Todo not found: 99"));

        mockMvc.perform(delete("/api/todos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Todo not found: 99"));
    }
}
