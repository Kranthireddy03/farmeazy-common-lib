package com.farmeazy.common.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class ServiceRequestDto {
    private String id;
    private Long userId;
    private String requestType;
    private String status;
    private List<ServiceRequestRequirementDto> requirements;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ServiceRequestDto() {
    }

    public ServiceRequestDto(String id, Long userId, String requestType, String status,
                             List<ServiceRequestRequirementDto> requirements,
                             LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.requestType = requestType;
        this.status = status;
        this.requirements = requirements;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private Long userId;
        private String requestType;
        private String status;
        private List<ServiceRequestRequirementDto> requirements;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder requestType(String requestType) {
            this.requestType = requestType;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder requirements(List<ServiceRequestRequirementDto> requirements) {
            this.requirements = requirements;
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

        public ServiceRequestDto build() {
            return new ServiceRequestDto(id, userId, requestType, status, requirements, createdAt, updatedAt);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ServiceRequestRequirementDto> getRequirements() {
        return requirements;
    }

    public void setRequirements(List<ServiceRequestRequirementDto> requirements) {
        this.requirements = requirements;
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
        ServiceRequestDto that = (ServiceRequestDto) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(requestType, that.requestType) &&
                Objects.equals(status, that.status) &&
                Objects.equals(requirements, that.requirements) &&
                Objects.equals(createdAt, that.createdAt) &&
                Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, requestType, status, requirements, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "ServiceRequestDto{" +
                "id='" + id + '\'' +
                ", userId=" + userId +
                ", requestType='" + requestType + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
