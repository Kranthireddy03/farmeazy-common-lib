package com.farmeazy.common.logging;

import com.farmeazy.common.audit.crypto.AuditPayloadEncryptor;
import com.farmeazy.common.audit.repository.ApiAuditTraceRepository;
import com.farmeazy.common.entity.ApiAuditTrace;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 15)
@RequiredArgsConstructor
public class ApiAuditPersistenceFilter extends OncePerRequestFilter {

    private final ApiAuditTraceRepository auditRepository;
    private final AuditPayloadEncryptor encryptor;

    @Value("${spring.application.name:UNKNOWN_SERVICE}")
    private String applicationName;

    @Value("${app.audit.payload.max-size-bytes:1048576}")
    private int maxPayloadSizeBytes;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String contentType = request.getContentType();

        // Skip non-business paths and WebSockets/SSE/File uploads
        if (path.startsWith("/actuator") || path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs") ||
            path.startsWith("/ws") || path.startsWith("/websocket") || path.contains("/download")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (contentType != null && (contentType.toLowerCase().contains("multipart") || 
                                    contentType.toLowerCase().contains("octet-stream") || 
                                    contentType.toLowerCase().contains("text/event-stream"))) {
            filterChain.doFilter(request, response);
            return;
        }

        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        long startTime = System.currentTimeMillis();
        
        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            persistAudit(requestWrapper, responseWrapper, duration);
            responseWrapper.copyBodyToResponse();
        }
    }

    private void persistAudit(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response, long duration) {
        try {
            String requestBody = "";
            String responseBody = "";

            if (request.getContentAsByteArray().length > maxPayloadSizeBytes) {
                requestBody = "{\"payloadCaptured\":false, \"payloadOmittedReason\":\"SIZE_LIMIT\"}";
            } else {
                requestBody = new String(request.getContentAsByteArray(), StandardCharsets.UTF_8);
            }

            if (response.getContentAsByteArray().length > maxPayloadSizeBytes) {
                responseBody = "{\"payloadCaptured\":false, \"payloadOmittedReason\":\"SIZE_LIMIT\"}";
            } else {
                responseBody = new String(response.getContentAsByteArray(), StandardCharsets.UTF_8);
            }

            // 1. Redact
            String redactedRequest = SecretRedactor.redact(requestBody);
            String redactedResponse = SecretRedactor.redact(responseBody);

            // 2. Encrypt
            AuditPayloadEncryptor.EncryptedResult reqResult = encryptor.encrypt(redactedRequest);
            AuditPayloadEncryptor.EncryptedResult resResult = encryptor.encrypt(redactedResponse);

            // 3. Build and Save
            String correlationId = request.getHeader("X-Correlation-ID");
            if (correlationId == null || correlationId.isBlank()) {
                correlationId = UUID.randomUUID().toString();
            }
            String traceId = UUID.randomUUID().toString();

            // Simple user extraction (ideally from SecurityContext)
            Long userId = null;
            if (request.getUserPrincipal() != null) {
                try {
                    userId = Long.valueOf(request.getUserPrincipal().getName());
                } catch (NumberFormatException ignored) {}
            }

            String serviceRequestId = request.getHeader("X-Internal-Service-Request-Id");
            String supportTicketId = request.getHeader("X-Internal-Support-Ticket-Id");

            ApiAuditTrace trace = ApiAuditTrace.builder()
                    .traceId(traceId)
                    .correlationId(correlationId)
                    .serviceRequestId(serviceRequestId)
                    .supportTicketId(supportTicketId)
                    .serviceName(applicationName)
                    .endpoint(request.getRequestURI())
                    .method(request.getMethod())
                    .statusCode(response.getStatus())
                    .durationMs(duration)
                    .requestPayload(reqResult != null ? reqResult.getEncryptedPayload() : null)
                    .responsePayload(resResult != null ? resResult.getEncryptedPayload() : null)
                    .isEncrypted(true)
                    .keyVersion(reqResult != null ? reqResult.getKeyVersion() : (resResult != null ? resResult.getKeyVersion() : null))
                    .userId(userId)
                    .timestamp(LocalDateTime.now())
                    // Retention expires at (7 days)
                    .expiresAt(LocalDateTime.now().plusDays(7))
                    .build();

            auditRepository.save(trace);
            
        } catch (Exception e) {
            // Silently fail to not break business logic, just log
            logger.error("Failed to persist API Audit Trace", e);
        }
    }
}
