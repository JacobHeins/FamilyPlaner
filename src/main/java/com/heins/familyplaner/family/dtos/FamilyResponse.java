package com.heins.familyplaner.family.dtos;


import java.util.List;

public record FamilyResponse(Long id, String name, List<FamilyMemberResponse> familyMembers) {}