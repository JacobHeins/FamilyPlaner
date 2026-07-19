package com.heins.familyplanner.auth;

import com.heins.familyplanner.accounts.Repositories.FamilyAccountsRepository;
import com.heins.familyplanner.accounts.entities.FamilyAccount;
import com.heins.familyplanner.auth.dtos.LoginRequest;
import com.heins.familyplanner.auth.dtos.LoginResponse;
import com.heins.familyplanner.auth.dtos.RegisterRequest;
import com.heins.familyplanner.exceptions.Result;
import com.heins.familyplanner.family.entities.Family;
import com.heins.familyplanner.family.repositories.FamilyRepository;
import com.heins.familyplanner.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    FamilyAccountsRepository familyAccountsRepository;

    @Mock
    FamilyRepository familyRepository;

    @Mock
    JwtService jwtService;

    @Mock
    PasswordEncoder passwordEncoder;

    AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(familyAccountsRepository, familyRepository, jwtService, passwordEncoder);
    }

    private FamilyAccount mockAccount() {
        Family family = new Family("Heins");
        ReflectionTestUtils.setField(family, "id", 1L);
        FamilyAccount account = new FamilyAccount("heins-family", "hashed-pw", family);
        ReflectionTestUtils.setField(account, "id", 1L);
        return account;
    }

    // ----------------------------- login -----------------------------

    @Test
    void login_returnsToken_whenCredentialsAreValid() {
        FamilyAccount account = mockAccount();
        when(familyAccountsRepository.findByPublicSlug("heins-family")).thenReturn(Optional.of(account));
        when(passwordEncoder.matches("secret", account.getPasswordHash())).thenReturn(true);
        when(jwtService.generateToken(account)).thenReturn("jwt-token");

        Result<LoginResponse> result = authService.login(new LoginRequest("heins-family", "secret"));

        assertInstanceOf(Result.Success.class, result);
        assertEquals("jwt-token", ((Result.Success<LoginResponse>) result).value().token());
        verify(jwtService).generateToken(account);
    }

    @Test
    void login_returnsUnauthorized_whenAccountNotFound() {
        when(familyAccountsRepository.findByPublicSlug("unknown")).thenReturn(Optional.empty());

        Result<LoginResponse> result = authService.login(new LoginRequest("unknown", "secret"));

        assertInstanceOf(Result.Failure.class, result);
        assertEquals(Result.ErrorType.UNAUTHORIZED, ((Result.Failure<LoginResponse>) result).errorType());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void login_returnsUnauthorized_whenPasswordDoesNotMatch() {
        FamilyAccount account = mockAccount();
        when(familyAccountsRepository.findByPublicSlug("heins-family")).thenReturn(Optional.of(account));
        when(passwordEncoder.matches("wrong-pw", account.getPasswordHash())).thenReturn(false);

        Result<LoginResponse> result = authService.login(new LoginRequest("heins-family", "wrong-pw"));

        assertInstanceOf(Result.Failure.class, result);
        assertEquals(Result.ErrorType.UNAUTHORIZED, ((Result.Failure<LoginResponse>) result).errorType());
        verify(jwtService, never()).generateToken(any());
    }

    // ----------------------------- register -----------------------------

    @Test
    void register_createsAccountAndReturnsToken_whenSlugIsNew() {
        Family savedFamily = new Family("Heins");
        ReflectionTestUtils.setField(savedFamily, "id", 1L);
        FamilyAccount savedAccount = new FamilyAccount("heins-family", "hashed-pw", savedFamily);
        ReflectionTestUtils.setField(savedAccount, "id", 1L);

        when(familyAccountsRepository.findByPublicSlug("heins-family")).thenReturn(Optional.empty());
        when(familyRepository.save(any())).thenReturn(savedFamily);
        when(passwordEncoder.encode("password123")).thenReturn("hashed-pw");
        when(familyAccountsRepository.save(any())).thenReturn(savedAccount);
        when(jwtService.generateToken(savedAccount)).thenReturn("jwt-token");

        Result<LoginResponse> result = authService.register(
                new RegisterRequest("heins-family", "Heins", "password123"));

        assertInstanceOf(Result.Success.class, result);
        assertEquals("jwt-token", ((Result.Success<LoginResponse>) result).value().token());
        verify(familyRepository).save(any(Family.class));
        verify(passwordEncoder).encode("password123");
        verify(familyAccountsRepository).save(any(FamilyAccount.class));
    }

    @Test
    void register_returnsConflict_whenSlugAlreadyExists() {
        when(familyAccountsRepository.findByPublicSlug("heins-family")).thenReturn(Optional.of(mockAccount()));

        Result<LoginResponse> result = authService.register(
                new RegisterRequest("heins-family", "Heins", "password123"));

        assertInstanceOf(Result.Failure.class, result);
        assertEquals(Result.ErrorType.CONFLICT, ((Result.Failure<LoginResponse>) result).errorType());
        verify(familyRepository, never()).save(any());
        verify(familyAccountsRepository, never()).save(any());
    }
}
