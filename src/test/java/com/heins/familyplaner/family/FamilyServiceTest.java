package com.heins.familyplaner.family;

import com.heins.familyplaner.family.dtos.FamilyDto;
import com.heins.familyplaner.family.entities.Family;
import com.heins.familyplaner.family.entities.FamilyMember;
import com.heins.familyplaner.family.repositories.FamilyMemberRepository;
import com.heins.familyplaner.family.repositories.FamilyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FamilyServiceTest {

    @Mock
    FamilyRepository familyRepository;

    @Mock
    FamilyMemberRepository familyMemberRepository;

    @InjectMocks
    FamilyService familyService;

    //-------------addFamily-------------
    @Test
    void addFamily_saveAndReturnsDtos() {
        //given
        Family saved = new Family("Heins");
        ReflectionTestUtils.setField(saved, "id", 1L);  // <-- Set ID manually
        when(familyRepository.save(any())).thenReturn(saved);

        //when
        FamilyDto result = familyService.addFamily("Heins");

        //then
        assertEquals("Heins", result.name());
        verify(familyRepository).save(any(Family.class));
    }


    //-------------getAllFamilies---------
    @Test
    void getAllFamilies_returnDtos(){
        //given
        Family heins = new Family("Heins");
        ReflectionTestUtils.setField(heins, "id", 1L);
        Family lehnert = new Family("Lehner");
        ReflectionTestUtils.setField(lehnert, "id", 2L);

        when(familyRepository.findAll()).thenReturn(List.of(heins,lehnert));

        //when
        var result = familyService.getAllFamilies();

        //then
        assertEquals(2, result.size());
        assertEquals("Heins", result.getFirst().name());
        assertEquals("Lehner", result.get(1).name());
        verify(familyRepository).findAll();

    }

    //----------------getFamilyById-----------
    @Test
    void getFamilyById_returnDto_whenFound(){
        //given
        Family saved = new Family("Heins");
        ReflectionTestUtils.setField(saved, "id", 1L);

        when(familyRepository.findById(1L)).thenReturn(Optional.of(saved));

        //when
        FamilyDto result = familyService.getFamilyById(1L);

        //then
        assertEquals("Heins", result.name());
        verify(familyRepository).findById(1L);
    }


    @Test
    void getFamilyById_throwsException_whenNotFound(){
        when(familyRepository.findById(99L)).thenReturn(Optional.empty());

        //when
        RuntimeException ex = assertThrows(RuntimeException.class,
                ()-> familyService.getFamilyById(99L));

        //then
        assertEquals("Family not found: 99", ex.getMessage());
    }

    //----------------getFamilyByName-----------
    @Test
    void getFamilyByName_returnDto_whenFound(){
        Family saved = new Family("Heins");
        ReflectionTestUtils.setField(saved, "id", 1L);

        when(familyRepository.findByName("Heins")).thenReturn(Optional.of(saved));

        FamilyDto result = familyService.getFamilyByName("Heins");

        assertEquals("Heins", result.name());
        verify(familyRepository).findByName("Heins");
    }

    @Test
    void getFamilyByName_throwsException_whenNotFound(){
        when(familyRepository.findByName("Heins")).thenReturn(Optional.empty());

        // when
        RuntimeException ex = assertThrows(RuntimeException.class,
                ()-> familyService.getFamilyByName("Heins"));

        //then
        assertEquals("Family not found: Heins", ex.getMessage());
    }

    //----------------addFamilyMembers-----------
    @Test
    void addFamilyMember_addsFamilyMemberAndSavesFamily(){
        Family family = new Family("Heins");
        ReflectionTestUtils.setField(family, "id", 1L);
        when(familyRepository.findById(1L)).thenReturn(Optional.of(family));

        when(familyMemberRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(familyRepository.save(any())).thenAnswer(i -> i.getArgument(0));


        FamilyDto result = familyService.addFamilyMember(1L, "Jacob");

        assertEquals("Heins", result.name());
        assertEquals(1, result.familyMembers().size());
        assertEquals("Jacob", result.familyMembers().getFirst().name());

        verify(familyMemberRepository).save(any(FamilyMember.class));
        verify(familyRepository).save(family);
    }

    @Test
    void addFamilyMember_throwsException_whenFamilyMissing(){
        when(familyRepository.findById(123L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> familyService.addFamilyMember(123L, "Jacob"));

        assertEquals("Family not found: 123", ex.getMessage());
    }

}
