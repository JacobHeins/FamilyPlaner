package com.heins.familyplanner.activities.mapper;

import com.heins.familyplanner.activities.dtos.ActivityResponse;
import com.heins.familyplanner.activities.entities.Activity;
import com.heins.familyplanner.family.mapper.FamilyMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ActivityMapper {
    private final FamilyMapper familyMapper;

    public ActivityResponse toActivityResponse(Activity activity) {
        return new ActivityResponse(
                activity.getId(),
                activity.getName(),
                activity.getDescription(),
                activity.getLocation(),
                activity.getDay(),
                activity.getStartTime(),
                activity.getEndTime(),
                activity.getFamily().getId(),
                activity.getParticipants().stream().map(familyMapper::toFamilyMemberResponse).toList()
        );
    }
}
