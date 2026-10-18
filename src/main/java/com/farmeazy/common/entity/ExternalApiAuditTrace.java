package com.farmeazy.common.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "external_api_audit_traces", indexes = {
    @Index(name = "idx_ext_audit_service", columnList = "service_name"),
    @Index(name = "idx_ext_audit_provider", columnList = "provider_name")
})
public class ExternalApiAuditTrace {

    @Id
    @Column(name = "trace_id", length = 100)
    private String traceId;

    @Column(name = "correlation_id", length = 100)
    private String correlationId;

    @Column(name = "service_name", nullable = false, length = 100)
    private String serviceName;

    @Column(name = "provider_name", nullable = false, length = 100)
    private String providerName;

    @Column(nullable = false, length = 255)
    private String endpoint;

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
    private Boolean isEncrypted = false;

    @Column(name = "reference_id", length = 100)
    private String referenceId; // External provider's ID (e.g. razorpay_order_id)

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    public ExternalApiAuditTrace() {
    }

    public ExternalApiAuditTrace(String traceId, String correlationId, String serviceName,
                                 String providerName, String endpoint, Integer statusCode,
                                 Long durationMs, String requestPayload, String responsePayload,
                                 String keyVersion, Boolean isEncrypted, String referenceId,
                                 LocalDateTime expiresAt, LocalDateTime timestamp) {
        this.traceId = traceId;
        this.correlationId = correlationId;
        this.serviceName = serviceName;
        this.providerName = providerName;
        this.endpoint = endpoint;
        this.statusCode = statusCode;
        this.durationMs = durationMs;
        this.requestPayload = requestPayload;
        this.responsePayload = responsePayload;
        this.keyVersion = keyVersion;
        this.isEncrypted = isEncrypted != null ? isEncrypted : false;
        this.referenceId = referenceId;
        this.expiresAt = expiresAt;
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String traceId;
        private String correlationId;
        private String serviceName;
        private String providerName;
        private String endpoint;
        private Integer statusCode;
        private Long durationMs;
        private String requestPayload;
        private String responsePayload;
        private String keyVersion;
        private Boolean isEncrypted = false;
        private String referenceId;
        private LocalDateTime expiresAt;
        private LocalDateTime timestamp = LocalDateTime.now();

        public Builder traceId(String traceId) {
            this.traceId = traceId;
            return this;
        }

        public Builder correlationId(String correlationId) {
            this.correlationId = correlationId;
            return this;
        }

        public Builder serviceName(String serviceName) {
            this.serviceName = serviceName;
            return this;
        }

        public Builder providerName(String providerName) {
            this.providerName = providerName;
            return this;
        }

        public Builder endpoint(String endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        public Builder statusCode(Integer statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public Builder durationMs(Long durationMs) {
            this.durationMs = durationMs;
            return this;
        }

        public Builder requestPayload(String requestPayload) {
            this.requestPayload = requestPayload;
            return this;
        }

        public Builder responsePayload(String responsePayload) {
            this.responsePayload = responsePayload;
            return this;
        }

        public Builder keyVersion(String keyVersion) {
            this.keyVersion = keyVersion;
            return this;
        }

        public Builder isEncrypted(Boolean isEncrypted) {
            this.isEncrypted = isEncrypted;
            return this;
        }

        public Builder referenceId(String referenceId) {
            this.referenceId = referenceId;
            return this;
        }

        public Builder expiresAt(LocalDateTime expiresAt) {
            this.expiresAt = expiresAt;
            return this;
        }

        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public ExternalApiAuditTrace build() {
            return new ExternalApiAuditTrace(traceId, correlationId, serviceName, providerName,
                    endpoint, statusCode, durationMs, requestPayload, responsePayload,
                    keyVersion, isEncrypted, referenceId, expiresAt, timestamp);
        }
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public Long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(Long durationMs) {
        this.durationMs = durationMs;
    }

    public String getRequestPayload() {
        return requestPayload;
    }

    public void setRequestPayload(String requestPayload) {
        this.requestPayload = requestPayload;
    }

    public String getResponsePayload() {
        return responsePayload;
    }

    public void setResponsePayload(String responsePayload) {
        this.responsePayload = responsePayload;
    }

    public String getKeyVersion() {
        return keyVersion;
    }

    public void setKeyVersion(String keyVersion) {
        this.keyVersion = keyVersion;
    }

    public Boolean getIsEncrypted() {
        return isEncrypted;
    }

    public void setIsEncrypted(Boolean isEncrypted) {
        this.isEncrypted = isEncrypted;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
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
        ExternalApiAuditTrace that = (ExternalApiAuditTrace) o;
        return Objects.equals(traceId, that.traceId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(traceId);
    }

    @Override
    public String toString() {
        return "ExternalApiAuditTrace{" +
                "traceId='" + traceId + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", providerName='" + providerName + '\'' +
                ", endpoint='" + endpoint + '\'' +
                ", statusCode=" + statusCode +
                ", durationMs=" + durationMs +
                ", timestamp=" + timestamp +
                '}';
    }
}
