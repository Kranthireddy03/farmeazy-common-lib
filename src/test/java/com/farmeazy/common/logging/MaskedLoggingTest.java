package com.farmeazy.common.logging;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxy;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MaskedLoggingTest {
    @Test void appenderMasksDirectSlf4jArgumentsAndExceptionMessages() {
        var context = new LoggerContext();
        PatternLayout.defaultConverterMap.put("maskedMsg", MaskedMessageConverter.class.getName());
        PatternLayout.defaultConverterMap.put("maskedException", MaskedExceptionConverter.class.getName());
        var layout = new PatternLayout();
        layout.setContext(context);
        layout.setPattern("%maskedMsg%n%maskedException");
        layout.start();
        var event = new LoggingEvent();
        event.setMessage("refreshToken={} bankAccountNumber={} JSON: {\"otp\":\"654321\"}");
        event.setArgumentArray(new Object[]{"sensitive-refresh-value", "123456789012"});
        event.setThrowableProxy(new ThrowableProxy(new IllegalArgumentException("password=exception-secret")));
        String output = layout.doLayout(event);
        assertFalse(output.contains("sensitive-refresh-value"));
        assertFalse(output.contains("123456789012"));
        assertFalse(output.contains("654321"));
        assertFalse(output.contains("exception-secret"));
        assertTrue(output.contains("IllegalArgumentException"));
        layout.stop();
        context.stop();
    }
}
