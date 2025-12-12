package com.envyguard.backend.dto;

import com.envyguard.backend.entity.Severity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class IncidentRequest {

    @NotBlank(message = "Description is required")
    @Size(min = 10, message = "Description must have at least 10 characters")
    private String description;

    @NotNull(message = "Severity is required")
    private Severity severity;
}
