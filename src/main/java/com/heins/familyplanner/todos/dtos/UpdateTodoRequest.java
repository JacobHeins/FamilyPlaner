package com.heins.familyplanner.todos.dtos;

import jakarta.validation.constraints.*;

import java.time.Instant;

public record UpdateTodoRequest(
        @NotBlank
        @Size(min = 1, max = 100)
        String name,

        @Size(max = 500)
        String description,

        @Future
        Instant deuDate,

        @NotNull
        Boolean completed,

        @Positive
        @Min(1)
        Long assigneeId
) {
}
