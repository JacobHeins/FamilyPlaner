package com.heins.familyplanner.todos;

import com.heins.familyplanner.accounts.entities.FamilyAccount;
import com.heins.familyplanner.exceptions.Result;
import com.heins.familyplanner.todos.dtos.AddTodoRequest;
import com.heins.familyplanner.todos.dtos.GetTodosRequest;
import com.heins.familyplanner.todos.dtos.TodoResponse;
import com.heins.familyplanner.todos.dtos.UpdateTodoRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/todos")
@Tag(name = "Todo", description = "Todo management API")
public class TodoController {

    private final TodoService todoService;

    @GetMapping
    ResponseEntity<?> getTodos(@RequestBody @Valid GetTodosRequest getTodosRequest,
            @AuthenticationPrincipal FamilyAccount account) {

        if (!account.getFamily().getId().equals(getTodosRequest.familyId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return switch (todoService.getTasks(getTodosRequest)) {
            case Result.Success<List<TodoResponse>> s -> ResponseEntity.ok(s.value());
            case Result.Failure<List<TodoResponse>> f -> ResponseEntity.of(f.toProblemDetail()).build();
        };
    }

    @PostMapping
    ResponseEntity<?> addTodo(@RequestBody @Valid AddTodoRequest addTodoRequest,
            @AuthenticationPrincipal FamilyAccount account) {

        if (!account.getFamily().getId().equals(addTodoRequest.familyId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return switch (todoService.addTask(addTodoRequest)) {
            case Result.Success<TodoResponse> s -> ResponseEntity.status(HttpStatus.CREATED).body(s.value());
            case Result.Failure<TodoResponse> f -> ResponseEntity.of(f.toProblemDetail()).build();
        };
    }

    @PutMapping("/{id}")
    ResponseEntity<?> updateTodo(@PathVariable Long id,
            @RequestBody @Valid UpdateTodoRequest updateTodoRequest,
            @AuthenticationPrincipal FamilyAccount account) {

        return switch (todoService.updateTodo(id, updateTodoRequest, account.getFamily().getId())) {
            case Result.Success<TodoResponse> s -> ResponseEntity.ok(s.value());
            case Result.Failure<TodoResponse> f -> ResponseEntity.of(f.toProblemDetail()).build();
        };
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteTodo(@PathVariable Long id,
            @AuthenticationPrincipal FamilyAccount account) {
        return switch (todoService.deleteTodo(id, account.getFamily().getId())) {
            case Result.Success<Void> _ -> ResponseEntity.noContent().build();
            case Result.Failure<Void> f -> ResponseEntity.of(f.toProblemDetail()).build();
        };
    }
}
