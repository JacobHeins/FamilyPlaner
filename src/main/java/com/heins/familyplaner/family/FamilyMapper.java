package com.heins.familyplaner.family;


import com.heins.familyplaner.family.dtos.FamilyDto;
import com.heins.familyplaner.family.dtos.PersonDto;
import com.heins.familyplaner.family.entities.Family;
import com.heins.familyplaner.family.entities.Person;

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

    public static PersonDto toDto(Person person) {
        return new PersonDto(
                person.getId(),
                person.getName()
        );
    }
}