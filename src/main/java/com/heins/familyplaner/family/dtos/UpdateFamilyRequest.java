package com.heins.familyplaner.family.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateFamilyRequest(
        @NotNull
        @Size(min=2, max = 50)
        String name
) { }
