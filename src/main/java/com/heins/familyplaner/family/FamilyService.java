package com.heins.familyplaner.family;

import com.heins.familyplaner.exceptions.Result;
import com.heins.familyplaner.family.dtos.AddFamilyMemberRequest;
import com.heins.familyplaner.family.dtos.FamilyResponse;
import com.heins.familyplaner.family.dtos.UpdateFamilyRequest;
import com.heins.familyplaner.family.entities.Family;
import com.heins.familyplaner.family.entities.FamilyMember;
import com.heins.familyplaner.family.mapper.FamilyMapper;
import com.heins.familyplaner.family.mapper.FamilyRoleMapper;
import com.heins.familyplaner.family.repositories.FamilyRepository;
import com.heins.familyplaner.family.repositories.FamilyMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FamilyService {

    private final FamilyRepository familyRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final FamilyMapper familyMapper;
    private final FamilyRoleMapper familyRoleMapper;

    @Transactional
    public Result<FamilyResponse> addFamily(String name) {
        log.debug("Creating family with name: {}", name);
        Family newFamily = familyRepository.save(new Family(name));
        log.info("Family created with id: {}", newFamily.getId());
        return Result.success(familyMapper.toFamilyResponse(newFamily));
    }

    @Transactional
    public Result<FamilyResponse> updateFamily(Long familyId, UpdateFamilyRequest request) {
        log.debug("Updating family {} with values {}", familyId, request);
        Optional<Family> oldFamilyOpt = familyRepository.findById(familyId);
        if (oldFamilyOpt.isEmpty()) {
            log.warn("Family not found with id: {}", familyId);
            return Result.notFound("Family not found with id: " + familyId);
        }
        Family oldFamily = oldFamilyOpt.get();
        oldFamily.setName(request.name());
        Family newFamily = familyRepository.save(oldFamily);
        return Result.success(familyMapper.toFamilyResponse(newFamily));
    }

    public List<FamilyResponse> getAllFamilies() {
        log.debug("Fetching all families");
        return familyRepository
                .findAll()
                .stream()
                .map(familyMapper::toFamilyResponse)
                .toList();
    }

    @Transactional
    public Result<FamilyResponse> addFamilyMember(Long familyId, AddFamilyMemberRequest request) {
        log.debug("Adding member '{}' to familyId: {}", request.name(), familyId);
        var familyOpt = familyRepository.findById(familyId);
        if (familyOpt.isEmpty()) {
            log.warn("Family not found with id: {}", familyId);
            return Result.notFound("Family not found with id: " + familyId);
        }
        Family family = familyOpt.get();
        FamilyMember familyMember = new FamilyMember(request.name(), familyRoleMapper.toDomain(request.role()));
        family.addMember(familyMember);
        familyMemberRepository.save(familyMember);
        family = familyRepository.save(family);
        log.info("Member added to familyId: {}", family.getId());
        return Result.success(familyMapper.toFamilyResponse(family));
    }
}
