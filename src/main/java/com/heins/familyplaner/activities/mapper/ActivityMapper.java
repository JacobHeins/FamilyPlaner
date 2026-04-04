package com.heins.familyplaner.activities.mapper;

import com.heins.familyplaner.activities.dtos.ActivityResponse;
import com.heins.familyplaner.activities.entities.Activity;
import com.heins.familyplaner.family.mapper.FamilyMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ActivityMapper {
    private final FamilyMapper familyMapper;

    public ActivityResponse toActivityResponse(Activity activity) {
        return new ActivityResponse(
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
