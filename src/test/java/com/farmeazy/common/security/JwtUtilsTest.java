package com.farmeazy.common.security;

import com.farmeazy.common.dto.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "secret", "this-is-a-very-long-and-secure-test-secret-key-that-exceeds-64-characters-for-hmac-sha512-testing");
        ReflectionTestUtils.setField(jwtUtils, "expiration", 3600000L); // 1 hour
    }

    @Test
    @DisplayName("Verify JWT generation, parsing, claim extraction, and validation")
    void testJwtLifecycle() {
        UserPrincipal user = UserPrincipal.builder()
                .id(42L)
                .username("farmer_ram")
                .email("ramesh@farm-eazy.com")
                .roles(List.of("ROLE_FARMER", "ROLE_USER"))
                .enabled(true)
                .build();

        String token = jwtUtils.generateToken(user, "session-xyz-123");

        assertNotNull(token);
        assertFalse(token.isBlank());
        assertTrue(jwtUtils.validateToken(token));
        assertTrue(jwtUtils.validateToken(token, user));

        assertEquals("farmer_ram", jwtUtils.extractUsername(token));
        assertEquals("session-xyz-123", jwtUtils.extractSessionId(token));
        UserPrincipal parsed = jwtUtils.extractPrincipal(token);
        assertEquals(42L, parsed.getId());
        assertEquals("ramesh@farm-eazy.com", parsed.getEmail());
        assertEquals("farmer_ram", parsed.getUsername());
        assertEquals("session-xyz-123", parsed.getSessionId());
        List<String> roles = jwtUtils.extractRoles(token);
        assertEquals(2, roles.size());
        assertTrue(roles.contains("ROLE_FARMER"));
        assertTrue(roles.contains("ROLE_USER"));
    }

    @Test
    @DisplayName("Verify invalid token fails validation gracefully without throwing")
    void testInvalidTokenValidation() {
        assertFalse(jwtUtils.validateToken("invalid.token.structure"));
        assertFalse(jwtUtils.validateToken(""));
        assertFalse(jwtUtils.validateToken(null));
    }

    @Test
    void weakKeysAndLongAccessLifetimeFailConfigurationValidation() {
        ReflectionTestUtils.setField(jwtUtils, "secret", "short");
        assertThrows(IllegalStateException.class, jwtUtils::validateConfiguration);
        assertThrows(IllegalStateException.class, () -> jwtUtils.generateToken(UserPrincipal.builder().id(1L)
                .email("user@example.com").roles(List.of("USER")).build()));
    }

    @Test
    void exactAllowlistRejectsDomainAndSuffixMatches() {
        assertTrue(SuperAdminPolicy.isAllowed(" INFO@FARM-EAZY.COM "));
        assertTrue(SuperAdminPolicy.isAllowed("support@farm-eazy.com"));
        assertTrue(SuperAdminPolicy.isAllowed("no-reply@farm-eazy.com"));
        assertFalse(SuperAdminPolicy.isAllowed("admin@farm-eazy.com"));
        assertFalse(SuperAdminPolicy.isAllowed("support@farm-eazy.com.attacker.example"));
        assertFalse(SuperAdminPolicy.isAllowed(null));
        var principal = UserPrincipal.builder().id(1L).email("admin@farm-eazy.com").roles(List.of("ROLE_SUPERADMIN")).build();
        assertTrue(principal.getAuthorities().isEmpty());
        assertThrows(IllegalArgumentException.class, () -> jwtUtils.generateToken(principal));
    }

    @Test
    void privilegedJwtRequiresAndCarriesVerifiedMfaAssurance() {
        var unverified = UserPrincipal.builder().id(1L).username("operator").email("info@farm-eazy.com")
                .roles(List.of("ROLE_SUPERADMIN")).build();
        assertThrows(IllegalArgumentException.class, () -> jwtUtils.generateToken(unverified, "session-id"));

        var verified = UserPrincipal.builder().id(1L).username("operator").email("info@farm-eazy.com")
                .roles(List.of("ROLE_SUPERADMIN")).mfaVerified(true).build();
        String token = jwtUtils.generateToken(verified, "session-id");
        UserPrincipal parsed = jwtUtils.extractPrincipal(token);
        assertTrue(parsed.isMfaVerified());
        assertTrue(parsed.getAuthorities().stream().anyMatch(a -> "ROLE_SUPERADMIN".equals(a.getAuthority())));
    }
}
