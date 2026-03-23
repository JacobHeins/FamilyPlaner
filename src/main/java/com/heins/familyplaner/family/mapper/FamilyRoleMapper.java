package com.heins.familyplaner.family.mapper;

import com.heins.familyplaner.family.dtos.FamilyRoleDto;
import com.heins.familyplaner.family.entities.FamilyRole;
import org.springframework.stereotype.Component;

@Component
public class FamilyRoleMapper {

    public FamilyRoleDto toDto(FamilyRole familyRole) {
        return switch (familyRole) {
            case DAD -> FamilyRoleDto.DAD;
            case MOM -> FamilyRoleDto.MOM;
            case CHILD -> FamilyRoleDto.CHILD;
        };
    }

    public FamilyRole toDomain(FamilyRoleDto familyRoleDto) {
        return switch (familyRoleDto) {
            case DAD -> FamilyRole.DAD;
            case MOM -> FamilyRole.MOM;
            case CHILD -> FamilyRole.CHILD;
        };
    }
}
