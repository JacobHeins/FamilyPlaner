package com.heins.familyplaner.family.dtos;


import java.util.List;

public record FamilyDto(Long id, String name, List<FamilyMemberDto> familyMembers) {}