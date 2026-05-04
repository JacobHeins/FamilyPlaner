package com.heins.familyplanner.activities;

import com.heins.familyplanner.accounts.Repositories.FamilyAccountsRepository;
import com.heins.familyplanner.accounts.entities.FamilyAccount;
import com.heins.familyplanner.activities.dtos.ActivityResponse;
import com.heins.familyplanner.exceptions.Result;
import com.heins.familyplanner.family.entities.Family;
import com.heins.familyplanner.security.JwtService;
import com.heins.familyplanner.config.TestWebMvcConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ActivitiesController.class)
@ImportAutoConfiguration(exclude = com.heins.familyplanner.configuration.JpaConfig.class)
@Import(TestWebMvcConfig.class)
public class ActivitiesControllerTest {

        @Autowired
        MockMvc mockMvc;

        @MockitoBean
        ActivitiesService activitiesService;

        @MockitoBean
        JwtService jwtService;

        @MockitoBean
        FamilyAccountsRepository accountsRepository;

        private static final String TOKEN = "Bearer test-token";

        private final LocalDate futureDate = LocalDate.now().plusDays(7);

        private FamilyAccount mockAccount(Long familyId) {
                Family family = new Family("Test");
                ReflectionTestUtils.setField(family, "id", familyId);
                FamilyAccount account = new FamilyAccount("test-slug", "hash", family);
                ReflectionTestUtils.setField(account, "id", 1L);
                return account;
        }

        @BeforeEach
        void setUpJwt() {
                when(jwtService.isTokenValid("test-token")).thenReturn(true);
                when(jwtService.extractSlug("test-token")).thenReturn("test-slug");
                when(accountsRepository.findByPublicSlug("test-slug")).thenReturn(Optional.of(mockAccount(1L)));
        }

        private ActivityResponse sampleResponse() {
                return new ActivityResponse(1L, "Football", null, null, futureDate, null, null, 1L, List.of());
        }

        // -------------------------------------------------------
        // GET /api/activities
        // -------------------------------------------------------

        @Test
        void getActivities_returnsOk_whenFamilyExists() throws Exception {
                when(activitiesService.getActivities(any(), any()))
                                .thenReturn(Result.success(List.of(sampleResponse())));

                mockMvc.perform(get("/api/activities")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{ \"familyId\": 1 }")
                                .header("Authorization", TOKEN))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.length()").value(1))
                                .andExpect(jsonPath("$[0].id").value(1))
                                .andExpect(jsonPath("$[0].name").value("Football"));

                verify(activitiesService).getActivities(any(), any());
        }

        @Test
        void getActivities_returnsNotFound_whenFamilyMissing() throws Exception {
                when(activitiesService.getActivities(any(), any())).thenReturn(Result.notFound("Family not found: 1"));

                mockMvc.perform(get("/api/activities")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{ \"familyId\": 1 }")
                                .header("Authorization", TOKEN))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.detail").value("Family not found: 1"));
        }

        @Test
        void getActivities_returnsForbidden_whenFamilyIdDoesNotMatchAccount() throws Exception {
                mockMvc.perform(get("/api/activities")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{ \"familyId\": 99 }")
                                .header("Authorization", TOKEN))
                                .andExpect(status().isForbidden());

                verify(activitiesService, never()).getActivities(any(), any());
        }

        @Test
        void getActivities_filtersByMemberId_whenProvided() throws Exception {
                when(activitiesService.getActivities(any(), any()))
                                .thenReturn(Result.success(List.of(sampleResponse())));

                mockMvc.perform(get("/api/activities")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{ \"familyId\": 1, \"familyMemberId\": 10 }")
                                .header("Authorization", TOKEN))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.length()").value(1));

                verify(activitiesService).getActivities(any(), any());
        }

        // -------------------------------------------------------
        // POST /api/activities
        // -------------------------------------------------------

        @Test
        void createActivity_returnsCreated_whenValid() throws Exception {
                when(activitiesService.createActivity(any(), any())).thenReturn(Result.success(sampleResponse()));

                String json = """
                                { "name": "Football", "day": "%s", "familyId": 1 }
                                """.formatted(futureDate);

                mockMvc.perform(post("/api/activities")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                                .header("Authorization", TOKEN))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.name").value("Football"));
        }

        @Test
        void createActivity_returnsBadRequest_whenNameMissing() throws Exception {
                mockMvc.perform(post("/api/activities")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{ \"day\": \"" + futureDate + "\", \"familyId\": 1 }")
                                .header("Authorization", TOKEN))
                                .andExpect(status().isBadRequest());

                verify(activitiesService, never()).createActivity(any(), any());
        }

        @Test
        void createActivity_returnsBadRequest_whenFamilyIdMissing() throws Exception {
                mockMvc.perform(post("/api/activities")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{ \"name\": \"Football\", \"day\": \"" + futureDate + "\" }")
                                .header("Authorization", TOKEN))
                                .andExpect(status().isBadRequest());

                verify(activitiesService, never()).createActivity(any(), any());
        }

        @Test
        void createActivity_returnsNotFound_whenFamilyMissing() throws Exception {
                when(activitiesService.createActivity(any(), any()))
                                .thenReturn(Result.notFound("Family with id: 99 not found."));

                String json = """
                                { "name": "Football", "day": "%s", "familyId": 1 }
                                """.formatted(futureDate);

                mockMvc.perform(post("/api/activities")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                                .header("Authorization", TOKEN))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.detail").value("Family with id: 99 not found."));
        }

        // -------------------------------------------------------
        // PUT /api/activities/{id}
        // -------------------------------------------------------

        @Test
        void updateActivity_returnsCreated_whenValid() throws Exception {
                ActivityResponse updated = new ActivityResponse(1L, "Swimming", null, null, futureDate, null, null, 1L,
                                List.of());
                when(activitiesService.updateActivity(eq(1L), any(), any())).thenReturn(Result.success(updated));

                String json = """
                                { "name": "Swimming", "day": "%s" }
                                """.formatted(futureDate);

                mockMvc.perform(put("/api/activities/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                                .header("Authorization", TOKEN))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.name").value("Swimming"));
        }

        @Test
        void updateActivity_returnsBadRequest_whenNameMissing() throws Exception {
                mockMvc.perform(put("/api/activities/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{ \"day\": \"" + futureDate + "\" }")
                                .header("Authorization", TOKEN))
                                .andExpect(status().isBadRequest());

                verify(activitiesService, never()).updateActivity(any(), any(), any());
        }

        @Test
        void updateActivity_returnsNotFound_whenActivityMissing() throws Exception {
                when(activitiesService.updateActivity(eq(99L), any(), any()))
                                .thenReturn(Result.notFound("Activity with id: 99 not found."));

                String json = """
                                { "name": "Swimming", "day": "%s" }
                                """.formatted(futureDate);

                mockMvc.perform(put("/api/activities/99")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                                .header("Authorization", TOKEN))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.detail").value("Activity with id: 99 not found."));
        }

        // -------------------------------------------------------
        // DELETE /api/activities/{id}
        // -------------------------------------------------------

        @Test
        void deleteActivity_returnsNoContent_whenActivityExists() throws Exception {
                when(activitiesService.deleteActivity(any(), any())).thenReturn(Result.success(null));

                mockMvc.perform(delete("/api/activities/1")
                                .header("Authorization", TOKEN))
                                .andExpect(status().isNoContent());

                verify(activitiesService).deleteActivity(1L, 1L);
        }

        @Test
        void deleteActivity_returnsNotFound_whenActivityMissing() throws Exception {
                when(activitiesService.deleteActivity(any(), any()))
                                .thenReturn(Result.notFound("Activity with id: 99 does not exist"));

                mockMvc.perform(delete("/api/activities/99")
                                .header("Authorization", TOKEN))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.detail").value("Activity with id: 99 does not exist"));
        }
}
