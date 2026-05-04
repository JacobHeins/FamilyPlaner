package com.heins.familyplanner.family;

import com.heins.familyplanner.accounts.entities.FamilyAccount;
import com.heins.familyplanner.exceptions.Result;
import com.heins.familyplanner.family.dtos.AddFamilyMemberRequest;
import com.heins.familyplanner.family.dtos.FamilyResponse;
import com.heins.familyplanner.family.dtos.UpdateFamilyRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/families")
@Tag(name = "Family", description = "Family management API")
public class FamilyController {

    private final FamilyService familyService;

    public FamilyController(FamilyService familyService) {
        this.familyService = familyService;
    }

    @Operation(summary = "Get the family information for a specific family")
    @ApiResponse(responseCode = "200", description = "List of all families")
    @GetMapping("/{id}")
    public ResponseEntity<?> getFamily(
            @PathVariable Long id,
            @AuthenticationPrincipal FamilyAccount account) {

        if (!account.getFamily().getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return switch (familyService.getFamily(id, account.getId())) {
            case Result.Success<FamilyResponse> s -> ResponseEntity.status(HttpStatus.OK).body(s.value());
            case Result.Failure<FamilyResponse> f -> ResponseEntity.of(f.toProblemDetail()).build();
        };
    }

    @Operation(summary = "Update a family e.g. rename it")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateFamily(
            @PathVariable Long id,
            @RequestBody @NonNull @Valid UpdateFamilyRequest request,
            @AuthenticationPrincipal FamilyAccount account) {

        if (!account.getFamily().getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return switch (familyService.updateFamily(id, account.getId(), request)) {
            case Result.Success<FamilyResponse> s -> ResponseEntity.status(HttpStatus.CREATED).body(s.value());
            case Result.Failure<FamilyResponse> f -> ResponseEntity.of(f.toProblemDetail()).build();
        };
    }

    @Operation(summary = "Add a new member to a family")
    @PostMapping("/{id}/members")
    public ResponseEntity<?> addPersonToFamily(
            @PathVariable Long id,
            @RequestBody @NonNull @Valid AddFamilyMemberRequest request,
            @AuthenticationPrincipal FamilyAccount account) {

        if (!account.getFamily().getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return switch (familyService.addFamilyMember(id, account.getId(), request)) {
            case Result.Success<FamilyResponse> s -> ResponseEntity.status(HttpStatus.CREATED).body(s.value());
            case Result.Failure<FamilyResponse> f -> ResponseEntity.of(f.toProblemDetail()).build();
        };
    }
}
