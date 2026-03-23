package com.heins.familyplaner.family;


import com.heins.familyplaner.family.dtos.FamilyDto;
import com.heins.familyplaner.family.dtos.FamilyMemberDto;
import com.heins.familyplaner.family.entities.Family;
import com.heins.familyplaner.family.entities.FamilyMember;

import java.util.stream.Collectors;

public class FamilyMapper {

    public static FamilyDto toDto(Family family) {
        return new FamilyDto(
                family.getId(),
                family.getName(),
                family.getFamilyMembers()
                        .stream()
                        .map(FamilyMapper::toDto)
                        .collect(Collectors.toList())
        );
    }

    public static FamilyMemberDto toDto(FamilyMember familyMember) {
        return new FamilyMemberDto(
                familyMember.getId(),
                familyMember.getName()
        );
    }
}