package com.farmeazy.common.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "decryption_audit_logs", indexes = {
    @Index(name = "idx_decryption_log_actor", columnList = "actor_user_id"),
    @Index(name = "idx_decryption_log_record", columnList = "audit_record_id")
})
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
    private LocalDateTime timestamp = LocalDateTime.now();

    public DecryptionAuditLog() {
    }

    public DecryptionAuditLog(Long id, Long actorUserId, String actorEmail, String auditRecordId,
                              String fieldRequested, String reason, String ticketReference,
                              String correlationId, Boolean success, Long permissionId,
                              String sourceIp, String userAgent, LocalDateTime timestamp) {
        this.id = id;
        this.actorUserId = actorUserId;
        this.actorEmail = actorEmail;
        this.auditRecordId = auditRecordId;
        this.fieldRequested = fieldRequested;
        this.reason = reason;
        this.ticketReference = ticketReference;
        this.correlationId = correlationId;
        this.success = success;
        this.permissionId = permissionId;
        this.sourceIp = sourceIp;
        this.userAgent = userAgent;
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Long actorUserId;
        private String actorEmail;
        private String auditRecordId;
        private String fieldRequested;
        private String reason;
        private String ticketReference;
        private String correlationId;
        private Boolean success;
        private Long permissionId;
        private String sourceIp;
        private String userAgent;
        private LocalDateTime timestamp = LocalDateTime.now();

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder actorUserId(Long actorUserId) {
            this.actorUserId = actorUserId;
            return this;
        }

        public Builder actorEmail(String actorEmail) {
            this.actorEmail = actorEmail;
            return this;
        }

        public Builder auditRecordId(String auditRecordId) {
            this.auditRecordId = auditRecordId;
            return this;
        }

        public Builder fieldRequested(String fieldRequested) {
            this.fieldRequested = fieldRequested;
            return this;
        }

        public Builder reason(String reason) {
            this.reason = reason;
            return this;
        }

        public Builder ticketReference(String ticketReference) {
            this.ticketReference = ticketReference;
            return this;
        }

        public Builder correlationId(String correlationId) {
            this.correlationId = correlationId;
            return this;
        }

        public Builder success(Boolean success) {
            this.success = success;
            return this;
        }

        public Builder permissionId(Long permissionId) {
            this.permissionId = permissionId;
            return this;
        }

        public Builder sourceIp(String sourceIp) {
            this.sourceIp = sourceIp;
            return this;
        }

        public Builder userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public DecryptionAuditLog build() {
            return new DecryptionAuditLog(id, actorUserId, actorEmail, auditRecordId, fieldRequested,
                    reason, ticketReference, correlationId, success, permissionId, sourceIp, userAgent, timestamp);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getActorUserId() {
        return actorUserId;
    }

    public void setActorUserId(Long actorUserId) {
        this.actorUserId = actorUserId;
    }

    public String getActorEmail() {
        return actorEmail;
    }

    public void setActorEmail(String actorEmail) {
        this.actorEmail = actorEmail;
    }

    public String getAuditRecordId() {
        return auditRecordId;
    }

    public void setAuditRecordId(String auditRecordId) {
        this.auditRecordId = auditRecordId;
    }

    public String getFieldRequested() {
        return fieldRequested;
    }

    public void setFieldRequested(String fieldRequested) {
        this.fieldRequested = fieldRequested;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getTicketReference() {
        return ticketReference;
    }

    public void setTicketReference(String ticketReference) {
        this.ticketReference = ticketReference;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Long getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Long permissionId) {
        this.permissionId = permissionId;
    }

    public String getSourceIp() {
        return sourceIp;
    }

    public void setSourceIp(String sourceIp) {
        this.sourceIp = sourceIp;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DecryptionAuditLog that = (DecryptionAuditLog) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "DecryptionAuditLog{" +
                "id=" + id +
                ", actorUserId=" + actorUserId +
                ", actorEmail='" + actorEmail + '\'' +
                ", auditRecordId='" + auditRecordId + '\'' +
                ", success=" + success +
                ", timestamp=" + timestamp +
                '}';
    }
}
