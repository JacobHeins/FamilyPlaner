package com.heins.familyplaner.family;

import com.heins.familyplaner.family.dtos.AddFamilyMemberRequest;
import com.heins.familyplaner.family.dtos.FamilyDto;
import com.heins.familyplaner.family.entities.Family;
import com.heins.familyplaner.family.entities.FamilyMember;
import com.heins.familyplaner.family.mapper.FamilyMapper;
import com.heins.familyplaner.family.mapper.FamilyRoleMapper;
import com.heins.familyplaner.family.repositories.FamilyRepository;
import com.heins.familyplaner.family.repositories.FamilyMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FamilyService {

    private final FamilyRepository familyRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final FamilyMapper familyMapper;
    private final FamilyRoleMapper familyRoleMapper;


    public FamilyDto addFamily(String name) {
        Family newFamily = familyRepository.save(new Family(name));
        return familyMapper.toDto(newFamily);
    }

    public List<FamilyDto> getAllFamilies() {
        return familyRepository
                .findAll()
                .stream()
                .map(familyMapper::toDto)
                .toList();
    }

    public FamilyDto getFamilyById(Long id) {
        Family family = familyRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Family not found: " + id));

        return familyMapper.toDto(family);
    }

    private Family getFamily(long id) {
        return familyRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Family not found: " + id));
    }

    public FamilyDto getFamilyByName(String name) {
        Family family = familyRepository
                .findByName(name)
                .orElseThrow(()-> new RuntimeException("Family not found: " + name));
        return familyMapper.toDto(family);
    }

    public FamilyDto addFamilyMember(AddFamilyMemberRequest request) {
        Family family = getFamily(request.familyId());
        FamilyMember familyMember = new FamilyMember(request.name(), familyRoleMapper.toDomain(request.role()));
        family.addMember(familyMember);
        familyMemberRepository.save(familyMember);
        family =  familyRepository.save(family);
        return familyMapper.toDto(family);
    }

}
