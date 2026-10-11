package com.farmeazy.common.logging;

import ch.qos.logback.classic.pattern.ThrowableProxyConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

/** Prevents exception messages from bypassing the log message masker. */
public final class MaskedExceptionConverter extends ThrowableProxyConverter {
    @Override
    public String convert(ILoggingEvent event) {
        return SensitiveDataMasker.maskMessage(super.convert(event));
    }
}
