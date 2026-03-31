package com.heins.familyplaner.family;

import com.heins.familyplaner.exceptions.Result;
import com.heins.familyplaner.family.dtos.AddFamilyMemberRequest;
import com.heins.familyplaner.family.dtos.AddFamilyRequestRequest;
import com.heins.familyplaner.family.dtos.FamilyResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public List<FamilyResponse> getAllFamilies() {
        return familyService.getAllFamilies();
    }

    @Operation(summary = "Create a new family")
    @PostMapping
    public ResponseEntity<?> addFamily(
            @RequestBody @NonNull @Valid AddFamilyRequestRequest request) {
        return switch (familyService.addFamily(request.name())) {
            case Result.Success<FamilyResponse> s -> ResponseEntity.status(HttpStatus.CREATED).body(s.value());
            case Result.Failure<FamilyResponse> f -> ResponseEntity.of(f.toProblemDetail()).build();
        };
    }

    @Operation(summary = "Add a new member to a family")
    @PostMapping("/members")
    public ResponseEntity<?> addPersonToFamily(
            @RequestBody @NonNull @Valid AddFamilyMemberRequest request) {
        return switch (familyService.addFamilyMember(request)) {
            case Result.Success<FamilyResponse> s -> ResponseEntity.status(HttpStatus.CREATED).body(s.value());
            case Result.Failure<FamilyResponse> f -> ResponseEntity.of(f.toProblemDetail()).build();
        };
    }
}
