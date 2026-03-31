package com.heins.familyplaner.todos;

import com.heins.familyplaner.exceptions.Result;
import com.heins.familyplaner.todos.dtos.AddTodoRequest;
import com.heins.familyplaner.todos.dtos.TodoResponse;
import com.heins.familyplaner.todos.dtos.UpdateTodoRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/todos")
@Tag(name = "Todo", description = "Todo management API")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    ResponseEntity<?> getTodos(@RequestParam Long familyId,
            @RequestParam(required = false) Long assigneeId) {
        return switch (todoService.getTasks(familyId, assigneeId)) {
            case Result.Success<List<TodoResponse>> s -> ResponseEntity.ok(s.value());
            case Result.Failure<List<TodoResponse>> f -> ResponseEntity.of(f.toProblemDetail()).build();
        };
    }

    @PostMapping
    ResponseEntity<?> addTodo(@RequestBody @Valid AddTodoRequest addTodoRequest) {
        return switch (todoService.addTask(addTodoRequest)) {
            case Result.Success<TodoResponse> s -> ResponseEntity.status(HttpStatus.CREATED).body(s.value());
            case Result.Failure<TodoResponse> f -> ResponseEntity.of(f.toProblemDetail()).build();
        };
    }

    @PutMapping
    ResponseEntity<?> updateTodo(@RequestBody @Valid UpdateTodoRequest updateTodoRequest) {
        return switch (todoService.updateTodo(updateTodoRequest)) {
            case Result.Success<TodoResponse> s -> ResponseEntity.ok(s.value());
            case Result.Failure<TodoResponse> f -> ResponseEntity.of(f.toProblemDetail()).build();
        };
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteTodo(@PathVariable Long id) {
        return switch (todoService.deleteTodo(id)) {
            case Result.Success<Void> _ -> ResponseEntity.noContent().build();
            case Result.Failure<Void> f -> ResponseEntity.of(f.toProblemDetail()).build();
        };
    }
}
