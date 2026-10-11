package com.farmeazy.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceRequestRequirementDto {
    private Long id;
    private String serviceRequestId;
    private String requirementType;
    private String status;
    private String requirementPayload;
    private LocalDateTime completedAt;
    private Long completedBy;
    private String auditTraceId;
}
