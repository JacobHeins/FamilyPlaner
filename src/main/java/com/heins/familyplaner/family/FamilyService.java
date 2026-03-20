package com.heins.familyplaner.family;

import com.heins.familyplaner.family.dtos.FamilyDto;
import com.heins.familyplaner.family.entities.Family;
import com.heins.familyplaner.family.entities.Person;
import com.heins.familyplaner.family.repositories.FamilyRepository;
import com.heins.familyplaner.family.repositories.PersonRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FamilyService {

    private final FamilyRepository familyRepository;
    private final PersonRepository personRepository;

    public FamilyService(FamilyRepository familyRepository, PersonRepository personRepository) {
        this.familyRepository = familyRepository;
        this.personRepository = personRepository;
    }

    public FamilyDto addFamily(String name) {
        Family newFamily = familyRepository.save(new Family(name));
        return FamilyMapper.toDto(newFamily);
    }

    public List<FamilyDto> getAllFamilies() {
        return familyRepository
                .findAll()
                .stream()
                .map(FamilyMapper::toDto)
                .toList();
    }

    public FamilyDto getFamilyById(Long id) {
        Family family = familyRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Family not found: " + id));

        return FamilyMapper.toDto(family);
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
        return FamilyMapper.toDto(family);
    }

    public FamilyDto addPerson(long familyId, String name) {
        Family family = getFamily(familyId);
        Person person = new Person(name);
        family.addPerson(person);
        personRepository.save(person);
        family =  familyRepository.save(family);
        return FamilyMapper.toDto(family);
    }

}
