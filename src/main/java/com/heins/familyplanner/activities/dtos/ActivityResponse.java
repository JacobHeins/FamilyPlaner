package com.heins.familyplanner.activities.dtos;

import com.heins.familyplanner.family.dtos.FamilyMemberResponse;
import com.heins.familyplanner.family.dtos.FamilyResponse;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record ActivityResponse(
        Long id,
        String name,
        String description,
        String location,
        LocalDate day,
        LocalTime startTime,
        LocalTime endTime,
        Long familyId,
        List<FamilyMemberResponse> participants
) {
}
