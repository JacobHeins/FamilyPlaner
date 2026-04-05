package com.heins.familyplaner.todos;

import com.heins.familyplaner.exceptions.Result;
import com.heins.familyplaner.family.entities.FamilyMember;
import com.heins.familyplaner.family.repositories.FamilyMemberRepository;
import com.heins.familyplaner.family.repositories.FamilyRepository;
import com.heins.familyplaner.todos.dtos.AddTodoRequest;
import com.heins.familyplaner.todos.dtos.TodoResponse;
import com.heins.familyplaner.todos.dtos.UpdateTodoRequest;
import com.heins.familyplaner.todos.entities.Todo;
import com.heins.familyplaner.todos.mapper.TodoMapper;
import com.heins.familyplaner.todos.repositories.TodoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TodoService {

    private final TodoRepository todoRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final FamilyRepository familyRepository;
    private final TodoMapper todoMapper;

    @Transactional
    public Result<TodoResponse> addTask(AddTodoRequest addTodoRequest) {
        log.debug("Adding todo '{}' for familyId: {}", addTodoRequest.name(), addTodoRequest.familyId());

        var familyOpt = familyRepository.findById(addTodoRequest.familyId());
        if (familyOpt.isEmpty()) {
            log.warn("Family not found: {}", addTodoRequest.familyId());
            return Result.notFound("Family not found: " + addTodoRequest.familyId());
        }

        FamilyMember assignee = null;
        if (addTodoRequest.assigneeId() != null) {
            var assigneeOpt = familyMemberRepository.findById(addTodoRequest.assigneeId());
            if (assigneeOpt.isEmpty()) {
                log.warn("Assignee not found: {}", addTodoRequest.assigneeId());
                return Result.notFound("Member not found: " + addTodoRequest.assigneeId());
            }
            assignee = assigneeOpt.get();
        }

        Todo todo = new Todo(
                addTodoRequest.name(),
                addTodoRequest.description(),
                addTodoRequest.deuDate(),
                familyOpt.get(),
                assignee);

        todoRepository.save(todo);
        log.info("Todo created with id: {}", todo.getId());
        return Result.success(todoMapper.toTodoResponse(todo));
    }

    public Result<List<TodoResponse>> getTasks(Long familyId, Long memberId) {
        log.debug("Fetching todos for familyId: {}, memberId: {}", familyId, memberId);

        if (!familyRepository.existsById(familyId)) {
            log.warn("Family not found: {}", familyId);
            return Result.notFound("Family not found: " + familyId);
        }

        Specification<Todo> spec = (_, _, _) -> null;

        spec = spec.and((root, _, cb) ->
                cb.equal(root.get("family").get("id"), familyId));

        if (memberId != null) {
            spec = spec.and((root, _, cb) ->
                    cb.equal(root.get("assignee").get("id"), memberId));
        }

        return Result.success(todoRepository
                .findAll(spec)
                .stream()
                .map(todoMapper::toTodoResponse)
                .toList());
    }

    @Transactional
    public Result<TodoResponse> updateTodo(UpdateTodoRequest req) {
        log.debug("Updating todo id: {}", req.id());

        FamilyMember assignee = null;
        if (req.assigneeId() != null) {
            var assigneeOpt = familyMemberRepository.findById(req.assigneeId());
            if (assigneeOpt.isEmpty()) {
                log.warn("Assignee not found: {}", req.assigneeId());
                return Result.notFound("Member not found: " + req.assigneeId());
            }
            assignee = assigneeOpt.get();
        }

        var todoOpt = todoRepository.findById(req.id());
        if (todoOpt.isEmpty()) {
            log.warn("Todo not found: {}", req.id());
            return Result.notFound("Todo not found: " + req.id());
        }

        Todo todo = todoOpt.get();
        todo.update(req.name(), req.description(), req.deuDate(), req.completed(), assignee);
        todo = todoRepository.save(todo);
        log.info("Todo updated: {}", todo.getId());
        return Result.success(todoMapper.toTodoResponse(todo));
    }

    @Transactional
    public Result<Void> deleteTodo(Long id) {
        log.debug("Deleting todo id: {}", id);

        if (!todoRepository.existsById(id)) {
            log.warn("Todo not found: {}", id);
            return Result.notFound("Todo not found: " + id);
        }
        todoRepository.deleteById(id);
        log.info("Todo deleted: {}", id);
        return Result.success(null);
    }
}

