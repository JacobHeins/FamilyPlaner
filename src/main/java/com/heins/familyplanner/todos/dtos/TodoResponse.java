package com.heins.familyplanner.todos.dtos;

import com.heins.familyplanner.family.dtos.FamilyMemberResponse;

import java.time.Instant;

public record TodoResponse(Long id,
                           String name,
                           String description,
                           Instant dueDate,
                           Boolean completed,
                           Long familyId,
                           FamilyMemberResponse assignee) {
}
