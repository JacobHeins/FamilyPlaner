package com.heins.familyplanner.activities;

import com.heins.familyplanner.accounts.entities.FamilyAccount;
import com.heins.familyplanner.activities.dtos.ActivityResponse;
import com.heins.familyplanner.activities.dtos.CreateActivityRequest;
import com.heins.familyplanner.activities.dtos.GetActivitiesRequest;
import com.heins.familyplanner.activities.dtos.UpdateActivityRequest;
import com.heins.familyplanner.exceptions.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/activities")
@Tag(name = "Activities", description = "Family management API")
public class ActivitiesController {

    private final ActivitiesService activitiesService;

    @GetMapping
    public ResponseEntity<?> getActivities(@RequestBody @Valid GetActivitiesRequest getActivitiesRequest,
            @AuthenticationPrincipal FamilyAccount account) {

        if (!account.getFamily().getId().equals(getActivitiesRequest.familyId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return switch (activitiesService.getActivities(getActivitiesRequest, account.getId())) {
            case Result.Success<List<ActivityResponse>> s -> ResponseEntity.ok(s.value());
            case Result.Failure<List<ActivityResponse>> f -> ResponseEntity.of(f.toProblemDetail()).build();
        };
    }

    @PostMapping
    public ResponseEntity<?> createActivity(
            @Valid @NotNull @RequestBody CreateActivityRequest createActivityRequest,
            @AuthenticationPrincipal FamilyAccount account) {

        if (!account.getFamily().getId().equals(createActivityRequest.familyId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return switch (activitiesService.createActivity(createActivityRequest, account.getId())) {
            case Result.Success<ActivityResponse> s -> ResponseEntity.status(HttpStatus.CREATED).body(s.value());
            case Result.Failure<ActivityResponse> f -> ResponseEntity.of(f.toProblemDetail()).build();
        };
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updateActivity(
            @PathVariable Long id,
            @Valid @NotNull @RequestBody UpdateActivityRequest updateActivityRequest,
            @AuthenticationPrincipal FamilyAccount account) {
        return switch (activitiesService.updateActivity(id, updateActivityRequest, account.getFamily().getId())) {
            case Result.Success<ActivityResponse> s -> ResponseEntity.status(HttpStatus.CREATED).body(s.value());
            case Result.Failure<ActivityResponse> f -> ResponseEntity.of(f.toProblemDetail()).build();
        };
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteActivity(@PathVariable Long id,
            @AuthenticationPrincipal FamilyAccount account) {
        return switch (activitiesService.deleteActivity(id, account.getFamily().getId())) {
            case Result.Success<Void> _ -> ResponseEntity.noContent().build();
            case Result.Failure<Void> f -> ResponseEntity.of(f.toProblemDetail()).build();
        };
    }

}
