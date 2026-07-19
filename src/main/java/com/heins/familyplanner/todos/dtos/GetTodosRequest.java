package com.heins.familyplanner.todos.dtos;

public record GetTodosRequest (
        Long familyId,
        Long assigneeId
){
}
