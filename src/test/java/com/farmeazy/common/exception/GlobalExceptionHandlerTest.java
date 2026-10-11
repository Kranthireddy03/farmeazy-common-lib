package com.farmeazy.common.exception;

import com.farmeazy.common.dto.ErrorResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("Verify BaseException is translated to formatted ErrorResponse")
    void testHandleBaseException() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/test");
        request.addHeader("X-Correlation-ID", "corr-test-123");

        ResourceNotFoundException ex = new ResourceNotFoundException("Product", "id", 999L);
        ResponseEntity<ErrorResponse> response = handler.handleBaseException(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("RESOURCE_NOT_FOUND", response.getBody().getErrorCode());
        assertEquals("corr-test-123", response.getBody().getRequestId());
        assertTrue(response.getBody().getMessage().contains("Product not found with id: '999'"));
    }

    @Test
    @DisplayName("Verify unhandled Exception is translated to 500 without leaking stacktrace")
    void testHandleGeneralException() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/unexpected");

        Exception ex = new NullPointerException("Internal null reference");
        ResponseEntity<ErrorResponse> response = handler.handleGeneralException(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INTERNAL_SERVER_ERROR", response.getBody().getErrorCode());
        assertFalse(response.getBody().getMessage().contains("NullPointerException"));
        assertEquals("An unexpected error occurred. Please try again later.", response.getBody().getMessage());
    }
}
