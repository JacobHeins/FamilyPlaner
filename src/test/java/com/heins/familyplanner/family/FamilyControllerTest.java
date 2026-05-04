package com.heins.familyplanner.family;

import com.heins.familyplanner.accounts.Repositories.FamilyAccountsRepository;
import com.heins.familyplanner.accounts.entities.FamilyAccount;
import com.heins.familyplanner.exceptions.Result;
import com.heins.familyplanner.family.dtos.FamilyResponse;
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

import java.util.Optional;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FamilyController.class)
@ImportAutoConfiguration(exclude = com.heins.familyplanner.configuration.JpaConfig.class)
@Import(TestWebMvcConfig.class)
public class FamilyControllerTest {

        @Autowired
        MockMvc mockMvc;

        @MockitoBean
        FamilyService familyService;

        @MockitoBean
        JwtService jwtService;

        @MockitoBean
        FamilyAccountsRepository accountsRepository;

        private static final String TOKEN = "Bearer test-token";

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

        // ---------------------------------------------------------
        // GET /api/families/{id}
        // ---------------------------------------------------------
        @Test
        void getFamily_returnsOk_whenFamilyBelongsToAccount() throws Exception {
                FamilyResponse familyResponse = new FamilyResponse(1L, "Heins", List.of());
                when(familyService.getFamily(1L, 1L)).thenReturn(Result.success(familyResponse));

                mockMvc.perform(get("/api/families/1")
                                .header("Authorization", TOKEN))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.name").value("Heins"));

                verify(familyService).getFamily(1L, 1L);
        }

        @Test
        void getFamily_returnsForbidden_whenFamilyDoesNotBelongToAccount() throws Exception {
                mockMvc.perform(get("/api/families/99")
                                .header("Authorization", TOKEN))
                                .andExpect(status().isForbidden());

                verify(familyService, never()).getFamily(any(), any());
        }

        // ---------------------------------------------------------
        // PUT /api/families/{id} — valid request
        // ---------------------------------------------------------
        @Test
        void updateFamily_validRequest_returnsUpdatedFamily() throws Exception {
                FamilyResponse familyResponse = new FamilyResponse(1L, "Heins Family", List.of());
                when(familyService.updateFamily(any(), any(), any())).thenReturn(Result.success(familyResponse));

                String json = """
                                { "name": "Heins Family" }
                                """;

                mockMvc.perform(put("/api/families/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                                .header("Authorization", TOKEN))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.name").value("Heins Family"));

                verify(familyService).updateFamily(any(), any(), any());
        }

        @Test
        void updateFamily_familyNotFound_returnsNotFound() throws Exception {
                when(familyService.updateFamily(any(), any(), any()))
                                .thenReturn(Result.notFound("Family not found with id: 1"));

                String json = """
                                { "name": "Missing Family" }
                                """;

                mockMvc.perform(put("/api/families/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                                .header("Authorization", TOKEN))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.detail").value("Family not found with id: 1"));
        }

        @Test
        void updateFamily_shortName_returnsBadRequest() throws Exception {
                String json = """
                                { "name": "H" }
                                """;

                mockMvc.perform(put("/api/families/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                                .header("Authorization", TOKEN))
                                .andExpect(status().isBadRequest());

                verify(familyService, never()).updateFamily(any(), any(), any());
        }

        // ---------------------------------------------------------
        // POST /api/families/{id}/members — valid request
        // ---------------------------------------------------------
        @Test
        void addFamilyMember_validRequest_returnsCreated() throws Exception {
                FamilyResponse familyResponse = new FamilyResponse(1L, "Heins", List.of());
                when(familyService.addFamilyMember(any(), any(), any())).thenReturn(Result.success(familyResponse));

                String json = """
                                { "name": "Jacob", "role": "DAD" }
                                """;

                mockMvc.perform(post("/api/families/1/members")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                                .header("Authorization", TOKEN))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.name").value("Heins"));
        }

        @Test
        void addFamilyMember_familyNotFound_returnsNotFound() throws Exception {
                when(familyService.addFamilyMember(any(), any(), any()))
                                .thenReturn(Result.notFound("Family not found with id: 99"));

                String json = """
                                { "name": "Jacob", "role": "DAD" }
                                """;

                mockMvc.perform(post("/api/families/1/members")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                                .header("Authorization", TOKEN))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.detail").value("Family not found with id: 99"));
        }
}
