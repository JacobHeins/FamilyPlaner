package com.heins.familyplanner.auth.dtos;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Family slug is required")
        String familySlug,

        @NotBlank(message = "Password is required")
        String password
) {
}
