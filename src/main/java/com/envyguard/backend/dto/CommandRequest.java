package com.envyguard.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para solicitudes de comandos remotos.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommandRequest {

    @NotBlank(message = "Computer name is required")
    private String computerName;

    @NotBlank(message = "Action is required")
    private String action;

    private String targetIp;

    private String macAddress;

    private String parameters;
}
