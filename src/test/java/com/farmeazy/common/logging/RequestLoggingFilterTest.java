package com.farmeazy.common.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

class RequestLoggingFilterTest {

    @Test
    @DisplayName("Verify RequestLoggingFilter logs request and response without error")
    void testRequestLogging() throws ServletException, IOException {
        RequestLoggingFilter filter = new RequestLoggingFilter();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(request.getRequestURI()).thenReturn("/api/products/10");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(response.getStatus()).thenReturn(200);

        assertDoesNotThrow(() -> filter.doFilter(request, response, filterChain));
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Verify RequestLoggingFilter skips health check endpoints")
    void testSkipActuator() throws ServletException, IOException {
        RequestLoggingFilter filter = new RequestLoggingFilter();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(request.getRequestURI()).thenReturn("/actuator/health");
        when(request.getMethod()).thenReturn("GET");

        assertDoesNotThrow(() -> filter.doFilter(request, response, filterChain));
        verify(filterChain, times(1)).doFilter(request, response);
    }
}
