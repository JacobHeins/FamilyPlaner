package com.heins.familyplanner.family.dtos;


import java.util.List;

public record FamilyResponse(Long id, String name, List<FamilyMemberResponse> familyMembers) {}