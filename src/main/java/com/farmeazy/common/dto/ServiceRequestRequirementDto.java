package com.farmeazy.common.dto;

import java.time.LocalDateTime;
import java.util.Objects;

public class ServiceRequestRequirementDto {
    private Long id;
    private String serviceRequestId;
    private String requirementType;
    private String status;
    private String requirementPayload;
    private LocalDateTime completedAt;
    private Long completedBy;
    private String auditTraceId;

    public ServiceRequestRequirementDto() {
    }

    public ServiceRequestRequirementDto(Long id, String serviceRequestId, String requirementType,
                                        String status, String requirementPayload, LocalDateTime completedAt,
                                        Long completedBy, String auditTraceId) {
        this.id = id;
        this.serviceRequestId = serviceRequestId;
        this.requirementType = requirementType;
        this.status = status;
        this.requirementPayload = requirementPayload;
        this.completedAt = completedAt;
        this.completedBy = completedBy;
        this.auditTraceId = auditTraceId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String serviceRequestId;
        private String requirementType;
        private String status;
        private String requirementPayload;
        private LocalDateTime completedAt;
        private Long completedBy;
        private String auditTraceId;

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

        public ServiceRequestRequirementDto build() {
            return new ServiceRequestRequirementDto(id, serviceRequestId, requirementType, status,
                    requirementPayload, completedAt, completedBy, auditTraceId);
        }
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceRequestRequirementDto that = (ServiceRequestRequirementDto) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(serviceRequestId, that.serviceRequestId) &&
                Objects.equals(requirementType, that.requirementType) &&
                Objects.equals(status, that.status) &&
                Objects.equals(requirementPayload, that.requirementPayload) &&
                Objects.equals(completedAt, that.completedAt) &&
                Objects.equals(completedBy, that.completedBy) &&
                Objects.equals(auditTraceId, that.auditTraceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, serviceRequestId, requirementType, status, requirementPayload, completedAt, completedBy, auditTraceId);
    }

    @Override
    public String toString() {
        return "ServiceRequestRequirementDto{" +
                "id=" + id +
                ", serviceRequestId='" + serviceRequestId + '\'' +
                ", requirementType='" + requirementType + '\'' +
                ", status='" + status + '\'' +
                ", completedAt=" + completedAt +
                ", completedBy=" + completedBy +
                '}';
    }
}
