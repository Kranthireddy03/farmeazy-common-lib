package com.farmeazy.common.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "audit_decryption_permissions", indexes = {
    @Index(name = "idx_audit_perm_user", columnList = "user_id"),
    @Index(name = "idx_audit_perm_status", columnList = "status")
})
public class AuditDecryptionPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "granted_by", nullable = false)
    private Long grantedBy;

    @Column(name = "granted_at", nullable = false, updatable = false)
    private LocalDateTime grantedAt = LocalDateTime.now();

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    @Column(name = "revoked_by")
    private Long revokedBy;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Column(nullable = false, length = 20)
    private String status = "ACTIVE"; // ACTIVE, EXPIRED, REVOKED

    public AuditDecryptionPermission() {
    }

    public AuditDecryptionPermission(Long id, Long userId, Long grantedBy, LocalDateTime grantedAt,
                                     LocalDateTime expiresAt, LocalDateTime revokedAt, Long revokedBy,
                                     String reason, String status) {
        this.id = id;
        this.userId = userId;
        this.grantedBy = grantedBy;
        this.grantedAt = grantedAt != null ? grantedAt : LocalDateTime.now();
        this.expiresAt = expiresAt;
        this.revokedAt = revokedAt;
        this.revokedBy = revokedBy;
        this.reason = reason;
        this.status = status != null ? status : "ACTIVE";
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Long userId;
        private Long grantedBy;
        private LocalDateTime grantedAt = LocalDateTime.now();
        private LocalDateTime expiresAt;
        private LocalDateTime revokedAt;
        private Long revokedBy;
        private String reason;
        private String status = "ACTIVE";

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder grantedBy(Long grantedBy) {
            this.grantedBy = grantedBy;
            return this;
        }

        public Builder grantedAt(LocalDateTime grantedAt) {
            this.grantedAt = grantedAt;
            return this;
        }

        public Builder expiresAt(LocalDateTime expiresAt) {
            this.expiresAt = expiresAt;
            return this;
        }

        public Builder revokedAt(LocalDateTime revokedAt) {
            this.revokedAt = revokedAt;
            return this;
        }

        public Builder revokedBy(Long revokedBy) {
            this.revokedBy = revokedBy;
            return this;
        }

        public Builder reason(String reason) {
            this.reason = reason;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public AuditDecryptionPermission build() {
            return new AuditDecryptionPermission(id, userId, grantedBy, grantedAt, expiresAt, revokedAt, revokedBy, reason, status);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getGrantedBy() {
        return grantedBy;
    }

    public void setGrantedBy(Long grantedBy) {
        this.grantedBy = grantedBy;
    }

    public LocalDateTime getGrantedAt() {
        return grantedAt;
    }

    public void setGrantedAt(LocalDateTime grantedAt) {
        this.grantedAt = grantedAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public LocalDateTime getRevokedAt() {
        return revokedAt;
    }

    public void setRevokedAt(LocalDateTime revokedAt) {
        this.revokedAt = revokedAt;
    }

    public Long getRevokedBy() {
        return revokedBy;
    }

    public void setRevokedBy(Long revokedBy) {
        this.revokedBy = revokedBy;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuditDecryptionPermission that = (AuditDecryptionPermission) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "AuditDecryptionPermission{" +
                "id=" + id +
                ", userId=" + userId +
                ", grantedBy=" + grantedBy +
                ", grantedAt=" + grantedAt +
                ", expiresAt=" + expiresAt +
                ", status='" + status + '\'' +
                '}';
    }
}
