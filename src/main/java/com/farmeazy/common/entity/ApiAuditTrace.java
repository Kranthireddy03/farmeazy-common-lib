package com.farmeazy.common.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "api_audit_traces", indexes = {
    @Index(name = "idx_api_audit_service", columnList = "service_name"),
    @Index(name = "idx_api_audit_user", columnList = "user_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiAuditTrace {

    @Id
    @Column(name = "trace_id", length = 100)
    private String traceId;

    @Column(name = "correlation_id", length = 100)
    private String correlationId;

    @Column(name = "service_request_id", length = 100)
    private String serviceRequestId;

    @Column(name = "support_ticket_id", length = 100)
    private String supportTicketId;

    @Column(name = "service_name", nullable = false, length = 100)
    private String serviceName;

    @Column(nullable = false, length = 255)
    private String endpoint;

    @Column(nullable = false, length = 20)
    private String method;

    @Column(name = "status_code", nullable = false)
    private Integer statusCode;

    @Column(name = "duration_ms", nullable = false)
    private Long durationMs;

    @Column(name = "request_payload", columnDefinition = "TEXT")
    private String requestPayload; // masked

    @Column(name = "response_payload", columnDefinition = "TEXT")
    private String responsePayload; // masked and encrypted

    @Column(name = "key_version", length = 50)
    private String keyVersion;

    @Column(name = "is_encrypted", nullable = false)
    @Builder.Default
    private Boolean isEncrypted = false;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}
