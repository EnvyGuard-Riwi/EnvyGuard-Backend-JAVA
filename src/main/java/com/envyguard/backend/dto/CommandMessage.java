package com.envyguard.backend.dto;

import com.envyguard.backend.entity.Command;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for command messages sent to RabbitMQ.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommandMessage {

    private Long commandId;
    private String computerName;
    private Command.CommandType commandType;
    private String parameters;
    private LocalDateTime timestamp;
}

