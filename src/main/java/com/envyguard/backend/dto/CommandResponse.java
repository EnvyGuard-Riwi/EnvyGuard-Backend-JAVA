package com.envyguard.backend.dto;

import com.envyguard.backend.entity.Command;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for command responses received from C# agents via RabbitMQ.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommandResponse {

    private Long commandId;
    private String computerName;
    private Command.CommandStatus status;
    private String resultMessage;
    private LocalDateTime executedAt;
}

