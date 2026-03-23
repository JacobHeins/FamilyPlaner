package com.heins.familyplaner.family.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record AddFamilyMemberRequest(
        @Positive
        Long familyId,

        @NotBlank
        @Size(min = 2, max = 50)
        String name,

        @NotNull
        FamilyRoleDto role)

{
}
