package com.heins.familyplaner.todos.dtos;

import com.heins.familyplaner.family.dtos.FamilyMemberResponse;

import java.time.Instant;

public record TodoResponse(Long id,
                           String name,
                           String description,
                           Instant dueDate,
                           Boolean completed,
                           Long familyId,
                           FamilyMemberResponse assignee) {
}
