package com.farmeazy.common.logging;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

/** Masks formatted SLF4J arguments at the appender boundary, including direct logger calls. */
public final class MaskedMessageConverter extends ClassicConverter {
    @Override
    public String convert(ILoggingEvent event) {
        return SensitiveDataMasker.maskMessage(event.getFormattedMessage());
    }
}
