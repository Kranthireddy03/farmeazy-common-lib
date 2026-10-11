package com.farmeazy.common.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Map;

public final class AppLogger {

    private static final Logger log = LoggerFactory.getLogger("com.farmeazy.application");
    private static volatile String serviceName = "farmeazy-service";

    private AppLogger() {}

    public static void setServiceName(String name) {
        if (name != null && !name.isBlank()) {
            serviceName = name;
        }
    }

    public static String getServiceName() {
        return serviceName;
    }

    public static void info(LogEvent event, String message) {
        log(LogLevel.INFO, event, message, null, null, null);
    }

    public static void info(LogEvent event, String message, String details) {
        log(LogLevel.INFO, event, message, details, null, null);
    }

    public static void info(LogEvent event, String message, String details, Long durationMs) {
        log(LogLevel.INFO, event, message, details, durationMs, null);
    }

    public static void debug(LogEvent event, String message) {
        log(LogLevel.DEBUG, event, message, null, null, null);
    }

    public static void debug(LogEvent event, String message, String details) {
        log(LogLevel.DEBUG, event, message, details, null, null);
    }

    public static void warn(LogEvent event, String message) {
        log(LogLevel.WARN, event, message, null, null, null);
    }

    public static void warn(LogEvent event, String message, String details) {
        log(LogLevel.WARN, event, message, details, null, null);
    }

    public static void error(LogEvent event, String message) {
        log(LogLevel.ERROR, event, message, null, null, null);
    }

    public static void error(LogEvent event, String message, String details) {
        log(LogLevel.ERROR, event, message, details, null, null);
    }

    public static void error(LogEvent event, String message, Throwable throwable) {
        log(LogLevel.ERROR, event, message, throwable != null ? throwable.getMessage() : null, null, throwable);
    }

    public static void error(LogEvent event, String message, String details, Throwable throwable) {
        log(LogLevel.ERROR, event, message, details, null, throwable);
    }

    public static void logDbOperation(String operation, String entity, Long id, Long durationMs, String message) {
        LogEvent event = "INSERT".equalsIgnoreCase(operation) ? LogEvent.DB_INSERT_SUCCESS :
                         "UPDATE".equalsIgnoreCase(operation) ? LogEvent.DB_UPDATE_SUCCESS :
                         "DELETE".equalsIgnoreCase(operation) ? LogEvent.DB_DELETE_SUCCESS : LogEvent.DB_READ_SUCCESS;
        String details = "entity=" + entity + (id != null ? " id=" + id : "");
        log(LogLevel.INFO, event, message, details, durationMs, null);
    }

    public static void logDbFailure(String operation, String entity, String error, Long durationMs) {
        String details = "entity=" + entity + " operation=" + operation + " error=" + error;
        log(LogLevel.ERROR, LogEvent.DB_OPERATION_FAILED, "Database operation failed", details, durationMs, null);
    }

    private static void log(LogLevel level, LogEvent event, String message, String details, Long durationMs, Throwable throwable) {
        Caller caller = resolveCaller();
        String requestId = MDC.get("correlationId");
        if (requestId == null || requestId.isBlank()) {
            requestId = "N/A";
        }

        String sanitizedMsg = SensitiveDataMasker.maskMessage(message);
        String sanitizedDetails = details != null ? SensitiveDataMasker.maskMessage(details) : null;

        StringBuilder sb = new StringBuilder();
        sb.append("service=").append(serviceName)
          .append(" class=").append(caller.simpleClassName)
          .append(" method=").append(caller.methodName)
          .append(" line=").append(caller.lineNumber)
          .append(" requestId=").append(requestId)
          .append(" event=").append(event != null ? event.name() : "GENERAL")
          .append(" message=\"").append(sanitizedMsg).append("\"");

        if (sanitizedDetails != null && !sanitizedDetails.isBlank()) {
            sb.append(" details=\"").append(sanitizedDetails).append("\"");
        }

        if (durationMs != null) {
            sb.append(" durationMs=").append(durationMs);
        }

        String logOutput = sb.toString();

        switch (level) {
            case TRACE -> log.trace(logOutput);
            case DEBUG -> log.debug(logOutput);
            case INFO -> log.info(logOutput);
            case WARN -> log.warn(logOutput);
            case ERROR -> {
                if (throwable != null) {
                    log.error(logOutput, throwable);
                } else {
                    log.error(logOutput);
                }
            }
        }
    }

    private enum LogLevel {
        TRACE, DEBUG, INFO, WARN, ERROR
    }

    private static final class Caller {
        String simpleClassName = "Unknown";
        String methodName = "unknown";
        int lineNumber = 0;
    }

    private static Caller resolveCaller() {
        Caller caller = new Caller();
        StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).walk(frames -> {
            frames.forEach(frame -> {
                String cn = frame.getClassName();
                if (caller.lineNumber == 0
                        && !cn.startsWith("com.farmeazy.common.logging")
                        && !cn.startsWith("java.lang")
                        && !cn.startsWith("jdk.")
                        && !cn.startsWith("org.springframework")) {
                    caller.simpleClassName = frame.getDeclaringClass().getSimpleName();
                    caller.methodName = frame.getMethodName();
                    caller.lineNumber = frame.getLineNumber();
                }
            });
            return null;
        });
        return caller;
    }
}
