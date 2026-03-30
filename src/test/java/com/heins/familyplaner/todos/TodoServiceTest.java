package com.heins.familyplaner.todos;

import com.heins.familyplaner.exceptions.Result;
import com.heins.familyplaner.family.entities.Family;
import com.heins.familyplaner.family.entities.FamilyMember;
import com.heins.familyplaner.family.entities.FamilyRole;
import com.heins.familyplaner.family.mapper.FamilyMapper;
import com.heins.familyplaner.family.mapper.FamilyRoleMapper;
import com.heins.familyplaner.family.repositories.FamilyMemberRepository;
import com.heins.familyplaner.family.repositories.FamilyRepository;
import com.heins.familyplaner.todos.dtos.AddTodoRequest;
import com.heins.familyplaner.todos.dtos.TodoResponse;
import com.heins.familyplaner.todos.dtos.UpdateTodoRequest;
import com.heins.familyplaner.todos.entities.Todo;
import com.heins.familyplaner.todos.mapper.TodoMapper;
import com.heins.familyplaner.todos.repositories.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TodoServiceTest {

    @Mock
    TodoRepository todoRepository;
    @Mock
    FamilyRepository familyRepository;
    @Mock
    FamilyMemberRepository familyMemberRepository;

    FamilyRoleMapper familyRoleMapper = new FamilyRoleMapper();
    FamilyMapper familyMapper = new FamilyMapper(familyRoleMapper);
    TodoMapper todoMapper = new TodoMapper(familyMapper);

    TodoService todoService;

    Family family;
    FamilyMember member;

    @BeforeEach
    void setUp() {
        todoService = new TodoService(todoRepository, familyMemberRepository, familyRepository, todoMapper);

        family = new Family("Heins");
        ReflectionTestUtils.setField(family, "id", 1L);

        member = new FamilyMember("Jacob", FamilyRole.DAD);
        ReflectionTestUtils.setField(member, "id", 10L);
        member.setFamily(family);
    }

    // -------------------------------------------------------
    // addTask
    // -------------------------------------------------------
    @Test
    void addTask_returnsSuccess_whenFamilyExists() {
        when(familyRepository.findById(1L)).thenReturn(Optional.of(family));
        when(todoRepository.save(any())).thenAnswer(i -> {
            Todo t = i.getArgument(0);
            ReflectionTestUtils.setField(t, "id", 42L);
            return t;
        });

        AddTodoRequest req = new AddTodoRequest("Buy milk", null, null, 1L, null);
        Result<TodoResponse> result = todoService.addTask(req);

        assertInstanceOf(Result.Success.class, result);
        TodoResponse response = ((Result.Success<TodoResponse>) result).value();
        assertEquals("Buy milk", response.name());
        assertEquals(1L, response.familyId());
        assertNull(response.assignee());
        verify(todoRepository).save(any(Todo.class));
    }

    @Test
    void addTask_returnsSuccess_withAssignee() {
        when(familyRepository.findById(1L)).thenReturn(Optional.of(family));
        when(familyMemberRepository.findById(10L)).thenReturn(Optional.of(member));
        when(todoRepository.save(any())).thenAnswer(i -> {
            Todo t = i.getArgument(0);
            ReflectionTestUtils.setField(t, "id", 42L);
            return t;
        });

        AddTodoRequest req = new AddTodoRequest("Buy milk", null, null, 1L, 10L);
        Result<TodoResponse> result = todoService.addTask(req);

        assertInstanceOf(Result.Success.class, result);
        TodoResponse response = ((Result.Success<TodoResponse>) result).value();
        assertEquals("Jacob", response.assignee().name());
    }

    @Test
    void addTask_returnsNotFound_whenFamilyMissing() {
        when(familyRepository.findById(99L)).thenReturn(Optional.empty());

        AddTodoRequest req = new AddTodoRequest("Buy milk", null, null, 99L, null);
        Result<TodoResponse> result = todoService.addTask(req);

        assertInstanceOf(Result.Failure.class, result);
        Result.Failure<TodoResponse> failure = (Result.Failure<TodoResponse>) result;
        assertEquals(Result.ErrorType.NOT_FOUND, failure.errorType());
        assertEquals("Family not found: 99", failure.error());
        verify(todoRepository, never()).save(any());
    }

    @Test
    void addTask_returnsNotFound_whenAssigneeMissing() {
        when(familyRepository.findById(1L)).thenReturn(Optional.of(family));
        when(familyMemberRepository.findById(99L)).thenReturn(Optional.empty());

        AddTodoRequest req = new AddTodoRequest("Buy milk", null, null, 1L, 99L);
        Result<TodoResponse> result = todoService.addTask(req);

        assertInstanceOf(Result.Failure.class, result);
        assertEquals("Member not found: 99", ((Result.Failure<TodoResponse>) result).error());
        verify(todoRepository, never()).save(any());
    }

    // -------------------------------------------------------
    // getTasks
    // -------------------------------------------------------
    @Test
    @SuppressWarnings("unchecked")
    void getTasks_returnsTodos_whenFamilyExists() {
        when(familyRepository.existsById(1L)).thenReturn(true);
        Todo todo = new Todo("Buy milk", null, null, family, null);
        ReflectionTestUtils.setField(todo, "id", 1L);
        when(todoRepository.findAll(any(Specification.class))).thenReturn(List.of(todo));

        Result<List<TodoResponse>> result = todoService.getTasks(1L, null);

        assertInstanceOf(Result.Success.class, result);
        List<TodoResponse> todos = ((Result.Success<List<TodoResponse>>) result).value();
        assertEquals(1, todos.size());
        assertEquals("Buy milk", todos.getFirst().name());
    }

    @Test
    void getTasks_returnsNotFound_whenFamilyMissing() {
        when(familyRepository.existsById(99L)).thenReturn(false);

        Result<List<TodoResponse>> result = todoService.getTasks(99L, null);

        assertInstanceOf(Result.Failure.class, result);
        assertEquals("Family not found: 99", ((Result.Failure<List<TodoResponse>>) result).error());
    }

    // -------------------------------------------------------
    // updateTodo
    // -------------------------------------------------------
    @Test
    void updateTodo_returnsSuccess_whenTodoExists() {
        Todo todo = new Todo("Old name", null, null, family, null);
        ReflectionTestUtils.setField(todo, "id", 1L);
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));
        when(todoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        UpdateTodoRequest req = new UpdateTodoRequest(1L, "New name", null, Instant.now().plusSeconds(3600), false,
                null);
        Result<TodoResponse> result = todoService.updateTodo(req);

        assertInstanceOf(Result.Success.class, result);
        assertEquals("New name", ((Result.Success<TodoResponse>) result).value().name());
        verify(todoRepository).save(todo);
    }

    @Test
    void updateTodo_returnsNotFound_whenTodoMissing() {
        when(todoRepository.findById(99L)).thenReturn(Optional.empty());

        UpdateTodoRequest req = new UpdateTodoRequest(99L, "Name", null, null, false, null);
        Result<TodoResponse> result = todoService.updateTodo(req);

        assertInstanceOf(Result.Failure.class, result);
        assertEquals(Result.ErrorType.NOT_FOUND, ((Result.Failure<TodoResponse>) result).errorType());
        assertEquals("Todo not found: 99", ((Result.Failure<TodoResponse>) result).error());
    }

    @Test
    void updateTodo_returnsNotFound_whenAssigneeMissing() {
        when(familyMemberRepository.findById(99L)).thenReturn(Optional.empty());

        UpdateTodoRequest req = new UpdateTodoRequest(1L, "Name", null, null, false, 99L);
        Result<TodoResponse> result = todoService.updateTodo(req);

        assertInstanceOf(Result.Failure.class, result);
        assertEquals("Member not found: 99", ((Result.Failure<TodoResponse>) result).error());
    }

    // -------------------------------------------------------
    // deleteTodo
    // -------------------------------------------------------
    @Test
    void deleteTodo_returnsSuccess_whenTodoExists() {
        when(todoRepository.existsById(1L)).thenReturn(true);

        Result<Void> result = todoService.deleteTodo(1L);

        assertInstanceOf(Result.Success.class, result);
        verify(todoRepository).deleteById(1L);
    }

    @Test
    void deleteTodo_returnsNotFound_whenTodoMissing() {
        when(todoRepository.existsById(99L)).thenReturn(false);

        Result<Void> result = todoService.deleteTodo(99L);

        assertInstanceOf(Result.Failure.class, result);
        assertEquals("Todo not found: 99", ((Result.Failure<Void>) result).error());
        verify(todoRepository, never()).deleteById(any());
    }
}
