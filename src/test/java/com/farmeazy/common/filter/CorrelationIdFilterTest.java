package com.farmeazy.common.filter;

import com.farmeazy.common.constant.RoleConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class CorrelationIdFilterTest {

    private final CorrelationIdFilter filter = new CorrelationIdFilter();

    @Test
    @DisplayName("Verify filter generates X-Correlation-ID when header is missing")
    void testGeneratesCorrelationIdWhenMissing() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, (req, res) -> {
            // Downstream filter action
        });

        String headerValue = response.getHeader(RoleConstants.HEADER_CORRELATION_ID);
        assertNotNull(headerValue);
        assertFalse(headerValue.isBlank());
        assertEquals(16, headerValue.length());
    }

    @Test
    @DisplayName("Verify filter preserves existing X-Correlation-ID from upstream gateway")
    void testPreservesIncomingCorrelationId() throws ServletException, IOException {
        String existingId = "gw-corr-98765432";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(RoleConstants.HEADER_CORRELATION_ID, existingId);
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, (req, res) -> {});

        String headerValue = response.getHeader(RoleConstants.HEADER_CORRELATION_ID);
        assertEquals(existingId, headerValue);
    }
}
