package com.heins.familyplanner.family.mapper;


import com.heins.familyplanner.family.dtos.FamilyResponse;
import com.heins.familyplanner.family.dtos.FamilyMemberResponse;
import com.heins.familyplanner.family.entities.Family;
import com.heins.familyplanner.family.entities.FamilyMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FamilyMapper {

    private final FamilyRoleMapper familyRoleMapper;

    public FamilyResponse toFamilyResponse(Family family) {
        return new FamilyResponse(
                family.getId(),
                family.getName(),
                family.getFamilyMembers()
                        .stream()
                        .map(this::toFamilyMemberResponse)
                        .collect(Collectors.toList())
        );
    }

    public FamilyMemberResponse toFamilyMemberResponse(FamilyMember familyMember) {
        return new FamilyMemberResponse(
                familyMember.getId(),
                familyMember.getName(),
                familyRoleMapper.toDto(familyMember.getRole())
        );
    }
}