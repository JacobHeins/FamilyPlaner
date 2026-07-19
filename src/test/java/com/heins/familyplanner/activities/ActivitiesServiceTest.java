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
import com.heins.familyplanner.family.entities.FamilyRole;
import com.heins.familyplanner.family.mapper.FamilyMapper;
import com.heins.familyplanner.family.mapper.FamilyRoleMapper;
import com.heins.familyplanner.family.repositories.FamilyMemberRepository;
import com.heins.familyplanner.family.repositories.FamilyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ActivitiesServiceTest {

    @Mock
    ActivitiesRepository activitiesRepository;
    @Mock
    FamilyRepository familyRepository;
    @Mock
    FamilyMemberRepository familyMemberRepository;

    FamilyRoleMapper familyRoleMapper = new FamilyRoleMapper();
    FamilyMapper familyMapper = new FamilyMapper(familyRoleMapper);
    ActivityMapper activityMapper = new ActivityMapper(familyMapper);

    ActivitiesService activitiesService;

    Family family;
    FamilyMember member;
    final LocalDate futureDate = LocalDate.now().plusDays(7);

    @BeforeEach
    void setUp() {
        activitiesService = new ActivitiesService(activitiesRepository, familyRepository, familyMemberRepository,
                activityMapper);

        family = new Family("Heins");
        ReflectionTestUtils.setField(family, "id", 1L);

        member = new FamilyMember("Jacob", FamilyRole.DAD);
        ReflectionTestUtils.setField(member, "id", 10L);
        member.setFamily(family);
    }

    // -------------------------------------------------------
    // createActivity
    // -------------------------------------------------------

    @Test
    void createActivity_returnsSuccess_whenFamilyExists() {
        when(familyRepository.findByIdAndAccountId(1L, 1L)).thenReturn(Optional.of(family));
        when(activitiesRepository.save(any())).thenAnswer(i -> {
            Activity a = i.getArgument(0);
            ReflectionTestUtils.setField(a, "id", 1L);
            return a;
        });

        CreateActivityRequest req = new CreateActivityRequest("Football", null, null, futureDate, null, null, 1L, null);
        Result<ActivityResponse> result = activitiesService.createActivity(req, 1L);

        assertInstanceOf(Result.Success.class, result);
        ActivityResponse resp = ((Result.Success<ActivityResponse>) result).value();
        assertEquals("Football", resp.name());
        assertEquals(1L, resp.familyId());
        assertTrue(resp.participants().isEmpty());
        verify(activitiesRepository).save(any(Activity.class));
    }

    @Test
    void createActivity_returnsSuccess_withParticipants() {
        when(familyRepository.findByIdAndAccountId(1L, 1L)).thenReturn(Optional.of(family));
        when(familyMemberRepository.findById(10L)).thenReturn(Optional.of(member));
        when(activitiesRepository.save(any())).thenAnswer(i -> {
            Activity a = i.getArgument(0);
            ReflectionTestUtils.setField(a, "id", 1L);
            return a;
        });

        CreateActivityRequest req = new CreateActivityRequest("Football", null, null, futureDate, null, null, 1L,
                List.of(10L));
        Result<ActivityResponse> result = activitiesService.createActivity(req, 1L);

        assertInstanceOf(Result.Success.class, result);
        ActivityResponse resp = ((Result.Success<ActivityResponse>) result).value();
        assertEquals(1, resp.participants().size());
        assertEquals("Jacob", resp.participants().getFirst().name());
    }

    @Test
    void createActivity_skipsUnknownParticipants() {
        when(familyRepository.findByIdAndAccountId(1L, 1L)).thenReturn(Optional.of(family));
        when(familyMemberRepository.findById(99L)).thenReturn(Optional.empty());
        when(activitiesRepository.save(any())).thenAnswer(i -> {
            Activity a = i.getArgument(0);
            ReflectionTestUtils.setField(a, "id", 1L);
            return a;
        });

        CreateActivityRequest req = new CreateActivityRequest("Football", null, null, futureDate, null, null, 1L,
                List.of(99L));
        Result<ActivityResponse> result = activitiesService.createActivity(req, 1L);

        assertInstanceOf(Result.Success.class, result);
        assertTrue(((Result.Success<ActivityResponse>) result).value().participants().isEmpty());
        verify(activitiesRepository).save(any(Activity.class));
    }

    @Test
    void createActivity_returnsNotFound_whenFamilyMissing() {
        when(familyRepository.findByIdAndAccountId(99L, 1L)).thenReturn(Optional.empty());

        CreateActivityRequest req = new CreateActivityRequest("Football", null, null, futureDate, null, null, 99L,
                null);
        Result<ActivityResponse> result = activitiesService.createActivity(req, 1L);

        assertInstanceOf(Result.Failure.class, result);
        assertEquals(Result.ErrorType.NOT_FOUND, ((Result.Failure<ActivityResponse>) result).errorType());
        verify(activitiesRepository, never()).save(any());
    }

    // -------------------------------------------------------
    // getActivities
    // -------------------------------------------------------

    @Test
    @SuppressWarnings("unchecked")
    void getActivities_returnsActivities_whenFamilyExists() {
        when(familyRepository.findByIdAndAccountId(1L, 1L)).thenReturn(Optional.of(family));
        Activity activity = new Activity("Football", null, null, futureDate, null, null, family, List.of());
        ReflectionTestUtils.setField(activity, "id", 1L);
        when(activitiesRepository.findAll(any(Specification.class))).thenReturn(List.of(activity));

        Result<List<ActivityResponse>> result = activitiesService.getActivities(new GetActivitiesRequest(1L, null), 1L);

        assertInstanceOf(Result.Success.class, result);
        List<ActivityResponse> activities = ((Result.Success<List<ActivityResponse>>) result).value();
        assertEquals(1, activities.size());
        assertEquals("Football", activities.getFirst().name());
    }

    @Test
    @SuppressWarnings("unchecked")
    void getActivities_returnsEmpty_whenNoActivitiesExist() {
        when(familyRepository.findByIdAndAccountId(1L, 1L)).thenReturn(Optional.of(family));
        when(activitiesRepository.findAll(any(Specification.class))).thenReturn(List.of());

        Result<List<ActivityResponse>> result = activitiesService.getActivities(new GetActivitiesRequest(1L, null), 1L);

        assertInstanceOf(Result.Success.class, result);
        assertTrue(((Result.Success<List<ActivityResponse>>) result).value().isEmpty());
    }

    @Test
    void getActivities_returnsNotFound_whenFamilyMissing() {
        when(familyRepository.findByIdAndAccountId(99L, 1L)).thenReturn(Optional.empty());

        Result<List<ActivityResponse>> result = activitiesService.getActivities(new GetActivitiesRequest(99L, null),
                1L);

        assertInstanceOf(Result.Failure.class, result);
        assertEquals(Result.ErrorType.NOT_FOUND, ((Result.Failure<List<ActivityResponse>>) result).errorType());
        assertEquals("Family not found: 99", ((Result.Failure<List<ActivityResponse>>) result).error());
    }

    // -------------------------------------------------------
    // updateActivity
    // -------------------------------------------------------

    @Test
    void updateActivity_returnsSuccess_whenActivityExists() {
        Activity activity = new Activity("Football", null, null, futureDate, null, null, family, List.of());
        ReflectionTestUtils.setField(activity, "id", 1L);
        when(activitiesRepository.findByIdAndFamily_Id(1L, 1L)).thenReturn(Optional.of(activity));
        when(activitiesRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        UpdateActivityRequest req = new UpdateActivityRequest("Swimming", null, null, futureDate, null, null, null);
        Result<ActivityResponse> result = activitiesService.updateActivity(1L, req, 1L);

        assertInstanceOf(Result.Success.class, result);
        assertEquals("Swimming", ((Result.Success<ActivityResponse>) result).value().name());
        verify(activitiesRepository).save(activity);
    }

    @Test
    void updateActivity_returnsSuccess_withParticipants() {
        Activity activity = new Activity("Football", null, null, futureDate, null, null, family, List.of());
        ReflectionTestUtils.setField(activity, "id", 1L);
        when(activitiesRepository.findByIdAndFamily_Id(1L, 1L)).thenReturn(Optional.of(activity));
        when(familyMemberRepository.findById(10L)).thenReturn(Optional.of(member));
        when(activitiesRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        UpdateActivityRequest req = new UpdateActivityRequest("Swimming", null, null, futureDate, null, null,
                List.of(10L));
        Result<ActivityResponse> result = activitiesService.updateActivity(1L, req, 1L);

        assertInstanceOf(Result.Success.class, result);
        assertEquals(1, ((Result.Success<ActivityResponse>) result).value().participants().size());
    }

    @Test
    void updateActivity_returnsNotFound_whenActivityMissing() {
        when(activitiesRepository.findByIdAndFamily_Id(99L, 1L)).thenReturn(Optional.empty());

        UpdateActivityRequest req = new UpdateActivityRequest("Swimming", null, null, futureDate, null, null, null);
        Result<ActivityResponse> result = activitiesService.updateActivity(99L, req, 1L);

        assertInstanceOf(Result.Failure.class, result);
        assertEquals(Result.ErrorType.NOT_FOUND, ((Result.Failure<ActivityResponse>) result).errorType());
        assertEquals("Activity with id: 99 not found.", ((Result.Failure<ActivityResponse>) result).error());
        verify(activitiesRepository, never()).save(any());
    }

    @Test
    void updateActivity_skipsUnknownParticipants() {
        Activity activity = new Activity("Football", null, null, futureDate, null, null, family, List.of());
        ReflectionTestUtils.setField(activity, "id", 1L);
        when(activitiesRepository.findByIdAndFamily_Id(1L, 1L)).thenReturn(Optional.of(activity));
        when(familyMemberRepository.findById(99L)).thenReturn(Optional.empty());
        when(activitiesRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        UpdateActivityRequest req = new UpdateActivityRequest("Swimming", null, null, futureDate, null, null,
                List.of(99L));
        Result<ActivityResponse> result = activitiesService.updateActivity(1L, req, 1L);

        assertInstanceOf(Result.Success.class, result);
        assertTrue(((Result.Success<ActivityResponse>) result).value().participants().isEmpty());
    }

    // -------------------------------------------------------
    // deleteActivity
    // -------------------------------------------------------

    @Test
    void deleteActivity_returnsSuccess_whenActivityExists() {
        when(activitiesRepository.existsByIdAndFamily_Id(1L, 1L)).thenReturn(true);

        Result<Void> result = activitiesService.deleteActivity(1L, 1L);

        assertInstanceOf(Result.Success.class, result);
        verify(activitiesRepository).deleteById(1L);
    }

    @Test
    void deleteActivity_returnsNotFound_whenActivityMissing() {
        when(activitiesRepository.existsByIdAndFamily_Id(99L, 1L)).thenReturn(false);

        Result<Void> result = activitiesService.deleteActivity(99L, 1L);

        assertInstanceOf(Result.Failure.class, result);
        assertEquals(Result.ErrorType.NOT_FOUND, ((Result.Failure<Void>) result).errorType());
        assertEquals("Activity with id: 99 does not exist", ((Result.Failure<Void>) result).error());
        verify(activitiesRepository, never()).deleteById(any());
    }
}
