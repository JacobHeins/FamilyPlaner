package com.heins.familyplaner.activities.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record CreateActivityRequest(
        @NotNull
        @Size(min = 2, max = 50)
        String name,

        @Size(min = 2, max = 300)
        String description,

        @Size(min = 2, max = 50)
        String location,

        @FutureOrPresent
        LocalDate day,

        LocalTime startTime,
        LocalTime endtime,

        @Min(1)
        Long familyId,

        @Valid
        List<@NotNull @Min(1) Long> participants
) {
}
