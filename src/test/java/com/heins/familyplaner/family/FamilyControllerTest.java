package com.heins.familyplaner.family;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.heins.familyplaner.family.dtos.FamilyDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FamilyController.class)
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
        List<FamilyDto> families = List.of(
                new FamilyDto(1L, "Heins", List.of()),
                new FamilyDto(2L, "Lehnert",  List.of())
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
        FamilyDto familyDto = new FamilyDto(1L, "Heins", List.of());
        when(familyService.addFamily("Heins")).thenReturn(familyDto);

        String json = """
                { "name": "Heins" }
                """;

        mockMvc.perform(post("/api/families")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
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
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.name").value("size must be between 2 and 50"));

        verify(familyService, never()).addFamily(any());
    }

    @Test
    void addFamily_emptyBody_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/families")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.name").value("must not be blank"));

        verify(familyService, never()).addFamily(any());
    }
}
