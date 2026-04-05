package com.heins.familyplaner.activities;

import com.heins.familyplaner.activities.dtos.ActivityResponse;
import com.heins.familyplaner.activities.dtos.CreateActivityRequest;
import com.heins.familyplaner.activities.dtos.UpdateActivityRequest;
import com.heins.familyplaner.exceptions.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/activities")
@Tag(name = "Activities", description = "Family management API")
public class ActivitiesController {

    private final ActivitiesService activitiesService;

    @GetMapping
    public ResponseEntity<?> getActivities(
            @RequestParam Long familyId,
            @RequestParam(required = false) Long memberId
    ) {
        return switch (activitiesService.getActivities(familyId, memberId)) {
            case Result.Success<List<ActivityResponse>> s -> ResponseEntity.ok(s.value());
            case Result.Failure<List<ActivityResponse>> f -> ResponseEntity.of(f.toProblemDetail()).build();
        };
    }

    @PostMapping
    public ResponseEntity<?> createActivity(
            @Valid @NotNull @RequestBody CreateActivityRequest createActivityRequest
    ) {
        return switch (activitiesService.createActivity(createActivityRequest)) {
            case Result.Success<ActivityResponse> s -> ResponseEntity.status(HttpStatus.CREATED).body(s.value());
            case Result.Failure<ActivityResponse> f-> ResponseEntity.of(f.toProblemDetail()).build();
        };
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updateActivity(
            @PathVariable Long id,
            @Valid @NotNull @RequestBody UpdateActivityRequest updateActivityRequest
    ) {
        return switch (activitiesService.updateActivity(id, updateActivityRequest)){
            case Result.Success<ActivityResponse> s -> ResponseEntity.status(HttpStatus.CREATED).body(s.value());
            case Result.Failure<ActivityResponse> f -> ResponseEntity.of(f.toProblemDetail()).build();
        };
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteActivity(@PathVariable Long id) {
        return switch (activitiesService.deleteActivity(id)) {
            case Result.Success<Void> _ -> ResponseEntity.noContent().build();
            case Result.Failure<Void> f -> ResponseEntity.of(f.toProblemDetail()).build();
        };
    }


}
