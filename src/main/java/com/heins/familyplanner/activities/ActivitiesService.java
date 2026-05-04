package com.heins.familyplanner.activities;

import com.heins.familyplanner.activities.dtos.ActivityResponse;
import com.heins.familyplanner.activities.dtos.CreateActivityRequest;
import com.heins.familyplanner.activities.dtos.GetActivitiesRequest;
import com.heins.familyplanner.activities.dtos.UpdateActivityRequest;
import com.heins.familyplanner.activities.entities.Activity;
import com.heins.familyplanner.activities.mapper.ActivityMapper;
import com.heins.familyplanner.activities.repositories.ActivitiesRepository;
import com.heins.familyplanner.exceptions.Result;
import com.heins.familyplanner.family.entities.Family;
import com.heins.familyplanner.family.entities.FamilyMember;
import com.heins.familyplanner.family.repositories.FamilyMemberRepository;
import com.heins.familyplanner.family.repositories.FamilyRepository;
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
    public Result<ActivityResponse> createActivity(CreateActivityRequest createActivityRequest, Long accountId) {
        log.debug("Add a new activity");

        Optional<Family> familyOpt = familyRepository.findByIdAndAccountId(createActivityRequest.familyId(), accountId);
        if (familyOpt.isEmpty()) {
            log.error("Family with id: {} not found.", createActivityRequest.familyId());
            return Result.notFound("Family with id: " + createActivityRequest.familyId() + " not found.");
        }

        List<FamilyMember> participants = new ArrayList<>();
        if (createActivityRequest.participants() != null) {
            for (Long participantId : createActivityRequest.participants()) {
                Optional<FamilyMember> familyMemberOpt = familyMemberRepository.findById(participantId);

                if (familyMemberOpt.isEmpty()) {
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

    public Result<List<ActivityResponse>> getActivities(GetActivitiesRequest request, Long accountId) {
        log.debug("Fetching Activities for familyId: {}, familyMemberId: {}", request.familyId(),
                request.familyMemberId());

        if (familyRepository.findByIdAndAccountId(request.familyId(), accountId).isEmpty()) {
            log.warn("Family not found: {}", request.familyId());
            return Result.notFound("Family not found: " + request.familyId());
        }

        Specification<Activity> spec = (_, _, _) -> null;

        spec = spec.and((root, _, cb) -> cb.equal(root.get("family").get("id"), request.familyId()));

        if (request.familyMemberId() != null) {
            spec = spec.and((root, _, cb) -> cb.equal(root.get("assignee").get("id"), request.familyMemberId()));
        }

        return Result.success(
                activitiesRepository.findAll(spec)
                        .stream()
                        .map(activityMapper::toActivityResponse)
                        .toList());
    }

    @Transactional
    public Result<ActivityResponse> updateActivity(Long activityId, UpdateActivityRequest updateActivityRequest,
            Long familyId) {
        log.debug("Update activity");

        Optional<Activity> activityOpt = activitiesRepository.findByIdAndFamily_Id(activityId, familyId);
        if (activityOpt.isEmpty()) {
            log.warn("Activity with id: {} not found.", activityId);
            return Result.notFound("Activity with id: " + activityId + " not found.");
        }

        List<FamilyMember> participants = new ArrayList<>();
        if (updateActivityRequest.participants() != null) {
            for (Long participantId : updateActivityRequest.participants()) {
                Optional<FamilyMember> familyMemberOpt = familyMemberRepository.findById(participantId);

                if (familyMemberOpt.isEmpty()) {
                    log.warn("Family member with id: {} not found and will not be added. ", participantId);
                    continue;
                }

                familyMemberOpt.ifPresent(participants::add);
            }
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
    public Result<Void> deleteActivity(Long activityId, Long familyId) {
        log.debug("Delete activity with id: {}", activityId);

        if (!activitiesRepository.existsByIdAndFamily_Id(activityId, familyId)) {
            log.error("Activity with id: {} does not exist", activityId);
            return Result.notFound("Activity with id: " + activityId + " does not exist");
        }

        activitiesRepository.deleteById(activityId);
        log.debug("Delete activity with id: {}", activityId);
        return Result.success(null);
    }
}
