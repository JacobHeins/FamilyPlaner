package com.heins.familyplanner.auth;

import com.heins.familyplanner.accounts.Repositories.FamilyAccountsRepository;
import com.heins.familyplanner.auth.dtos.LoginResponse;
import com.heins.familyplanner.config.TestWebMvcConfig;
import com.heins.familyplanner.exceptions.Result;
import com.heins.familyplanner.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@ImportAutoConfiguration(exclude = com.heins.familyplanner.configuration.JpaConfig.class)
@Import(TestWebMvcConfig.class)
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    AuthService authService;

    // Required by JwtAuthFilter which is part of the security filter chain
    @MockitoBean
    JwtService jwtService;

    @MockitoBean
    FamilyAccountsRepository accountsRepository;

    // -------------------------------------------------------
    // POST /api/auth/login
    // -------------------------------------------------------

    @Test
    void login_returnsOk_whenCredentialsAreValid() throws Exception {
        when(authService.login(any())).thenReturn(Result.success(new LoginResponse("jwt-token")));

        String json = """
                { "familySlug": "heins-family", "password": "secret" }
                """;

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void login_returnsUnauthorized_whenCredentialsAreInvalid() throws Exception {
        when(authService.login(any())).thenReturn(Result.unauthorized("Invalid credentials"));

        String json = """
                { "familySlug": "heins-family", "password": "wrong" }
                """;

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.detail").value("Invalid credentials"));
    }

    @Test
    void login_returnsBadRequest_whenSlugMissing() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"password\": \"secret\" }"))
                .andExpect(status().isBadRequest());

        verify(authService, never()).login(any());
    }

    @Test
    void login_returnsBadRequest_whenPasswordMissing() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"familySlug\": \"heins-family\" }"))
                .andExpect(status().isBadRequest());

        verify(authService, never()).login(any());
    }

    // -------------------------------------------------------
    // POST /api/auth/register
    // -------------------------------------------------------

    @Test
    void register_returnsCreated_whenRequestIsValid() throws Exception {
        when(authService.register(any())).thenReturn(Result.success(new LoginResponse("jwt-token")));

        String json = """
                { "familySlug": "heins-family", "familyName": "Heins", "password": "secret123" }
                """;

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void register_returnsConflict_whenAccountAlreadyExists() throws Exception {
        when(authService.register(any())).thenReturn(Result.conflict("Account already exists"));

        String json = """
                { "familySlug": "heins-family", "familyName": "Heins", "password": "secret123" }
                """;

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Account already exists"));
    }

    @Test
    void register_returnsBadRequest_whenSlugMissing() throws Exception {
        String json = """
                { "familyName": "Heins", "password": "secret123" }
                """;

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());

        verify(authService, never()).register(any());
    }

    @Test
    void register_returnsBadRequest_whenFamilyNameMissing() throws Exception {
        String json = """
                { "familySlug": "heins-family", "password": "secret123" }
                """;

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());

        verify(authService, never()).register(any());
    }

    @Test
    void register_returnsBadRequest_whenPasswordTooShort() throws Exception {
        String json = """
                { "familySlug": "heins-family", "familyName": "Heins", "password": "short" }
                """;

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());

        verify(authService, never()).register(any());
    }
}
