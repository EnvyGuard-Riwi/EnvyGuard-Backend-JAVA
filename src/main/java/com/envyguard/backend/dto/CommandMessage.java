package com.envyguard.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for command messages sent to RabbitMQ.
 * Estructura: { "action": "shutdown", "targetIp": "192.168.1.50", "parameters": "" }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommandMessage {

    private Long commandId;
    private String computerName;
    private String action;
    private String targetIp;
    private String macAddress;
    private String parameters;
    private LocalDateTime timestamp;
}

