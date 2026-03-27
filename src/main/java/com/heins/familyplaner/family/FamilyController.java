package com.heins.familyplaner.family;

import com.heins.familyplaner.family.dtos.AddFamilyMemberRequest;
import com.heins.familyplaner.family.dtos.AddFamilyRequestRequest;
import com.heins.familyplaner.family.dtos.FamilyDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.jspecify.annotations.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/families")
@Tag(name = "Family", description = "Family management API")
public class FamilyController {

    private final FamilyService familyService;

    public FamilyController(FamilyService familyService) {
        this.familyService = familyService;
    }

    @Operation(summary = "Get all families")
    @ApiResponse(responseCode = "200", description = "List of all families")
    @GetMapping
    public List<FamilyDto> getAllFamilies(){
        return familyService.getAllFamilies();
    }

    @Operation(summary = "Create a new family")
    @PostMapping
    public FamilyDto addFamily(
            @RequestBody @NonNull @Valid AddFamilyRequestRequest request){
        return familyService.addFamily(request.name());
    }

    // Add person to a family
    @Operation(summary = "Add a new member to a family")
    @PostMapping("/members")
    public FamilyDto addPersonToFamily(
            @RequestBody @NonNull @Valid AddFamilyMemberRequest request) {

        return familyService.addFamilyMember(request);
    }

}
