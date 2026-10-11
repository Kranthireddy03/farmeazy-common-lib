package com.farmeazy.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceRequestDto {
    private String id;
    private Long userId;
    private String requestType;
    private String status;
    private List<ServiceRequestRequirementDto> requirements;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
