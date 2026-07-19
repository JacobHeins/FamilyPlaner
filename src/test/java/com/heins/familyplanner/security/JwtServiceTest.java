package com.heins.familyplanner.security;

import com.heins.familyplanner.accounts.entities.FamilyAccount;
import com.heins.familyplanner.family.entities.Family;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private static final String SECRET = "dGVzdC1zZWNyZXQta2V5LXRoYXQtaXMtYXQtbGVhc3QtMjU2LWJpdHMtbG9uZw==";
    private static final long EXPIRATION_MS = 86400000L; // 24 hours

    JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", SECRET);
        ReflectionTestUtils.setField(jwtService, "expirationMs", EXPIRATION_MS);
    }

    private FamilyAccount mockAccount() {
        Family family = new Family("Heins");
        ReflectionTestUtils.setField(family, "id", 42L);
        FamilyAccount account = new FamilyAccount("heins-family", "hash", family);
        ReflectionTestUtils.setField(account, "id", 1L);
        return account;
    }

    @Test
    void generateToken_containsCorrectSlugAsSubject() {
        String token = jwtService.generateToken(mockAccount());
        assertEquals("heins-family", jwtService.extractSlug(token));
    }

    @Test
    void generateToken_containsCorrectFamilyId() {
        String token = jwtService.generateToken(mockAccount());
        assertEquals(42L, jwtService.extractFamilyId(token));
    }

    @Test
    void isTokenValid_returnsTrueForValidToken() {
        String token = jwtService.generateToken(mockAccount());
        assertTrue(jwtService.isTokenValid(token));
    }

    @Test
    void isTokenValid_returnsFalseForExpiredToken() {
        ReflectionTestUtils.setField(jwtService, "expirationMs", -1000L); // already expired
        String expiredToken = jwtService.generateToken(mockAccount());
        assertFalse(jwtService.isTokenValid(expiredToken));
    }

    @Test
    void isTokenValid_returnsFalseForTamperedToken() {
        String token = jwtService.generateToken(mockAccount());
        String tampered = token.substring(0, token.length() - 4) + "XXXX";
        assertFalse(jwtService.isTokenValid(tampered));
    }

    @Test
    void isTokenValid_returnsFalseForRandomString() {
        assertFalse(jwtService.isTokenValid("not.a.jwt"));
    }

    @Test
    void isTokenValid_returnsFalseForTokenSignedWithDifferentSecret() {
        JwtService otherService = new JwtService();
        // different secret (still valid base64/256-bit key)
        ReflectionTestUtils.setField(otherService, "secret",
                "b3RoZXItc2VjcmV0LWtleS10aGF0LWlzLWF0LWxlYXN0LTI1Ni1iaXRzLWxvbmc=");
        ReflectionTestUtils.setField(otherService, "expirationMs", EXPIRATION_MS);
        String tokenFromOtherService = otherService.generateToken(mockAccount());
        assertFalse(jwtService.isTokenValid(tokenFromOtherService));
    }
}
