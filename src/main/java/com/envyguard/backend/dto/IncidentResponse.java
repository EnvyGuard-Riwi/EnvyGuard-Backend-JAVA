package com.envyguard.backend.dto;

import com.envyguard.backend.entity.IncidentStatus;
import com.envyguard.backend.entity.Severity;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class IncidentResponse {
    private Long id;
    private String description;
    private Severity severity;
    private IncidentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}
