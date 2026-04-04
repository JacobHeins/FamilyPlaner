package com.heins.familyplaner.activities;

import com.heins.familyplaner.activities.dtos.ActivityResponse;
import com.heins.familyplaner.activities.dtos.CreateActivityRequest;
import com.heins.familyplaner.activities.dtos.UpdateActivityRequest;
import com.heins.familyplaner.activities.entities.Activity;
import com.heins.familyplaner.activities.mapper.ActivityMapper;
import com.heins.familyplaner.activities.repositories.ActivitiesRepository;
import com.heins.familyplaner.exceptions.Result;
import com.heins.familyplaner.family.entities.Family;
import com.heins.familyplaner.family.entities.FamilyMember;
import com.heins.familyplaner.family.repositories.FamilyMemberRepository;
import com.heins.familyplaner.family.repositories.FamilyRepository;
import com.heins.familyplaner.todos.entities.Todo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ActivitiesService {
    private final ActivitiesRepository activitiesRepository;
    private final FamilyRepository familyRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final ActivityMapper activityMapper;

    @Transactional
    public Result<ActivityResponse> createActivity(CreateActivityRequest createActivityRequest) {
        log.debug("Add a new activity");

        Optional<Family> familyOpt = familyRepository.findById(createActivityRequest.familyId());
        if(familyOpt.isEmpty()){
            log.error("Family with id: {} not found.", createActivityRequest.familyId());
            return Result.notFound("Family with id: " + createActivityRequest.familyId() + " not found.");
        }

        List<FamilyMember> participants = new ArrayList<>();
        if(createActivityRequest.participants() != null){
            for(Long participantId : createActivityRequest.participants()){
                Optional<FamilyMember> familyMemberOpt = familyMemberRepository.findById(participantId);

                if(familyMemberOpt.isEmpty()){
                    log.warn("Family member with id: {} not found and will not be added. ", participantId);
                    continue;
                }

                familyMemberOpt.ifPresent(participants::add);
            }
        }

        Activity newActivity = new Activity(createActivityRequest.name(),
                createActivityRequest.description(),
                createActivityRequest.location(),
                createActivityRequest.day(),
                createActivityRequest.startTime(),
                createActivityRequest.endtime(),
                familyOpt.get(),
                participants);

        newActivity = activitiesRepository.save(newActivity);

        return Result.success(activityMapper.toActivityResponse(newActivity));
    }

    public Result<List<ActivityResponse>> getActivities(Long familyId, Long familyMemberId) {
        log.debug("Fetching Activities for familyId: {}, familyMemberId: {}", familyId, familyMemberId);

        if (!familyRepository.existsById(familyId)) {
            log.warn("Family not found: {}", familyId);
            return Result.notFound("Family not found: " + familyId);
        }

        Specification<Activity> spec = (_, _, _) -> null;

        spec = spec.and((root, _, cb) ->
                cb.equal(root.get("family").get("id"), familyId));

        if (familyMemberId != null) {
            spec = spec.and((root, _, cb) ->
                    cb.equal(root.get("assignee").get("id"), familyMemberId));
        }

        return Result.success(
                activitiesRepository.findAll(spec)
                    .stream()
                    .map(activityMapper::toActivityResponse)
                    .toList());
    }

    public Result<ActivityResponse> updateActivity(Long activityId ,UpdateActivityRequest updateActivityRequest) {
        log.debug("Update activity");

        List<FamilyMember> participants = new ArrayList<>();
        if(updateActivityRequest.participants() != null){
            for(Long participantId : updateActivityRequest.participants()){
                Optional<FamilyMember> familyMemberOpt = familyMemberRepository.findById(participantId);

                if(familyMemberOpt.isEmpty()){
                    log.warn("Family member with id: {} not found and will not be added. ", participantId);
                    continue;
                }

                familyMemberOpt.ifPresent(participants::add);
            }
        }

        Optional<Activity> activityOpt = activitiesRepository.findById(activityId);
        if(activityOpt.isEmpty()){
            log.warn("Activity with id: {} not found.", activityId);
            return Result.notFound("Activity with id: " + activityId + " not found.");
        }

        Activity activity = activityOpt.get();
        activity.Update(updateActivityRequest.name(),
                updateActivityRequest.description(),
                updateActivityRequest.location(),
                updateActivityRequest.day(),
                updateActivityRequest.startTime(),
                updateActivityRequest.endtime(),
                participants);

        activity = activitiesRepository.save(activity);

        return Result.success(activityMapper.toActivityResponse(activity));
    }

    @Transactional
    public Result<Void> deleteActivity(Long activityId) {
        log.debug("Delete activity with id: {}", activityId);

        if (!activitiesRepository.existsById(activityId)) {
            log.error("Activity with id: {} does not exist", activityId);
            return Result.notFound("Activity with id: " + activityId + " does not exist");
        }

        activitiesRepository.deleteById(activityId);
        log.debug("Delete activity with id: {}", activityId);
        return Result.success(null);
    }
}
