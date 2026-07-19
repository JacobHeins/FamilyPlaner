package com.heins.familyplanner.todos;

import com.heins.familyplanner.accounts.Repositories.FamilyAccountsRepository;
import com.heins.familyplanner.accounts.entities.FamilyAccount;
import com.heins.familyplanner.exceptions.Result;
import com.heins.familyplanner.family.entities.Family;
import com.heins.familyplanner.security.JwtService;
import com.heins.familyplanner.todos.dtos.TodoResponse;
import com.heins.familyplanner.config.TestWebMvcConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TodoController.class)
@ImportAutoConfiguration(exclude = com.heins.familyplanner.configuration.JpaConfig.class)
@Import(TestWebMvcConfig.class)
public class TodoControllerTest {

        @Autowired
        MockMvc mockMvc;

        @MockitoBean
        TodoService todoService;

        @MockitoBean
        JwtService jwtService;

        @MockitoBean
        FamilyAccountsRepository accountsRepository;

        private static final String TOKEN = "Bearer test-token";

        private FamilyAccount mockAccount(Long familyId) {
                Family family = new Family("Test");
                ReflectionTestUtils.setField(family, "id", familyId);
                FamilyAccount account = new FamilyAccount("test-slug", "hash", family);
                ReflectionTestUtils.setField(account, "id", 1L);
                return account;
        }

        @BeforeEach
        void setUpJwt() {
                when(jwtService.isTokenValid("test-token")).thenReturn(true);
                when(jwtService.extractSlug("test-token")).thenReturn("test-slug");
                when(accountsRepository.findByPublicSlug("test-slug")).thenReturn(Optional.of(mockAccount(1L)));
        }

        // -------------------------------------------------------
        // GET /api/todos
        // -------------------------------------------------------
        @Test
        void getTodos_returnsOk_whenFamilyExists() throws Exception {
                TodoResponse todo = new TodoResponse(1L, "Buy milk", null, null, false, 1L, null);
                when(todoService.getTasks(any())).thenReturn(Result.success(List.of(todo)));

                mockMvc.perform(get("/api/todos")
                                .param("familyId", "1")
                                .header("Authorization", TOKEN))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.length()").value(1))
                                .andExpect(jsonPath("$[0].id").value(1))
                                .andExpect(jsonPath("$[0].name").value("Buy milk"));

                verify(todoService).getTasks(any());
        }

        @Test
        void getTodos_returnsNotFound_whenFamilyMissing() throws Exception {
                when(todoService.getTasks(any())).thenReturn(Result.notFound("Family not found: 1"));

                mockMvc.perform(get("/api/todos")
                                .param("familyId", "1")
                                .header("Authorization", TOKEN))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.detail").value("Family not found: 1"));
        }

        @Test
        void getTodos_returnsForbidden_whenFamilyIdDoesNotMatchAccount() throws Exception {
                mockMvc.perform(get("/api/todos")
                                .param("familyId", "99")
                                .header("Authorization", TOKEN))
                                .andExpect(status().isForbidden())
                                .andExpect(jsonPath("$.detail").value("Family access is not permitted"))
                                .andExpect(jsonPath("$.type")
                                                .value("https://familyplanner.heins.com/errors/forbidden"));

                verify(todoService, never()).getTasks(any());
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
                                .content(json)
                                .header("Authorization", TOKEN))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.name").value("Buy milk"));
        }

        @Test
        void addTodo_returnsBadRequest_whenNameMissing() throws Exception {
                mockMvc.perform(post("/api/todos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{ \"familyId\": 1 }")
                                .header("Authorization", TOKEN))
                                .andExpect(status().isBadRequest());

                verify(todoService, never()).addTask(any());
        }

        @Test
        void addTodo_returnsBadRequest_whenFamilyIdMissing() throws Exception {
                mockMvc.perform(post("/api/todos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{ \"name\": \"Buy milk\" }")
                                .header("Authorization", TOKEN))
                                .andExpect(status().isBadRequest());

                verify(todoService, never()).addTask(any());
        }

        @Test
        void addTodo_returnsNotFound_whenFamilyMissing() throws Exception {
                when(todoService.addTask(any())).thenReturn(Result.notFound("Family not found: 99"));

                String json = """
                                { "name": "Buy milk", "familyId": 1 }
                                """;

                mockMvc.perform(post("/api/todos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                                .header("Authorization", TOKEN))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.detail").value("Family not found: 99"));
        }

        // -------------------------------------------------------
        // PUT /api/todos/{id}
        // -------------------------------------------------------
        @Test
        void updateTodo_returnsOk_whenValid() throws Exception {
                TodoResponse updated = new TodoResponse(1L, "Updated name", null, null, true, 1L, null);
                when(todoService.updateTodo(any(), any(), any())).thenReturn(Result.success(updated));

                String json = """
                                { "name": "Updated name", "completed": true }
                                """;

                mockMvc.perform(put("/api/todos/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                                .header("Authorization", TOKEN))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.name").value("Updated name"))
                                .andExpect(jsonPath("$.completed").value(true));
        }

        @Test
        void updateTodo_returnsNotFound_whenTodoMissing() throws Exception {
                when(todoService.updateTodo(any(), any(), any())).thenReturn(Result.notFound("Todo not found: 99"));

                String json = """
                                { "name": "Updated name", "completed": false }
                                """;

                mockMvc.perform(put("/api/todos/99")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                                .header("Authorization", TOKEN))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.detail").value("Todo not found: 99"));
        }

        // -------------------------------------------------------
        // DELETE /api/todos/{id}
        // -------------------------------------------------------
        @Test
        void addTodo_returnsForbidden_whenFamilyIdDoesNotMatchAccount() throws Exception {
                mockMvc.perform(post("/api/todos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{ \"name\": \"Buy milk\", \"familyId\": 99 }")
                                .header("Authorization", TOKEN))
                                .andExpect(status().isForbidden())
                                .andExpect(jsonPath("$.detail").value("Family access is not permitted"))
                                .andExpect(jsonPath("$.type")
                                                .value("https://familyplanner.heins.com/errors/forbidden"));

                verify(todoService, never()).addTask(any());
        }

        // -------------------------------------------------------
        // DELETE /api/todos/{id}
        // -------------------------------------------------------
        @Test
        void deleteTodo_returnsNoContent_whenTodoExists() throws Exception {
                when(todoService.deleteTodo(any(), any())).thenReturn(Result.success(null));

                mockMvc.perform(delete("/api/todos/1")
                                .header("Authorization", TOKEN))
                                .andExpect(status().isNoContent());

                verify(todoService).deleteTodo(1L, 1L);
        }

        @Test
        void deleteTodo_returnsNotFound_whenTodoMissing() throws Exception {
                when(todoService.deleteTodo(any(), any())).thenReturn(Result.notFound("Todo not found: 99"));

                mockMvc.perform(delete("/api/todos/99")
                                .header("Authorization", TOKEN))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.detail").value("Todo not found: 99"));
        }
}
