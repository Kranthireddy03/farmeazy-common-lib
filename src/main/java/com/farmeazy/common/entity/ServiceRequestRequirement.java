package com.farmeazy.common.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "service_request_requirements", indexes = {
    @Index(name = "idx_srr_service_request", columnList = "service_request_id")
})
public class ServiceRequestRequirement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "service_request_id", nullable = false, length = 50)
    private String serviceRequestId;

    @Column(name = "requirement_type", nullable = false, length = 100)
    private String requirementType;

    @Column(nullable = false, length = 50)
    private String status = "PENDING"; // PENDING, IN_REVIEW, COMPLETED, REJECTED

    @Column(name = "requirement_payload", columnDefinition = "TEXT")
    private String requirementPayload; // JSON structure for context

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "completed_by")
    private Long completedBy; // User ID who completed/approved

    @Column(name = "audit_trace_id", length = 100)
    private String auditTraceId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public ServiceRequestRequirement() {
    }

    public ServiceRequestRequirement(Long id, String serviceRequestId, String requirementType,
                                     String status, String requirementPayload, LocalDateTime completedAt,
                                     Long completedBy, String auditTraceId, LocalDateTime createdAt,
                                     LocalDateTime updatedAt) {
        this.id = id;
        this.serviceRequestId = serviceRequestId;
        this.requirementType = requirementType;
        this.status = status != null ? status : "PENDING";
        this.requirementPayload = requirementPayload;
        this.completedAt = completedAt;
        this.completedBy = completedBy;
        this.auditTraceId = auditTraceId;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String serviceRequestId;
        private String requirementType;
        private String status = "PENDING";
        private String requirementPayload;
        private LocalDateTime completedAt;
        private Long completedBy;
        private String auditTraceId;
        private LocalDateTime createdAt = LocalDateTime.now();
        private LocalDateTime updatedAt = LocalDateTime.now();

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder serviceRequestId(String serviceRequestId) {
            this.serviceRequestId = serviceRequestId;
            return this;
        }

        public Builder requirementType(String requirementType) {
            this.requirementType = requirementType;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder requirementPayload(String requirementPayload) {
            this.requirementPayload = requirementPayload;
            return this;
        }

        public Builder completedAt(LocalDateTime completedAt) {
            this.completedAt = completedAt;
            return this;
        }

        public Builder completedBy(Long completedBy) {
            this.completedBy = completedBy;
            return this;
        }

        public Builder auditTraceId(String auditTraceId) {
            this.auditTraceId = auditTraceId;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public ServiceRequestRequirement build() {
            return new ServiceRequestRequirement(id, serviceRequestId, requirementType, status,
                    requirementPayload, completedAt, completedBy, auditTraceId, createdAt, updatedAt);
        }
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = "PENDING";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getServiceRequestId() {
        return serviceRequestId;
    }

    public void setServiceRequestId(String serviceRequestId) {
        this.serviceRequestId = serviceRequestId;
    }

    public String getRequirementType() {
        return requirementType;
    }

    public void setRequirementType(String requirementType) {
        this.requirementType = requirementType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRequirementPayload() {
        return requirementPayload;
    }

    public void setRequirementPayload(String requirementPayload) {
        this.requirementPayload = requirementPayload;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public Long getCompletedBy() {
        return completedBy;
    }

    public void setCompletedBy(Long completedBy) {
        this.completedBy = completedBy;
    }

    public String getAuditTraceId() {
        return auditTraceId;
    }

    public void setAuditTraceId(String auditTraceId) {
        this.auditTraceId = auditTraceId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceRequestRequirement that = (ServiceRequestRequirement) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ServiceRequestRequirement{" +
                "id=" + id +
                ", serviceRequestId='" + serviceRequestId + '\'' +
                ", requirementType='" + requirementType + '\'' +
                ", status='" + status + '\'' +
                ", completedAt=" + completedAt +
                ", completedBy=" + completedBy +
                '}';
    }
}
