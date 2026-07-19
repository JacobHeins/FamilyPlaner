package com.heins.familyplanner.family;

import com.heins.familyplanner.exceptions.Result;
import com.heins.familyplanner.family.dtos.AddFamilyMemberRequest;
import com.heins.familyplanner.family.dtos.FamilyResponse;
import com.heins.familyplanner.family.dtos.UpdateFamilyRequest;
import com.heins.familyplanner.family.entities.Family;
import com.heins.familyplanner.family.entities.FamilyMember;
import com.heins.familyplanner.family.mapper.FamilyMapper;
import com.heins.familyplanner.family.mapper.FamilyRoleMapper;
import com.heins.familyplanner.family.repositories.FamilyRepository;
import com.heins.familyplanner.family.repositories.FamilyMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public Result<FamilyResponse> updateFamily(Long familyId, Long accountId, UpdateFamilyRequest request) {
        log.debug("Updating family {} with values {}", familyId, request);
        Optional<Family> oldFamilyOpt = familyRepository.findByIdAndAccountId(familyId, accountId);
        if (oldFamilyOpt.isEmpty()) {
            log.warn("Family not found with id: {}", familyId);
            return Result.notFound("Family not found with id: " + familyId);
        }
        Family oldFamily = oldFamilyOpt.get();
        oldFamily.setName(request.name());
        Family newFamily = familyRepository.save(oldFamily);
        return Result.success(familyMapper.toFamilyResponse(newFamily));
    }

    public Result<FamilyResponse> getFamily(Long familyId, Long accountId) {
        log.debug("Fetching family with id: {}", familyId);

        Optional<Family> familyOpt = familyRepository.findByIdAndAccountId(familyId, accountId);
        if (familyOpt.isEmpty()) {
            log.warn("Family not found with id: {}", familyId);
            return Result.notFound("Family not found with id: " + familyId);
        }

        return Result.success(familyMapper.toFamilyResponse(familyOpt.get()));
    }

    @Transactional
    public Result<FamilyResponse> addFamilyMember(Long familyId, Long accountId, AddFamilyMemberRequest request) {
        log.debug("Adding member '{}' to familyId: {}", request.name(), familyId);
        var familyOpt = familyRepository.findByIdAndAccountId(familyId, accountId);
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
