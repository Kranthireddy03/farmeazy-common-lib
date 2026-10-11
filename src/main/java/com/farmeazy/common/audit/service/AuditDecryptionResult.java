package com.farmeazy.common.audit.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditDecryptionResult {
    private String auditId;
    private String correlationId;
    private String serviceName;
    private String endpoint;
    private String timestamp;
    private String request;
    private String response;
}
