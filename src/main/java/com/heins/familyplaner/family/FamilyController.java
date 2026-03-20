package com.heins.familyplaner.family;

import com.heins.familyplaner.family.dtos.FamilyDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/families")
public class FamilyController {

    private final FamilyService familyService;

    public FamilyController(FamilyService familyService) {
        this.familyService = familyService;
    }

    @GetMapping
    public List<FamilyDto> getAllFamilies(){
        return familyService.getAllFamilies();
    }

    @PostMapping
    public FamilyDto addFamily(@RequestParam String name){
        return familyService.addFamily(name);
    }

    // Add person to a family
    @PostMapping("/{familyId}/members")
    public FamilyDto addPersonToFamily(
            @PathVariable Long familyId,
            @RequestParam String name) {

        return familyService.addPerson(familyId, name);
    }

}
