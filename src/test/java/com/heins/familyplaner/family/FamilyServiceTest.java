package com.heins.familyplaner.family;

import com.heins.familyplaner.exceptions.Result;
import com.heins.familyplaner.family.dtos.AddFamilyMemberRequest;
import com.heins.familyplaner.family.dtos.FamilyResponse;
import com.heins.familyplaner.family.dtos.FamilyRoleDto;
import com.heins.familyplaner.family.entities.Family;
import com.heins.familyplaner.family.entities.FamilyMember;
import com.heins.familyplaner.family.mapper.FamilyMapper;
import com.heins.familyplaner.family.mapper.FamilyRoleMapper;
import com.heins.familyplaner.family.repositories.FamilyMemberRepository;
import com.heins.familyplaner.family.repositories.FamilyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FamilyServiceTest {

    @Mock
    FamilyRepository familyRepository;

    @Mock
    FamilyMemberRepository familyMemberRepository;

    FamilyRoleMapper familyRoleMapper = new FamilyRoleMapper();
    FamilyMapper familyMapper = new FamilyMapper(familyRoleMapper);

    FamilyService familyService;

    @BeforeEach
    void setUp() {
        familyService = new FamilyService(
                familyRepository,
                familyMemberRepository,
                familyMapper,
                familyRoleMapper
        );
    }

    //-------------addFamily-------------
    @Test
    void addFamily_saveAndReturnsDtos() {
        Family saved = new Family("Heins");
        ReflectionTestUtils.setField(saved, "id", 1L);
        when(familyRepository.save(any())).thenReturn(saved);

        Result<FamilyResponse> result = familyService.addFamily("Heins");

        assertInstanceOf(Result.Success.class, result);
        assertEquals("Heins", ((Result.Success<FamilyResponse>) result).value().name());
        verify(familyRepository).save(any(Family.class));
    }

    //-------------getAllFamilies---------
    @Test
    void getAllFamilies_returnDtos() {
        Family heins = new Family("Heins");
        ReflectionTestUtils.setField(heins, "id", 1L);
        Family lehnert = new Family("Lehner");
        ReflectionTestUtils.setField(lehnert, "id", 2L);

        when(familyRepository.findAll()).thenReturn(List.of(heins, lehnert));

        var result = familyService.getAllFamilies();

        assertEquals(2, result.size());
        assertEquals("Heins", result.getFirst().name());
        assertEquals("Lehner", result.get(1).name());
        verify(familyRepository).findAll();
    }

    //----------------addFamilyMembers-----------
    @Test
    void addFamilyMember_addsFamilyMemberAndSavesFamily() {
        Family family = new Family("Heins");
        ReflectionTestUtils.setField(family, "id", 1L);
        when(familyRepository.findById(1L)).thenReturn(Optional.of(family));
        when(familyMemberRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(familyRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        AddFamilyMemberRequest request = new AddFamilyMemberRequest(1L, "Jacob", FamilyRoleDto.DAD);
        Result<FamilyResponse> result = familyService.addFamilyMember(request);

        assertInstanceOf(Result.Success.class, result);
        FamilyResponse response = ((Result.Success<FamilyResponse>) result).value();
        assertEquals("Heins", response.name());
        assertEquals(1, response.familyMembers().size());
        assertEquals("Jacob", response.familyMembers().getFirst().name());
        assertEquals(FamilyRoleDto.DAD, response.familyMembers().getFirst().role());

        verify(familyMemberRepository).save(any(FamilyMember.class));
        verify(familyRepository).save(family);
    }

    @Test
    void addFamilyMember_returnsNotFound_whenFamilyMissing() {
        when(familyRepository.findById(123L)).thenReturn(Optional.empty());

        AddFamilyMemberRequest request = new AddFamilyMemberRequest(123L, "Jacob", FamilyRoleDto.DAD);
        Result<FamilyResponse> result = familyService.addFamilyMember(request);

        assertInstanceOf(Result.Failure.class, result);
        Result.Failure<FamilyResponse> failure = (Result.Failure<FamilyResponse>) result;
        assertEquals(Result.ErrorType.NOT_FOUND, failure.errorType());
        assertEquals("Family not found: 123", failure.error());
    }
}
