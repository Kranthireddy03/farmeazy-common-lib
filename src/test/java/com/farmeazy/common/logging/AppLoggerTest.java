package com.farmeazy.common.logging;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import static org.junit.jupiter.api.Assertions.*;

class AppLoggerTest {

    @Test
    @DisplayName("Verify AppLogger service name and logging without throwing exceptions")
    void testAppLoggerOperations() {
        AppLogger.setServiceName("test-service");
        assertEquals("test-service", AppLogger.getServiceName());

        MDC.put("correlationId", "test-corr-12345");

        assertDoesNotThrow(() -> {
            AppLogger.info(LogEvent.SYSTEM_STARTUP, "Service startup initiated");
            AppLogger.info(LogEvent.AUTHENTICATION_SUCCESS, "User login successful", "userId=123", 45L);
            AppLogger.debug(LogEvent.DATABASE_QUERY, "Executing query for product");
            AppLogger.warn(LogEvent.VALIDATION_FAILED, "Input validation error", "field=email");
            AppLogger.error(LogEvent.ERROR, "Unhandled system error occurred", new RuntimeException("Test Exception"));
            AppLogger.logDbOperation("INSERT", "Product", 101L, 12L, "Product inserted");
            AppLogger.logDbFailure("UPDATE", "Product", "OptimisticLockException", 8L);
        });

        MDC.clear();
    }
}
