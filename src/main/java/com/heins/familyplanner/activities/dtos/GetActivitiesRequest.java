package com.heins.familyplanner.activities.dtos;

public record GetActivitiesRequest(
        Long familyId,
        Long familyMemberId) {
}
