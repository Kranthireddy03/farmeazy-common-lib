package com.farmeazy.common.security;

import com.farmeazy.common.dto.UserPrincipal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {
    @AfterEach void cleanup() { SecurityContextHolder.clearContext(); }

    private JwtAuthenticationFilter filterWithActiveSession(JwtUtils jwt) {
        var filter = new JwtAuthenticationFilter(jwt);
        org.springframework.test.util.ReflectionTestUtils.setField(filter, "sessions", mock(SessionAccessVerifier.class));
        return filter;
    }
    @Test void spoofedHeadersNeverAuthenticate() throws Exception {
        JwtUtils jwt = mock(JwtUtils.class);
        var request = new MockHttpServletRequest();
        request.addHeader("X-User-Email", "support@farm-eazy.com");
        request.addHeader("X-User-Id", "1"); request.addHeader("X-User-Roles", "SUPERADMIN");
        new JwtAuthenticationFilter(jwt).doFilter(request, new MockHttpServletResponse(), (req, res) ->
                assertNull(SecurityContextHolder.getContext().getAuthentication()));
        verifyNoInteractions(jwt);
    }

    @Test void bearerIdentityWinsOverSpoofedIdentity() throws Exception {
        JwtUtils jwt = mock(JwtUtils.class);
        var principal = UserPrincipal.builder().id(42L).email("customer@example.com").username("customer").build();
        when(jwt.validateToken("signed")).thenReturn(true);
        when(jwt.extractPrincipal("signed")).thenReturn(principal);
        var request = new MockHttpServletRequest(); request.addHeader("Authorization", "Bearer signed");
        request.addHeader("X-User-Id", "1");
        filterWithActiveSession(jwt).doFilter(request, new MockHttpServletResponse(), (req, res) ->
                assertSame(principal, SecurityContextHolder.getContext().getAuthentication().getPrincipal()));
    }

    @Test void tokensWithoutCanonicalIdentityAreRejected() throws Exception {
        JwtUtils jwt = mock(JwtUtils.class);
        when(jwt.validateToken("legacy")).thenReturn(true);
        when(jwt.extractPrincipal("legacy")).thenThrow(new io.jsonwebtoken.JwtException("missing userId"));
        var request = new MockHttpServletRequest(); request.addHeader("Authorization", "Bearer legacy");
        var response = new MockHttpServletResponse();
        new JwtAuthenticationFilter(jwt).doFilter(request, response, (req, res) ->
                assertNull(SecurityContextHolder.getContext().getAuthentication()));
        assertEquals(401, response.getStatus());
        assertEquals("TOKEN_MALFORMED", response.getHeader("X-Auth-Rejection-Reason"));
    }

    @Test void expiredTokenSetsRejectionHeader() throws Exception {
        JwtUtils jwt = mock(JwtUtils.class);
        when(jwt.validateToken("expired-jwt")).thenReturn(false);
        when(jwt.isTokenExpired("expired-jwt")).thenReturn(true);
        var request = new MockHttpServletRequest(); request.addHeader("Authorization", "Bearer expired-jwt");
        var response = new MockHttpServletResponse();
        new JwtAuthenticationFilter(jwt).doFilter(request, response, (req, res) -> fail("Filter chain must not proceed"));
        assertEquals(401, response.getStatus());
        assertEquals("TOKEN_EXPIRED", response.getHeader("X-Auth-Rejection-Reason"));
        assertTrue(response.getContentAsString().contains("TOKEN_EXPIRED"));
    }

    @Test void invalidSignatureSetsRejectionHeader() throws Exception {
        JwtUtils jwt = mock(JwtUtils.class);
        when(jwt.validateToken("tampered-jwt")).thenReturn(false);
        when(jwt.isTokenExpired("tampered-jwt")).thenReturn(false);
        var request = new MockHttpServletRequest(); request.addHeader("Authorization", "Bearer tampered-jwt");
        var response = new MockHttpServletResponse();
        new JwtAuthenticationFilter(jwt).doFilter(request, response, (req, res) -> fail("Filter chain must not proceed"));
        assertEquals(401, response.getStatus());
        assertEquals("INVALID_SIGNATURE", response.getHeader("X-Auth-Rejection-Reason"));
        assertTrue(response.getContentAsString().contains("INVALID_SIGNATURE"));
    }

    @Test void revokedSessionSetsRejectionHeader() throws Exception {
        JwtUtils jwt = mock(JwtUtils.class);
        var principal = UserPrincipal.builder().id(42L).email("user@example.com").username("user").build();
        when(jwt.validateToken("valid-jwt")).thenReturn(true);
        when(jwt.extractPrincipal("valid-jwt")).thenReturn(principal);

        var sessions = mock(SessionAccessVerifier.class);
        doThrow(new com.farmeazy.common.exception.UnauthorizedException("Session has been revoked or expired"))
                .when(sessions).requireActive(principal);

        var filter = new JwtAuthenticationFilter(jwt);
        org.springframework.test.util.ReflectionTestUtils.setField(filter, "sessions", sessions);

        var request = new MockHttpServletRequest(); request.addHeader("Authorization", "Bearer valid-jwt");
        var response = new MockHttpServletResponse();
        filter.doFilter(request, response, (req, res) -> fail("Filter chain must not proceed"));
        assertEquals(401, response.getStatus());
        assertEquals("SESSION_REVOKED_OR_INACTIVE", response.getHeader("X-Auth-Rejection-Reason"));
        assertTrue(response.getContentAsString().contains("SESSION_REVOKED_OR_INACTIVE"));
    }
}
