package com.heins.familyplaner.family.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddFamilyRequestDto(
        @NotBlank
        @Size(min=2, max = 50)
        String name) {
}
