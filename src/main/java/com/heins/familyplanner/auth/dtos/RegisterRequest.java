package com.heins.familyplanner.auth.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Family Slug is required")
        @Size(min = 3, max = 50)
        String familySlug,

        @NotBlank(message = "Family Name is required")
        @Size(min = 2, max = 100)
        String familyName,

        @NotBlank(message = "Password in required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password
) {
}
