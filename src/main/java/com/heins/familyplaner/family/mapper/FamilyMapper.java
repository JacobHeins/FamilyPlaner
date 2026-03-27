package com.heins.familyplaner.family.mapper;


import com.heins.familyplaner.family.dtos.FamilyDto;
import com.heins.familyplaner.family.dtos.FamilyMemberDto;
import com.heins.familyplaner.family.entities.Family;
import com.heins.familyplaner.family.entities.FamilyMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FamilyMapper {

    private final FamilyRoleMapper familyRoleMapper;

    public FamilyDto toDto(Family family) {
        return new FamilyDto(
                family.getId(),
                family.getName(),
                family.getFamilyMembers()
                        .stream()
                        .map(this::toDto)
                        .collect(Collectors.toList())
        );
    }

    public FamilyMemberDto toDto(FamilyMember familyMember) {
        return new FamilyMemberDto(
                familyMember.getId(),
                familyMember.getName(),
                familyRoleMapper.toDto(familyMember.getRole())
        );
    }
}