package com.farmeazy.common.audit.service;

import java.util.Objects;

public class AuditDecryptionResult {
    private String auditId;
    private String correlationId;
    private String serviceName;
    private String endpoint;
    private String timestamp;
    private String request;
    private String response;

    public AuditDecryptionResult() {
    }

    public AuditDecryptionResult(String auditId, String correlationId, String serviceName,
                                 String endpoint, String timestamp, String request, String response) {
        this.auditId = auditId;
        this.correlationId = correlationId;
        this.serviceName = serviceName;
        this.endpoint = endpoint;
        this.timestamp = timestamp;
        this.request = request;
        this.response = response;
    }

    public String getAuditId() {
        return auditId;
    }

    public void setAuditId(String auditId) {
        this.auditId = auditId;
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

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuditDecryptionResult that = (AuditDecryptionResult) o;
        return Objects.equals(auditId, that.auditId) &&
                Objects.equals(correlationId, that.correlationId) &&
                Objects.equals(serviceName, that.serviceName) &&
                Objects.equals(endpoint, that.endpoint) &&
                Objects.equals(timestamp, that.timestamp) &&
                Objects.equals(request, that.request) &&
                Objects.equals(response, that.response);
    }

    @Override
    public int hashCode() {
        return Objects.hash(auditId, correlationId, serviceName, endpoint, timestamp, request, response);
    }

    @Override
    public String toString() {
        return "AuditDecryptionResult{" +
                "auditId='" + auditId + '\'' +
                ", correlationId='" + correlationId + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", endpoint='" + endpoint + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
