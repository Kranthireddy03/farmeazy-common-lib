package com.farmeazy.common.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "decryption_audit_logs", indexes = {
    @Index(name = "idx_decryption_log_actor", columnList = "actor_user_id"),
    @Index(name = "idx_decryption_log_record", columnList = "audit_record_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DecryptionAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "actor_user_id", nullable = false)
    private Long actorUserId;

    @Column(name = "actor_email", nullable = false, length = 255)
    private String actorEmail;

    @Column(name = "audit_record_id", nullable = false, length = 100)
    private String auditRecordId;

    @Column(name = "field_requested", nullable = false, length = 50)
    private String fieldRequested;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Column(name = "ticket_reference", length = 100)
    private String ticketReference;

    @Column(name = "correlation_id", length = 100)
    private String correlationId;

    @Column(nullable = false)
    private Boolean success;

    @Column(name = "permission_id")
    private Long permissionId;

    @Column(name = "source_ip", length = 50)
    private String sourceIp;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}
