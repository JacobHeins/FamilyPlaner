package com.heins.familyplanner.family;


import com.heins.familyplanner.exceptions.Result;
import com.heins.familyplanner.family.dtos.FamilyResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FamilyController.class)
@ImportAutoConfiguration(exclude = com.heins.familyplanner.configuration.JpaConfig.class)
public class FamilyControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    FamilyService familyService;

    // ---------------------------------------------------------
    // GET /api/families
    // ---------------------------------------------------------
    @Test
    void getAllFamilies_returnsListOfFamilies() throws Exception {
        List<FamilyResponse> families = List.of(
                new FamilyResponse(1L, "Heins", List.of()),
                new FamilyResponse(2L, "Lehnert", List.of())
        );
        when(familyService.getAllFamilies()).thenReturn(families);

        mockMvc.perform(get("/api/families"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Heins"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Lehnert"));

        verify(familyService).getAllFamilies();
    }

    @Test
    void getAllFamilies_returnsEmptyList() throws Exception {
        when(familyService.getAllFamilies()).thenReturn(List.of());

        mockMvc.perform(get("/api/families"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ---------------------------------------------------------
    // POST /api/families — valid request
    // ---------------------------------------------------------
    @Test
    void addFamily_validRequest_returnsCreatedFamily() throws Exception {
        FamilyResponse familyResponse = new FamilyResponse(1L, "Heins", List.of());
        when(familyService.addFamily("Heins")).thenReturn(Result.success(familyResponse));

        String json = """
                { "name": "Heins" }
                """;

        mockMvc.perform(post("/api/families")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Heins"));

        verify(familyService).addFamily("Heins");
    }

    // ---------------------------------------------------------
    // POST /api/families — validation errors
    // ---------------------------------------------------------
    @Test
    void addFamily_emptyName_returnsBadRequest() throws Exception {
        String json = """
                { "name": "" }
        """;

        mockMvc.perform(post("/api/families")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest());

        verify(familyService, never()).addFamily(any());
    }

    @Test
    void addFamily_emptyBody_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/families")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest());

        verify(familyService, never()).addFamily(any());
    }

    // ---------------------------------------------------------
    // PUT /api/families — valid request
    // ---------------------------------------------------------
    @Test
    void updateFamily_validRequest_returnsUpdatedFamily() throws Exception {
        FamilyResponse familyResponse = new FamilyResponse(1L, "Heins Family", List.of());
        when(familyService.updateFamily(any(), any())).thenReturn(Result.success(familyResponse));

        String json = """
                { "name": "Heins Family" }
                """;

        mockMvc.perform(put("/api/families/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Heins Family"));

        verify(familyService).updateFamily(any(), any());
    }

    @Test
    void updateFamily_familyNotFound_returnsNotFound() throws Exception {
        when(familyService.updateFamily(any(), any())).thenReturn(Result.notFound("Family not found with id: 99"));

        String json = """
                { "name": "Missing Family" }
                """;

        mockMvc.perform(put("/api/families/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Family not found with id: 99"));
    }

    @Test
    void updateFamily_shortName_returnsBadRequest() throws Exception {
        String json = """
                { "name": "H" }
                """;

        mockMvc.perform(put("/api/families/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());

        verify(familyService, never()).updateFamily(any(), any());
    }

    // ---------------------------------------------------------
    // POST /api/families/members — valid request
    // ---------------------------------------------------------
    @Test
    void addFamilyMember_validRequest_returnsCreated() throws Exception {
        FamilyResponse familyResponse = new FamilyResponse(1L, "Heins", List.of());
        when(familyService.addFamilyMember(any(), any())).thenReturn(Result.success(familyResponse));

        String json = """
                { "name": "Jacob", "role": "DAD" }
                """;

        mockMvc.perform(post("/api/families/1/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Heins"));
    }

    @Test
    void addFamilyMember_familyNotFound_returnsNotFound() throws Exception {
        when(familyService.addFamilyMember(any(), any())).thenReturn(Result.notFound("Family not found with id: 99"));

        String json = """
                { "name": "Jacob", "role": "DAD" }
                """;

        mockMvc.perform(post("/api/families/99/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Family not found with id: 99"));
    }
}
