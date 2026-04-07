package com.heins.familyplanner.todos.mapper;

import com.heins.familyplanner.family.mapper.FamilyMapper;
import com.heins.familyplanner.todos.dtos.TodoResponse;
import com.heins.familyplanner.todos.entities.Todo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TodoMapper {

    private final FamilyMapper familyMapper;

    public TodoResponse toTodoResponse(Todo todo) {
        return new TodoResponse(
                todo.getId(),
                todo.getName(),
                todo.getDescription(),
                todo.getDueDate(),
                todo.getCompleted(),
                todo.getFamily().getId(),
                todo.getAssignee() != null ? familyMapper.toFamilyMemberResponse(todo.getAssignee()) : null
        );
    }
}
