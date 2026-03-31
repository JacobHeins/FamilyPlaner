package com.heins.familyplaner.todos.dtos;

import jakarta.validation.constraints.*;

import java.time.Instant;

public record AddTodoRequest(
        @NotBlank
        @Size(min = 1, max = 100)
        String name,

        @Size(max = 500)
        String description,

        @Future
        Instant deuDate,

        @NotNull
        @Min(1)
        Long familyId,

        @Positive
        @Min(1)
        Long assigneeId
) {
}
